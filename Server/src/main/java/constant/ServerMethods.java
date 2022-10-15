package constant;

import com.sun.jdi.connect.spi.ClosedConnectionException;
import core.Launcher;
import entity.User;
import object.*;
import service.UserService;
import utils.JsonParser;
import utils.ObjectUtil;
import worker.Worker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ServerMethods {
    public static final Map<Header, Executable> methods;

    static {
        methods = Collections.synchronizedMap(new HashMap<Header, Executable>());

        methods.put(Header.BREAK_CONNECT, new Executable() {
            @Override
            public void run(Object... data) throws Exception {
                throw new ClosedConnectionException();
            }
        });

        methods.put(Header.AUTH_LOGIN, new Executable() {
            @Override
            public void run(Object... data) throws Exception {
                Worker worker = ObjectUtil.getFirstFromArray(data, Worker.class);
                Packet request = ObjectUtil.getFirstFromArray(data, Packet.class);
                UserService service = ObjectUtil.getFirstFromArray(data, UserService.class);
                String username = request.getData().get(DataKey.USERNAME);
                String password = request.getData().get(DataKey.PASSWORD);
                SecretKey secretKey = new SecretKey(request.getData().get(DataKey.SECRET_KEY));
                Packet response = new Packet(Header.AUTH_LOGIN);
                User user = null;
                try {
                    user = service.login(username, password);
                    if (!secretKey.getKey().isEmpty()) {
                        response.getData().put(DataKey.STATUS_CODE, StatusCode.OK);
                        response.getData().put(DataKey.UID, user.getId().toString());
                    }
                } catch (NotFoundException | PasswordNotCorrectException e) {
                    e.printStackTrace();
                    response.getData().put(DataKey.STATUS_CODE, StatusCode.UNAUTHENTICATED);
                }
                String json = JsonParser.toJson(response);
                worker.send(json);
                if (response.getData().get(DataKey.STATUS_CODE).equals(StatusCode.OK))
                    Launcher.getInstance().getSessions().put(user.getId().toString(), user.getUsername());
            }
        });
    }
}
