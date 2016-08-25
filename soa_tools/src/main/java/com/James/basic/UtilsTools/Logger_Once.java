package com.James.basic.UtilsTools;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by James on 16/5/20.
 * 只会打印一次的日志
 */
public class Logger_Once {
    private static final Log LOGGER = LogFactory.getLog(Logger_Once.class.getName());
    private static final ConcurrentHashMap<String,String> map = new ConcurrentHashMap<>();

    private static Boolean has(String message) {

        if (map.get(message) == null) {

            return false;
        }
        return true;
    }

    public static void debug(String message) {
        if (has(message)) {
            return;
        }
        LOGGER.debug(message);
    }

    public static void info(String message) {
        if (has(message)) {
            return;
        }
        LOGGER.info(message);
    }

    public static void warn(String message) {
        if (has(message)) {
            return;
        }
        LOGGER.warn(message);
    }

    public static void error(String message) {
        if (has(message)) {
            return;
        }
        LOGGER.error(message);
    }

    public static void debug(String message, Throwable e) {
        if (has(message)) {
            return;
        }
        LOGGER.debug(message, e);
    }

    public static void info(String message, Throwable e) {
        if (has(message)) {
            return;
        }
        LOGGER.info(message, e);
    }

    public static void warn(String message, Throwable e) {
        if (has(message)) {
            return;
        }
        LOGGER.warn(message, e);
    }

    public static void error(String message, Throwable e) {
        if (has(message)) {
            return;
        }
        LOGGER.error(message, e);
    }
}

