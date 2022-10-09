package constant;

public final class ClientConstant {
    //SSL Socket
    public static final String TRUST_STORE_NAME = "myTrustStore.jts"; //store lưu public Key + chứng chỉ.
    public static final String FILE_CONFIG_NAME = "system.conf";
    public static final String CLIENT_SIDE_PATH = "workspace/Client.Side/";
    public static final String TRUST_STORE_PASSWORD = "checksyntax";
    public static final boolean SSL_DEBUG_ENABLE = false;

    public static final String FAIL_CONNECT = "Server Closed";

    public static final int LOGIN_PAGE = 0;
    public static final int CHAT_PAGE = 1;
    public static final int LIMIT_MESSAGE_LINE = 40;
    public static final int LIMIT_INPUT_LINE = 30;
    public static final int AUTO_LEFT_TIME = 20000; //millisecond
    public static final int NAME_LIMIT = 10;
}
