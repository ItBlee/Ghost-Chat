package com.itblee.security;

import com.itblee.core.Worker;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class User implements Serializable {
    private String username;
    private String password;
    private Worker worker;
    private Certificate certificate;
    private Date createdDate;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UUID getUid() {
        return certificate != null ? certificate.getUid() : null;
    }

    public void setUid(String uid) {
        certificate.setUid(UUID.fromString(uid));
    }

    public void setUid(UUID uid) {
        certificate.setUid(uid);
    }

    public String getSecretKey() {
        return certificate != null ? certificate.getSecretKey() : null;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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
