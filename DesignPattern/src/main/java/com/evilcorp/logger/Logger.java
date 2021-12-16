package com.evilcorp.logger;

public interface Logger {
    enum Level {
        ERROR, WARNING, INFO
    }

    void log(SystemLogger.Level level, String message);
}
