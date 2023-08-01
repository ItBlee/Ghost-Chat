package com.itblee.core;

import com.itblee.core.function.Executable;
import com.itblee.transfer.Request;
import com.itblee.transfer.Packet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Controller {

    private Map<Request, Executable> methods = new HashMap<>();

    public void resolve(Worker worker, Packet data) throws Exception {
        Executable method = methods.get(data.getHeader());
        if (method != null)
            method.execute(worker, data);
        else throw new IllegalArgumentException("Undefined method to resolve !");
    }

    public void map(Request request, Executable executable) {
        this.methods.put(request, executable);
    }

    public void mapAll(Map<Request, Executable> methods) {
        this.methods.putAll(methods);
    }

    public void lock() {
        methods = Collections.unmodifiableMap(methods);
    }

}
