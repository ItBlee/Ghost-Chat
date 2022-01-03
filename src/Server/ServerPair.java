package Server;

import Services.DTO;
import Services.Header;
import Services.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class ServerPair extends Thread implements Runnable{
    private final Worker parent;
    private boolean isAllowInvite = false;
    private String replyInvite = "";
    private boolean isConfirmInvite = false;
    private String replyConfirm = "";

    public ServerPair(Worker parent) {
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
                    break;
                }

                //Gửi lệnh "invite_chat_{pairName}" hỏi người dùng có muốn ghép không
                DTO dto = new DTO(Header.INVITE_CHAT_HEADER);
                dto.setData(randomUser.getName());
                parent.responseHandle(dto);
                while (!isAllowInvite)
                    Thread.sleep(1000);
                //Nếu True tức đồng ý ghép
                if (replyInvite.equals("true")) {
                    //Nếu người đó đã ghép với người khác thì báo lại "invite_chat_{pairName}_fail"
                    if (randomUser.getWorker().isPaired()) {
                        dto = new DTO(Header.INVITE_CHAT_HEADER);
                        dto.setData(randomUser.getName());
                        parent.responseHandle(dto);
                        continue;
                    }

                    //Còn được thì gửi lệnh "accept_chat_myName" đến cho người muốn ghép để hỏi có đồng ý ghép với mình không.
                    dto = new DTO(Header.CONFIRM_CHAT_HEADER);
                    dto.setData(randomUser.getName());
                    parent.responseHandle(dto);
                    while (!isConfirmInvite)
                        Thread.sleep(1000);
                    //Nếu True tức đồng ý ghép
                    if (replyConfirm.equals("true")) {
                        //Thực hiện ghép cặp
                        parent.doPair(randomUser);
                        loadHistoryChat(randomUser);
                        DTO infoDTO = new DTO(Header.USER_INFO_HEADER);
                        infoDTO.setData(JsonParser.getUserInfo(parent.getUser()));
                        parent.responseHandle(infoDTO);
                        infoDTO.setData(JsonParser.getUserInfo(randomUser));
                        randomUser.getWorker().responseHandle(infoDTO);
                        break;
                    }
                    else {
                        //người muốn ghép không đồng ý thì mở khóa và thêm họ vào danh sách từ chối.
                        randomUser.getWorker().unlockPair();
                        parent.getDenied().add(randomUser);
                    }
                }
                //Nếu người dùng không muốn ghép với người server đề xuất thì thêm người đó vào danh sách từ chối.
                else parent.getDenied().add(randomUser);
            }
        } catch (IOException ignored) {} catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Lấy ngẫu nhiên từ danh sách chờ
    public User getRandomFromQueue() {
        User randomUser = null;
        Vector<User> temp = new Vector<>(Server.queue);
        if (temp.isEmpty())
            return null;

        temp.removeAll(parent.getDenied());
        if (!temp.isEmpty()) {
            randomUser = temp.remove(new Random().nextInt(temp.size()));
            if (randomUser.getWorker().isPaired())
                return getRandomFromQueue();
        }
        return randomUser;
    }

    //Lấy lịch sử chat giữa 2 người dùng từ lịch sử của người thực hiện ghép cặp.
    public void loadHistoryChat(User chatWith) throws IOException {
        if (!parent.getHistories().containsKey(chatWith))
            parent.getHistories().put(chatWith, new ArrayList<>());
        if (!chatWith.getWorker().getHistories().containsKey(parent.getUser()))
            chatWith.getWorker().getHistories().put(parent.getUser(), new ArrayList<>());

        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.toJsonTree(parent.getHistories()).getAsJsonArray();
        DTO dto = new DTO(Header.HISTORY_RECOVERY_HEADER);
        dto.setData(jsonArray.toString());
        parent.responseHandle(dto);
    }
}
