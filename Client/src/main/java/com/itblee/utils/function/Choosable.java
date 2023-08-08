package com.itblee.utils.function;

@FunctionalInterface
public interface Choosable {
    void reply(boolean isAccept) throws Exception;
}
