package constant;

import object.Certificate;
import com.sun.jdi.connect.spi.ClosedConnectionException;
import core.Launcher;
import object.*;
import utils.ObjectUtil;
import worker.Impl.SecureWorker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClientMethods {
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
                SecureWorker worker = ObjectUtil.getFirstFromArray(data, SecureWorker.class);
                Packet packet = ObjectUtil.getFirstFromArray(data, Packet.class);
                if (packet.getData().get(DataKey.STATUS_CODE).equals(StatusCode.OK)) {
                    UUID uid;
                    try {
                        uid = UUID.fromString(packet.getData().get(DataKey.SENDER));
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        throw new IllegalArgumentException("Wrong UID format !");
                    }
                    String username = packet.getData().get(DataKey.USERNAME);
                    Certificate certificate = new Certificate(uid, username, worker.getSecretKey());
                    certificate.setAuthenticated(true);
                    Launcher.getInstance().setCertificate(certificate);
                    worker.close();
                }

                if (packet.getData().get(DataKey.STATUS_CODE).equals(StatusCode.UNAUTHENTICATED))
                    throw new Exception("Authenticate Failed !");

                if (packet.getData().get(DataKey.STATUS_CODE).equals(StatusCode.BAD_REQUEST))
                    throw new Exception("Bad Request !");
            }
        });
    }
}
