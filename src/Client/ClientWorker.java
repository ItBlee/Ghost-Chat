package Client;

import ClientGUI.ClientGUI;
import ClientGUI.Dialog;
import Security.AES_Encryptor;
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
    @Override
    public void run() {
        try {
            while (true) {
                String response = Client.receive(); //Chờ thông điệp từ Server rồi xử lý
                if (response.equalsIgnoreCase(Header.BREAK_CONNECT_HEADER))
                    break;

                DTO serverPacket = responseHandle(response);
                if (serverPacket == null)
                    continue;
                System.out.println("Received: " + JsonParser.pack(serverPacket));
            }
            Client.close();
        } catch (Exception e) {
            Client.close();
        }
    }

    /**
     * Hàm dùng để xử lý dữ liệu để gửi cho server
     */
    public static void requestHandle(DTO dto) throws IOException {
        Client.currentPacket = dto;
        if (Client.currentPacket.getSender() == null)
            Client.currentPacket.setSender(Client.UID);
        Client.currentPacket.setReceiver(Client.UID);
        if (Client.currentPacket.getData() == null)
            Client.currentPacket.setData("null");
        Client.currentPacket.setCreatedDate(LocalDateTime.now().toString());
        String output = JsonParser.pack(Client.currentPacket);
        System.out.println("Sent: " + output);
        Client.send(AES_Encryptor.encrypt(output, Client.secretKey)); //mã hóa bằng secret key trước khi gửi
    }

    /**
     * Hàm dùng để xử lý dữ liệu từ Server gửi tới
     * @param data dữ liệu trừ server đã bị mã hóa
     * @return ServerDataPacket - Gói dữ liệu Server
     */
    public static DTO responseHandle(String data) throws IOException {
        if (data == null || data.isEmpty() || data.isBlank() || data.equals("wait") || data.equals("null"))
            return null;

        if (data.equalsIgnoreCase("stop"))
            throw new IOException();

        if (data.equalsIgnoreCase("Expired")) {
            System.out.println("Secret Key of this Client expired. Try to make new...");

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
                System.out.println("Server verifier not reply !");
                Client.close();
                throw new IOException();
            }

            Client.send("renewed");

            //Sau khi xác minh thì viết lại list uid vào system.conf
            FileHandler.write(Client.CLIENT_SIDE_PATH + Client.FILE_CONFIG_NAME, "", false); //clear file trước.
            for (String s : Client.uidStore)
                FileHandler.write(Client.CLIENT_SIDE_PATH + Client.FILE_CONFIG_NAME, s + "\n", true);

            //Gửi lại dữ liệu được mã hóa với secret key mới
            DTO packet = Client.currentPacket;
            requestHandle(packet);
            return null;
        }

        DTO dto = JsonParser.unpack(AES_Encryptor.decrypt(data, Client.secretKey)); //giả mã bằng secret key
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
                History[] get = JsonParser.getHistoriesFromJson(dto.getData());
                Client.histories = new ArrayList<>(Arrays.asList(get));
                for (History history : Client.histories) {
                    if (history.getSender().equals(Client.name))
                        Client.Frame.appendSend(history.getMessage(), history.getSentDate());
                    else Client.Frame.appendReceive(history.getMessage(), history.getSentDate());
                }
                return null;

            case Header.PAIRED_CHAT_HEADER:
                Client.pair = new PairInfo();
                Client.pair.setName(dto.getData());
                Client.pair.setStatus("Online");
                Client.Frame.stopFinding();
                return null;

            case Header.PAIR_LEFT_HEADER:
                Client.Frame.appendAlert(Client.pair.getName() + " left the chat", true);
                Client.pair.setStatus("Offline");
                Client.Frame.setInfo(Client.pair);
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
                Client.currentPacket = dto;
                if (Client.Frame.isLoginPage() && Client.Frame.isFinding())
                    Dialog.newConfirmDialog(Client.Frame, dto);
                else  {
                    dto.setData("false");
                    requestHandle(dto);
                }
                return null;

            case Header.USER_INFO_HEADER:
                Client.pair = JsonParser.unpackUserInfo(dto.getData());
                return null;

            case Header.NAME_CHECK_HEADER:
                Client.Frame.stopChecking(dto.getData());
                return null;

            default:
                return dto;
        }
    }
}
