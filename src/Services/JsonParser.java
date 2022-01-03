package Services;

import Server.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonParser {
    /**
     * Đóng gói data packet thành chuỗi dữ liệu JSON
     * @return String - dữ liệu dạng JSON
     */
    public static String pack(DTO dto) {
        JsonObject packet = new JsonObject();
        packet.addProperty("header", dto.getHeader());
        packet.addProperty("sender", dto.getSender());
        packet.addProperty("receiver", dto.getReceiver());
        packet.addProperty("data", dto.getData());
        packet.addProperty("createdDate", dto.getCreatedDate());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = com.google.gson.JsonParser.parseString(packet.toString());
        return gson.toJson(je);
    }

    /**
     * Khởi tạo data packet cho client từ dữ liệu JSON
     * @param json dữ liệu dạng JSON
     */
    public static DTO unpack(String json) {
        JsonObject packet = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
        DTO dto = new DTO(packet.get("header").getAsString());
        dto.setSender(packet.get("sender").getAsString());
        dto.setReceiver(packet.get("receiver").getAsString());
        dto.setData(packet.get("data").getAsString());
        dto.setCreatedDate(packet.get("createdDate").getAsString());
        return dto;
    }

    public static String getUserInfo(User user) {
        JsonObject packet = new JsonObject();
        packet.addProperty("name", user.getName());
        packet.addProperty("uid", user.getUID());
        packet.addProperty("status", user.getStatus());
        packet.addProperty("modifiedDate", user.getModifiedDate());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = com.google.gson.JsonParser.parseString(packet.toString());
        return gson.toJson(je);
    }
}
