package com.evilcorp.logger;

public class SystemLogger implements Logger {
    private static final SystemLogger instance = new SystemLogger();

    private SystemLogger() {}

    public static SystemLogger instance() {
        return instance;
    }

    public void log(Level level, String message) {
        System.err.println(level + " " + message);
    }
}