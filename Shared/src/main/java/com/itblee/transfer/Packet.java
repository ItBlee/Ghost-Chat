package com.itblee.transfer;

import com.itblee.utils.JsonParser;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Packet implements Serializable, Cloneable {
    private Request request;
    private StatusCode code;
    private String message;
    private final Map<DataKey, String> data = new HashMap<>();

    public Request getHeader() {
        return request;
    }

    public void setHeader(Request request) {
        this.request = request;
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

    public String get(DataKey key) {
        return data.get(key);
    }

    public <T> Optional<T> get(DataKey key, Class<T> tClass) {
        return JsonParser.fromJson(get(key), tClass);
    }

    public <T> Optional<T> get(DataKey key, Type type) {
        return JsonParser.fromJson(get(key), type);
    }

    public void putData(DataKey key, Object value) {
        if (value instanceof CharSequence || value instanceof UUID)
            data.put(key, value.toString());
        else data.put(key, JsonParser.toJson(value));
    }

    public void putData(DataKey key, Object value, Type type) {
        data.put(key, JsonParser.toJson(value, type));
    }

    public boolean is(StatusCode code) {
        return this.code == code;
    }

    public boolean is(Request request) {
        return this.request == request;
    }

    public Packet clear() {
        request = null;
        code = null;
        data.clear();
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
