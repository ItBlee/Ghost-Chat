package Server;

import Services.DTO;
import Services.Header;
import Services.History;
import Services.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.*;

public class WorkerPair extends Thread implements Runnable{
    private final Worker parent;
    private boolean isAllowInvite = false;
    private String replyInvite = "";
    private boolean isConfirmInvite = false;
    private String replyConfirm = "";

    public WorkerPair(Worker parent) {
        this.parent = parent;
    }

    public void replyInvite(String reply) {
        this.isAllowInvite = true;
        this.replyInvite = reply;
    }

    public void replyConfirm(String reply) {
        this.isConfirmInvite = true;
        this.replyConfirm = reply;
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        try {
            while (true) {
                //Lấy ngẫu nhiên 1 worker trong hàng đợi
                User randomUser = getRandomFromQueue();

                //Nếu không có ai đợi thì hủy tìm kiếm và đưa người dùng này vào hàng chờ
                if (randomUser == null) {
                    Server.queue.add(parent.getUser());
                    System.out.println("Queue: " + Server.queue);
                    parent.getDenied().clear();
                    break;
                }

                //Gửi lệnh "invite_chat_{pairName}" hỏi người dùng có muốn ghép không
                DTO dto = new DTO(Header.INVITE_CHAT_HEADER);
                dto.setData(randomUser.getName());
                parent.responseHandle(dto);

                int waitTime = 0;
                while (!isAllowInvite) {
                    Thread.sleep(1000);
                    waitTime += 1000;
                    if (waitTime == 10000)
                        replyInvite("false");
                }
                //Nếu True tức đồng ý ghép
                if (replyInvite.equals("true")) {
                    //Nếu người đó đã ghép với người khác thì báo lại "invite_chat_{pairName}_fail"
                    if (randomUser.getWorker().isPaired()) {
                        replyFail();
                        continue;
                    }

                    //Còn được thì gửi lệnh "accept_chat_myName" đến cho người muốn ghép để hỏi có đồng ý ghép với mình không.
                    dto = new DTO(Header.CONFIRM_CHAT_HEADER);
                    dto.setData(parent.getUser().getName());
                    dto.setSender(parent.getUser().getUID());
                    randomUser.getWorker().responseHandle(dto);

                    waitTime = 0;
                    while (!isConfirmInvite) {
                        Thread.sleep(1000);
                        waitTime += 1000;
                        if (waitTime == 10000)
                            replyConfirm("false");
                    }
                    //Nếu True tức đồng ý ghép
                    if (replyConfirm.equals("true")) {
                        //Thực hiện ghép cặp
                        doPair(randomUser);
                        sleep(500);
                        loadHistoryChat(randomUser);
                        transferInfo(parent.getUser(), randomUser);
                        break;
                    }
                    else {
                        //người muốn ghép không đồng ý thì mở khóa và thêm họ vào danh sách từ chối.
                        randomUser.getWorker().unlockPair();
                        parent.getDenied().add(randomUser.getName());
                        replyFail();
                    }
                }
                //Nếu người dùng không muốn ghép với người server đề xuất thì thêm người đó vào danh sách từ chối.
                else parent.getDenied().add(randomUser.getName());
            }
        } catch (Exception ignored) {}
    }

    public void replyFail() throws IOException {
        DTO dto = new DTO(Header.INVITE_CHAT_FAIL_HEADER);
        parent.responseHandle(dto);
    }

    //Lấy ngẫu nhiên từ danh sách chờ
    public User getRandomFromQueue() {
        User randomUser = null;
        Vector<User> temp = new Vector<>(Server.queue);
        if (temp.isEmpty())
            return null;

        //Xóa những người đã từ chối.
        temp.removeIf(user -> parent.getDenied().contains(user.getName()));
        if (!temp.isEmpty()) {
            randomUser = temp.remove(new Random().nextInt(temp.size()));
            if (randomUser.getWorker().isPaired())
                return getRandomFromQueue();
        }
        return randomUser;
    }

    //Thực hiện ghép cặp khi cả 2 bên đồng ý
    public void doPair(User withUser) throws IOException {
        Server.queue.remove(withUser);
        System.out.println(Server.queue);
        parent.lockPair();
        parent.pairWith(withUser);
        withUser.getWorker().lockPair();
        withUser.getWorker().pairWith(parent.getUser());
        DTO dto = new DTO(Header.PAIRED_CHAT_HEADER);
        dto.setData(withUser.getName());
        parent.responseHandle(dto);
        dto.setData(parent.getUser().getName());
        withUser.getWorker().responseHandle(dto);
        System.out.println("Paired " + parent.getUser().getName() + " with " + withUser.getName());
    }


    //Lấy lịch sử chat giữa 2 người dùng từ lịch sử của người thực hiện ghép cặp.
    public void loadHistoryChat(User chatWith) throws IOException, IllegalStateException {
        if (!parent.getHistories().containsKey(chatWith))
            parent.getHistories().put(chatWith, new ArrayList<>());
        if (!chatWith.getWorker().getHistories().containsKey(parent.getUser()))
            chatWith.getWorker().getHistories().put(parent.getUser(), new ArrayList<>());

        if (parent.getHistories().get(chatWith).isEmpty()
        ||  chatWith.getWorker().getHistories().get(parent.getUser()).isEmpty())
            return;

        ArrayList<History> restore = new ArrayList<>(parent.getHistories().get(chatWith));
        if (restore.removeAll(chatWith.getWorker().getHistories().get(parent.getUser())))
            restore.addAll(chatWith.getWorker().getHistories().get(parent.getUser()));

        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(restore).getAsJsonArray();
        DTO dto = new DTO(Header.HISTORY_RECOVERY_HEADER);
        dto.setSender(parent.getUser().getUID());
        dto.setData(jsonArray.toString());
        parent.responseHandle(dto);
        chatWith.getWorker().responseHandle(dto);
    }

    public void transferInfo(User user1, User user2) throws IOException {
        DTO infoDTO = new DTO(Header.USER_INFO_HEADER);
        infoDTO.setData(JsonParser.packUserInfo(user2));
        user1.getWorker().responseHandle(infoDTO);
        infoDTO.setData(JsonParser.packUserInfo(user1));
        user2.getWorker().responseHandle(infoDTO);
    }
}
