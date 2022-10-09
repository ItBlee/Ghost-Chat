package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * Format Json
 */
public class JsonParser {
    /**
     * chuyển object thành dữ liệu JSON
     * @return String - dữ liệu dạng JSON
     */
    public static String toJson(Object obj) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }

    /**
     * Khởi tạo Object từ dữ liệu JSON
     * @param json chuỗi JSON
     * @param classOfT class Object muốn tạo
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.fromJson(json, classOfT);
    }
}
