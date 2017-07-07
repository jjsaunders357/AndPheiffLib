package com.pheiffware.lib.utils.log;

public interface LogHandler {
    void error(String message, Exception e);

    void info(String message);
}
