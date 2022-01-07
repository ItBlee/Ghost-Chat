package Client;

import ClientGUI.Dialog;
import Model.DTO;
import Model.Header;
import Model.History;
import Model.PairInfo;
import Security.Security;
import Services.*;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Tạo ra một thread mới để kết nối và xử lý lắng nghe từ phía Server
 */
public class ClientWorker extends Thread implements Runnable {
    public static final int NAME_LIMIT = 10;
    public static String name;
    public static PairInfo pair;
    public static DTO currentPacket;
    public static ArrayList<History> histories;

    @Override
    public void run() {
        try {
            while (true) {
                String response = Client.receive(); //Chờ thông điệp từ Server rồi xử lý
                if (response.equalsIgnoreCase(Header.BREAK_CONNECT_HEADER))
                    break;

                if (response.equalsIgnoreCase("stop"))
                    break;

                DTO serverPacket = responseHandle(response);
                if (serverPacket == null)
                    continue;
                System.out.println("Received: " + JsonParser.pack(serverPacket));
            }
            Client.close();
            Client.Frame.showDisconnectAlert();
        } catch (Exception e) {
            Client.close();
            Client.Frame.showDisconnectAlert();
        }
    }

    /**
     * Hàm dùng để xử lý dữ liệu để gửi cho server
     */
    public static void requestHandle(DTO dto) throws IOException {
        currentPacket = dto;
        if (currentPacket.getSender() == null)
            currentPacket.setSender(Client.UID);
        currentPacket.setReceiver(Client.UID);
        if (currentPacket.getData() == null)
            currentPacket.setData("null");
        currentPacket.setCreatedDate(LocalDateTime.now().toString());
        String output = JsonParser.pack(currentPacket);
        System.out.println("Sent: " + output);
        Client.send(Security.encrypt(output, Client.secretKey)); //mã hóa bằng secret key trước khi gửi
    }

    /**
     * Hàm dùng để xử lý dữ liệu từ Server gửi tới
     * @param data dữ liệu trừ server đã bị mã hóa
     * @return ServerDataPacket - Gói dữ liệu Server
     */
    public static DTO responseHandle(String data) throws IOException {
        if (data == null || data.isEmpty() || data.isBlank() || data.equals("wait") || data.equals("null"))
            return null;

        if (data.equalsIgnoreCase("expired")) {
            System.out.println("Secret Key of Client expired. Make new...");

            //Thực hiện xác minh lại vì key cũ đã quá hạn.
            //Tạo key mới
            Client.create(Client.line);

            System.out.println("Sent " + Client.UID + "|" + Client.secretKey + " to server.");
            try {
                //Thực hiện kết nối SSL socket tới server verifier.
                Client.openVerify();
                Client.sendVerify(); //Gửi lại UID + key
                Client.waitVerify(); //chờ phản hồi ""
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Server Verifier not reply !");
                Client.close();
                Client.Frame.showDisconnectAlert();
                throw new IOException();
            }

            Client.send("renewed");

            //Sau khi xác minh thì viết lại list uid vào system.conf
            FileHandler.write(Client.CLIENT_SIDE_PATH + Client.FILE_CONFIG_NAME, "", false); //clear file trước.
            for (String s : Client.uidStore)
                FileHandler.write(Client.CLIENT_SIDE_PATH + Client.FILE_CONFIG_NAME, s + "\n", true);

            //Gửi lại dữ liệu được mã hóa với secret key mới
            DTO packet = currentPacket;
            requestHandle(packet);
            return null;
        }

        DTO dto = JsonParser.unpack(Security.decrypt(data, Client.secretKey), DTO.class); //giả mã bằng secret key
        System.out.println("Received: " + JsonParser.pack(dto) + "\n");
        switch (dto.getHeader()) {
            case Header.MESSAGE_HEADER:
                Client.Frame.appendReceive(dto.getData(), dto.getCreatedDate());
                return null;

            case Header.MESSAGE_SERVER_HEADER:
                JOptionPane.showMessageDialog(Client.Frame, dto.getData(), "Server Message", JOptionPane.INFORMATION_MESSAGE);
                return null;

            case Header.SERVER_BUSY_HEADER:
                if (Client.Frame.isLoginPage())
                    Dialog.newAlertDialog(Client.Frame, "Server Busy");
                return null;

            case Header.HISTORY_RECOVERY_HEADER:
                if (!Client.Frame.isChatPage())
                    return null;
                History[] get = JsonParser.unpack(dto.getData(), History[].class);
                histories = new ArrayList<>(Arrays.asList(get));
                for (History history : histories) {
                    if (history.getSender().equals(name))
                        Client.Frame.appendSend(history.getMessage(), history.getSentDate());
                    else Client.Frame.appendReceive(history.getMessage(), history.getSentDate());
                }
                return null;

            case Header.PAIRED_CHAT_HEADER:
                pair = new PairInfo();
                pair.setName(dto.getData());
                pair.setStatus("Online");
                Client.Frame.stopFinding();
                return null;

            case Header.PAIR_LEFT_HEADER:
                Client.Frame.appendAlert(pair.getName() + " left the chat", true);
                pair.setStatus("Offline");
                Client.Frame.setInfo(pair);
                return null;

            case Header.INVITE_CHAT_HEADER:
                if (Client.Frame.isLoginPage() && Client.Frame.isFinding())
                    Dialog.newInviteDialog(Client.Frame, dto.getData());
                else {
                    dto.setData("false");
                    requestHandle(dto);
                }
                return null;

            case Header.INVITE_CHAT_FAIL_HEADER:
                if (Client.Frame.isLoginPage())
                    Dialog.newInviteFailedDialog(Client.Frame);
                return null;

            case Header.CONFIRM_CHAT_HEADER:
                currentPacket = dto;
                if (Client.Frame.isLoginPage() && Client.Frame.isFinding())
                    Dialog.newConfirmDialog(Client.Frame, dto);
                else  {
                    dto.setData("false");
                    requestHandle(dto);
                }
                return null;

            case Header.USER_INFO_HEADER:
                pair = JsonParser.unpack(dto.getData(), PairInfo.class);
                return null;

            case Header.NAME_CHECK_HEADER:
                Client.Frame.stopChecking(dto.getData());
                return null;

            default:
                return dto;
        }
    }
}
