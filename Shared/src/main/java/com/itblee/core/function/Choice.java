package com.itblee.core.function;

@FunctionalInterface
public interface Choice {
    void reply(boolean isAccept) throws Exception;
}
