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

    /**
     * @param reply Đồng ý gửi invite hay không
     */
    public void replyInvite(String reply) {
        this.isAllowInvite = true;
        this.replyInvite = reply;
    }

    /**
     * @param reply Câu trả lời của người được Invite
     */
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

                //Gửi lệnh hỏi người dùng có muốn ghép không
                DTO dto = new DTO(Header.INVITE_CHAT_HEADER);
                dto.setData(randomUser.getWorker().getMyName());
                parent.responseHandle(dto);

                //Chờ phản hồi
                int waitTime = 0;
                while (!isAllowInvite) {
                    Thread.sleep(1000);
                    waitTime += 1000;
                    if (waitTime == 10000)
                        replyInvite("false");
                }
                //Nếu True tức đồng ý ghép
                if (replyInvite.equals("true")) {
                    //Nếu người đó đã ghép với người khác thì báo lại "fail"
                    if (randomUser.getWorker().isPaired()) {
                        replyFail();
                        continue;
                    }

                    //Còn được thì gửi lệnh đến cho người muốn ghép để hỏi có đồng ý ghép với mình không.
                    dto = new DTO(Header.CONFIRM_CHAT_HEADER);
                    dto.setData(parent.getMyName());
                    dto.setSender(parent.getUser().getUID());
                    randomUser.getWorker().responseHandle(dto);

                    //Chờ phản hồi
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
                        parent.getDenied().add(randomUser.getWorker().getMyName());
                        replyFail();
                    }
                }
                //Nếu người dùng không muốn ghép với người server đề xuất thì thêm người đó vào danh sách từ chối.
                else parent.getDenied().add(randomUser.getWorker().getMyName());
            }
        } catch (Exception ignored) {}
    }

    /**
     * Báo bị từ chối
     */
    public void replyFail() throws IOException {
        DTO dto = new DTO(Header.INVITE_CHAT_FAIL_HEADER);
        parent.responseHandle(dto);
    }

    /**
     * Lấy ngẫu nhiên từ danh sách chờ
     */
    public User getRandomFromQueue() {
        User randomUser = null;
        Vector<User> temp = new Vector<>(Server.queue);
        if (temp.isEmpty())
            return null;

        //Xóa những người đã từ chối.
        temp.removeIf(user -> parent.getDenied().contains(user.getWorker().getMyName()));
        if (!temp.isEmpty()) {
            randomUser = temp.remove(new Random().nextInt(temp.size()));
            if (randomUser.getWorker().isPaired())
                return getRandomFromQueue();
        }
        return randomUser;
    }

    /**
     * Thực hiện ghép cặp khi cả 2 bên đồng ý
     * @param withUser - với ai
     */
    public void doPair(User withUser) throws IOException {
        //Xóa khỏi hàng chờ
        Server.queue.remove(withUser);
        System.out.println(Server.queue);
        //lock đối tượng
        parent.lockPair();
        parent.pairWith(withUser);
        withUser.getWorker().lockPair();
        withUser.getWorker().pairWith(parent.getUser());
        //Send xác nhận kết nói
        DTO dto = new DTO(Header.PAIRED_CHAT_HEADER);
        dto.setData(withUser.getWorker().getMyName());
        parent.responseHandle(dto);
        dto.setData(parent.getMyName());
        withUser.getWorker().responseHandle(dto);
        System.out.println("Paired " + parent.getMyName() + " with " + withUser.getWorker().getMyName());
    }

    /**
     * Lấy lịch sử chat giữa 2 người dùng từ lịch sử của người thực hiện ghép cặp.
     * @param chatWith - với ai
     */
    public void loadHistoryChat(User chatWith) throws IOException, IllegalStateException {
        //Khởi tạo nếu chưa tồn tại
        if (!parent.getHistories().containsKey(chatWith))
            parent.getHistories().put(chatWith, new ArrayList<>());
        if (!chatWith.getWorker().getHistories().containsKey(parent.getUser()))
            chatWith.getWorker().getHistories().put(parent.getUser(), new ArrayList<>());

        //Bỏ qua nếu lịch sử 1 bên rỗng.
        if (parent.getHistories().get(chatWith).isEmpty()
        ||  chatWith.getWorker().getHistories().get(parent.getUser()).isEmpty())
            return;

        //Gộp lịch sử 2 bên
        ArrayList<History> restore = new ArrayList<>(parent.getHistories().get(chatWith));
        if (restore.removeAll(chatWith.getWorker().getHistories().get(parent.getUser())))
            restore.addAll(chatWith.getWorker().getHistories().get(parent.getUser()));

        //Format thành json
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(restore).getAsJsonArray();
        DTO dto = new DTO(Header.HISTORY_RECOVERY_HEADER);
        dto.setSender(parent.getUser().getUID());
        dto.setData(jsonArray.toString());

        //Send cho cả 2 đối tượng
        parent.responseHandle(dto);
        chatWith.getWorker().responseHandle(dto);
    }

    /**
     * Trao đổi info giữa 2 đối tượng
     */
    public void transferInfo(User user1, User user2) throws IOException {
        DTO infoDTO = new DTO(Header.USER_INFO_HEADER);
        infoDTO.setData(JsonParser.packUserInfo(user2));
        user1.getWorker().responseHandle(infoDTO);
        infoDTO.setData(JsonParser.packUserInfo(user1));
        user2.getWorker().responseHandle(infoDTO);
    }
}
