package object;

import java.util.HashMap;
import java.util.Map;

public class Packet {
    private Header header;
    private final Map<DataKey, String> data;

    public Packet(Header header) {
        data = new HashMap<>();
        this.header = header;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Map<DataKey, String> getData() {
        return data;
    }
}
