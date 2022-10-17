package tranfer;

import java.util.HashMap;
import java.util.Map;

public class Packet {
    private String header;
    private final Map<String, String> body = new HashMap<>();

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getData(String key) {
        return body.get(key);
    }

    public void putData(String key, String value) {
        body.put(key, value);
    }
}
