package constant;

import core.Launcher;
import entity.User;
import exception.ChatAppException;
import security.Certificate;
import service.UserService;
import tranfer.*;
import utils.JsonParser;
import utils.ObjectUtil;
import worker.Worker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ServerMethods {
    public static final Map<String, Executable> methods;

    static {
        methods = Collections.synchronizedMap(new HashMap<String, Executable>());

        methods.put(Header.BREAK_CONNECT, new Executable() {
            @Override
            public void run(Object... data) throws Exception {
                Worker worker = ObjectUtil.getFirstFromArray(data, Worker.class);
                worker.close();
            }
        });

        methods.put(Header.AUTH_LOGIN, new Executable() {
            @Override
            public void run(Object... data) throws Exception {
                Worker worker = ObjectUtil.getFirstFromArray(data, Worker.class);
                Packet request = ObjectUtil.getFirstFromArray(data, Packet.class);
                UserService service = ObjectUtil.getFirstFromArray(data, UserService.class);
                String username = request.getData(DataKey.USERNAME);
                String password = request.getData(DataKey.PASSWORD);
                String secretKey = request.getData(DataKey.SECRET_KEY);
                Packet response = new Packet();
                response.setHeader(Header.AUTH_LOGIN);
                User user = null;
                try {
                    user = service.login(username, password);
                    if (!secretKey.isEmpty()) {
                        response.putData(DataKey.STATUS_CODE, StatusCode.OK);
                        response.putData(DataKey.UID, user.getId().toString());
                        response.putData(DataKey.USERNAME, user.getUsername());
                    }
                } catch (ChatAppException e) {
                    e.printStackTrace();
                    response.putData(DataKey.ERROR_MESSAGE, e.getMessage());
                    response.putData(DataKey.STATUS_CODE, StatusCode.UNAUTHENTICATED);
                }
                String json = JsonParser.toJson(response);
                worker.send(json);
                if (response.getData(DataKey.STATUS_CODE).equals(StatusCode.OK)) {
                    Certificate certificate = new Certificate(user.getId().toString(), user.getUsername(), secretKey);
                    certificate.setAuthenticated(true);
                    Launcher.getInstance().getSessions().add(certificate);
                }
            }
        });

        methods.put(Header.AUTH_REGISTER, new Executable() {
            @Override
            public void run(Object... data) throws Exception {
                Worker worker = ObjectUtil.getFirstFromArray(data, Worker.class);
                Packet request = ObjectUtil.getFirstFromArray(data, Packet.class);
                UserService service = ObjectUtil.getFirstFromArray(data, UserService.class);
                String username = request.getData(DataKey.USERNAME);
                String password = request.getData(DataKey.PASSWORD);
                String secretKey = request.getData(DataKey.SECRET_KEY);
                Packet response = new Packet();
                response.setHeader(Header.AUTH_REGISTER);
                User user = null;
                try {
                    user = service.register(username, password);
                    response.putData(DataKey.STATUS_CODE, StatusCode.CREATED);
                    response.putData(DataKey.UID, user.getId().toString());
                    response.putData(DataKey.USERNAME, user.getUsername());
                } catch (ChatAppException e) {
                    e.printStackTrace();
                    response.putData(DataKey.ERROR_MESSAGE, e.getMessage());
                    response.putData(DataKey.STATUS_CODE, StatusCode.INTERNAL_SERVER_ERROR);
                }
                String json = JsonParser.toJson(response);
                worker.send(json);
                if (response.getData(DataKey.STATUS_CODE).equals(StatusCode.OK)) {
                    Certificate certificate = new Certificate(user.getId().toString(), user.getUsername(), secretKey);
                    certificate.setAuthenticated(true);
                    Launcher.getInstance().getSessions().add(certificate);
                }
            }
        });
    }
}
