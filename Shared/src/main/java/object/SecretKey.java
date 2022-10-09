package object;

import java.util.Objects;

public final class SecretKey {
    private final String key;

    public SecretKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public boolean equals(String s) {
        return key.equals(s);
    }
            
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecretKey)) return false;
        SecretKey dataKey = (SecretKey) o;
        return Objects.equals(key, dataKey.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
