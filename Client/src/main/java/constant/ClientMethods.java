package constant;

import com.sun.jdi.connect.spi.ClosedConnectionException;
import core.Launcher;
import security.Certificate;
import tranfer.*;
import utils.ObjectUtil;
import utils.StringUtil;
import worker.Impl.SecureWorker;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ClientMethods {
    public static final Map<String, Executable> methods;

    static {
        methods = Collections.synchronizedMap(new HashMap<String, Executable>());

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
                Packet response = ObjectUtil.getFirstFromArray(data, Packet.class);
                if (response.getData(DataKey.STATUS_CODE).equals(StatusCode.OK)) {
                    String uid = response.getData(DataKey.UID);
                    String username = response.getData(DataKey.USERNAME);
                    if (StringUtil.isNullOrEmpty(uid) || StringUtil.isNullOrEmpty(username))
                        throw new Exception("Authenticate Failed !");
                    Certificate certificate = new Certificate(uid, username, worker.getSecretKey());
                    certificate.setAuthenticated(true);
                    Launcher.getInstance().setCertificate(certificate);
                    worker.close();
                }
                else if (response.getData(DataKey.STATUS_CODE).equals(StatusCode.UNAUTHENTICATED))
                    throw new Exception("Authenticate Failed !");
            }
        });

        methods.put(Header.AUTH_REGISTER, new Executable() {
            @Override
            public void run(Object... data) throws Exception {
                SecureWorker worker = ObjectUtil.getFirstFromArray(data, SecureWorker.class);
                Packet response = ObjectUtil.getFirstFromArray(data, Packet.class);
                if (response.getData(DataKey.STATUS_CODE).equals(StatusCode.CREATED)) {
                    String uid = response.getData(DataKey.UID);
                    String username = response.getData(DataKey.USERNAME);
                    if (StringUtil.isNullOrEmpty(uid) || StringUtil.isNullOrEmpty(username))
                        throw new Exception("Authenticate Failed !");
                    Certificate certificate = new Certificate(uid, username, worker.getSecretKey());
                    certificate.setAuthenticated(true);
                    Launcher.getInstance().setCertificate(certificate);
                    worker.close();
                }
                else if (response.getData(DataKey.STATUS_CODE).equals(StatusCode.INTERNAL_SERVER_ERROR))
                    throw new Exception("Authenticate Failed !");
            }
        });
    }
}
