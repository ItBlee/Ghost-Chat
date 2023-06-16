package com.itblee.security;

import com.itblee.core.Worker;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class User implements Serializable {
    private String username;
    private Worker worker;
    private Certificate certificate;

    public User(Certificate certificate) {
        this.certificate = certificate;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getUid() {
        return certificate.getUid();
    }

    public void setUid(UUID uid) {
        certificate.setUid(uid);
    }

    public String getSecretKey() {
        return certificate.getSecretKey();
    }

    public void setSecretKey(String secretKey) {
        certificate.setSecretKey(secretKey);
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getCertificate().equals(that.getCertificate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCertificate());
    }
}
