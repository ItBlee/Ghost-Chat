package object;

import java.util.Objects;

public final class DataKey {
    public static final DataKey UID = new DataKey("uid");
    public static final DataKey SENDER = new DataKey("sender");
    public static final DataKey STATUS_CODE = new DataKey("code");
    public static final DataKey SECRET_KEY = new DataKey("sk");
    public static final DataKey USERNAME = new DataKey("un");
    public static final DataKey PASSWORD = new DataKey("pwd");
    public static final DataKey USER_CHOICE = new DataKey("uc");
    public static final DataKey ACCEPT = new DataKey("ok");
    public static final DataKey DECLINE = new DataKey("no");
    
    private final String key;

    private DataKey(String key) {
        this.key = key;
    }

    public boolean equals(String s) {
        return key.equalsIgnoreCase(s);
    }
            
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataKey)) return false;
        DataKey dataKey = (DataKey) o;
        return Objects.equals(key, dataKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
