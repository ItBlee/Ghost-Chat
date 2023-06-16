package com.itblee.core.function;

import com.itblee.core.Worker;
import com.itblee.transfer.Packet;

@FunctionalInterface
public interface Executable {
    void execute(Worker worker, Packet data) throws Exception;
}
