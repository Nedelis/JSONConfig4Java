package com.github.nedelis.jc4j.logging;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains all allowed message/logger levels
 */
@SuppressWarnings("unused")
public final class JC4JLogLevel {

    private static final Map<String, Integer> LEVELS = new HashMap<>();

    public static final int ALL = registerLevel("ALL", 0);
    public static final int TRACE = registerLevel("TRACE", 1);
    public static final int DEBUG = registerLevel("DEBUG", 2);
    public static final int INFO = registerLevel("INFO", 3);
    public static final int WARN = registerLevel("WARN", 4);
    public static final int ERROR = registerLevel("ERROR", 5);
    public static final int FATAL = registerLevel("FATAL", 6);
    public static final int OFF = registerLevel("OFF", 7);

    /**
     * Registers logging level
     * @param name level name
     * @param level level integer
     * @return level integer
     */
    private static int registerLevel(String name, int level) {
        LEVELS.put(name, level);
        return level;
    }

    /**
     * Finds name of the provided level integer
     * @param level level integer
     * @return name of the provided level
     */
    public static String getLevelName(int level) {
        for (var k : LEVELS.keySet()) {
            if (LEVELS.get(k) == level) {
                return k;
            }
        }
        return "OFF";
    }

}
