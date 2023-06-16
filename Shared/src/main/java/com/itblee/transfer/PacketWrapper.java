package com.itblee.transfer;

import java.io.Serializable;
import java.util.UUID;

public class PacketWrapper implements Serializable {
    private UUID sender;
    private String body;

    public UUID getSender() {
        return sender;
    }

    public void setSender(UUID sender) {
        this.sender = sender;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
