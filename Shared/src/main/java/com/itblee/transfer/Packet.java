package com.itblee.transfer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Packet implements Serializable, Cloneable {
    private Header header;
    private StatusCode code;
    private String message;
    private final Map<DataKey, Object> body = new HashMap<>();

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public StatusCode getCode() {
        return code;
    }

    public void setCode(StatusCode code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public <T> T get(DataKey key) {
        return (T) body.get(key);
    }

    public void putData(DataKey key, Object value) {
        body.put(key, value);
    }

    public boolean is(StatusCode code) {
        return this.code == code;
    }

    public boolean is(Header header) {
        return this.header == header;
    }

    public Packet clear() {
        header = null;
        code = null;
        body.clear();
        message = null;
        return this;
    }

    @Override
    public Packet clone() {
        try {
            return (Packet) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
