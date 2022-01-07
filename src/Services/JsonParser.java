package Services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Format Json
 */
public class JsonParser {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    /**
     * Đóng gói data packet thành chuỗi dữ liệu JSON
     * @return String - dữ liệu dạng JSON
     */
    public static String pack(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * Khởi tạo data packet cho client từ dữ liệu JSON
     * @param json dữ liệu dạng JSON
     * @param classOfT Object T muốn unpack
     */
    public static <T> T unpack(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
