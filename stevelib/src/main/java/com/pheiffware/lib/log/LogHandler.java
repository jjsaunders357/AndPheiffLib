package com.pheiffware.lib.log;

public interface LogHandler {
    void error(String message, Exception e);

    void info(String message);
}
