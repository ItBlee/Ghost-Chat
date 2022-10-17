package security;

import java.util.Objects;

public final class Certificate {
    private final String uid;
    private final String username;
    private final String secretKey;
    private boolean isAuthenticated;

    public Certificate(String uuid, String username, String secretKey) {
        this.uid = uuid;
        this.username = username;
        this.secretKey = secretKey;
        this.isAuthenticated = false;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Certificate)) return false;
        Certificate that = (Certificate) o;
        return getUid().equals(that.getUid()) && getUsername().equals(that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUid(), getUsername());
    }
}
