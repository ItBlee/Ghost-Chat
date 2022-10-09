package object;

import java.util.Objects;
import java.util.UUID;

public final class Certificate {
    private final UUID uid;
    private final String username;
    private final SecretKey secretKey;
    private boolean isAuthenticated;

    public Certificate(UUID uuid, String username, SecretKey secretKey) {
        this.uid = uuid;
        this.username = username;
        this.secretKey = secretKey;
        this.isAuthenticated = false;
    }

    public UUID getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public SecretKey getSecretKey() {
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
