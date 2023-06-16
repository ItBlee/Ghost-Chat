package com.itblee.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Optional;


public final class JsonParser {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonParser() {
        throw new AssertionError();
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> Optional<T> fromJson(String json, Class<T> tClass) {
        return Optional.ofNullable(gson.fromJson(json, tClass));
    }

}
