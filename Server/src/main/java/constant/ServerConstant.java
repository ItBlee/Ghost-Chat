package constant;

public final class ServerConstant {
    public static final int EXECUTOR_CORE = 3;          //Số thread một lúc
    public static final int EXECUTOR_MAX = 5;           //số thread tối đa khi server quá tải
    public static final int EXECUTOR_ALIVE_TIME = 10;    //thời gian một thread được sống nếu không làm gì
    public static final int EXECUTOR_CAPACITY = 10;     //Số lượng hàng chờ có thể chứa của executor
    public static final String KEY_STORE_NAME = "myKeyStore.jks";  //store lưu private Key + public Key + chứng chỉ.
    public static final String SERVER_SIDE_PATH = "workspace/Server.Side/";
    public static final String KEY_STORE_ALIAS = "mykey";
    public static final String KEY_STORE_PASSWORD_HASH = "0b1957259ce60db4f9cb5c51cb76a000cefe7234f922a515f56b977951eb6f84";
    public static final String KEY_STORE_PASSWORD_SALT = "5ae877676f3efe25";
    public static final boolean SSL_DEBUG_ENABLE = false;
    public static final String RENEW_USER_SESSION = "Renewed";
    public static final int SESSION_EXPIRED_TIME = -1;

    public static final String DB_NAME = "";
    public static final String DB_HOST = null;
    public static final Integer DB_PORT = null;
    public static final String DB_USERNAME = null;
    public static final String DB_PASSWORD = null;

}
