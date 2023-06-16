package com.itblee.core;

import com.itblee.core.function.Executable;
import com.itblee.transfer.Header;
import com.itblee.transfer.Packet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Controller {

    private Map<Header, Executable> methods = new HashMap<>();

    public void resolve(Worker worker, Packet data) throws Exception {
        Executable method = methods.get(data.getHeader());
        if (method != null)
            method.execute(worker, data);
        else throw new IllegalArgumentException("Undefined method to resolve !");
    }

    public void put(Header header, Executable executable) {
        this.methods.put(header, executable);
    }

    public void putAll(Map<Header, Executable> methods) {
        this.methods.putAll(methods);
    }

    public void lock() {
        methods = Collections.unmodifiableMap(methods);
    }

}
