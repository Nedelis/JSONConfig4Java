package com.github.nedelis.jc4j.logging;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class allows logger have custom message patterns
 */

public final class JC4JLoggerMessagePattern {

    /**
     * Stamp is the pattern element, that can be formatted
     */
    @FunctionalInterface
    private interface Stamp {

        @NotNull String formatted();

    }

    /* All stamps:
     'yyyy' returns the current year
     'MM' returns the current month
     'dd' returns the current day
     'hh' returns the current hour
     'HH' returns the current hour according to the AM/PM format
     'a' returns AM or PM
     'mm' returns the current minute
     'ss' returns the current second
     'SSS' returns the current millisecond
     'msg' returns the message
     'lvl' returns the level of the message
     'log-name' returns the name of the logger
    */

    /**
     * Contains all registered stamps
     */
    private static final Map<String, Stamp> STAMPS = new HashMap<>() {{
        put("yyyy", () -> LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy")));
        put("MM", () -> LocalDate.now().format(DateTimeFormatter.ofPattern("MM")));
        put("dd", () -> LocalDate.now().format(DateTimeFormatter.ofPattern("dd")));
        put("hh", () -> LocalDate.now().format(DateTimeFormatter.ofPattern("hh")));
        put("HH", () -> LocalTime.now().format(DateTimeFormatter.ofPattern("HH")));
        put("a", () -> LocalTime.now().format(DateTimeFormatter.ofPattern("a")));
        put("mm", () -> LocalTime.now().format(DateTimeFormatter.ofPattern("mm")));
        put("ss", () -> LocalTime.now().format(DateTimeFormatter.ofPattern("ss")));
        put("SSS", () -> LocalTime.now().format(DateTimeFormatter.ofPattern("SSS")));
    }};

    private final @NotNull String pattern;

    public JC4JLoggerMessagePattern() {
        this("`HH`:`mm`:`ss` `log-name` [`lvl`] `msg`");
    }

    public JC4JLoggerMessagePattern(@NotNull String pattern) {
        this.pattern = pattern;
    }

    /**
     * Format the given message according to the pattern
     * @param message message to be printed
     * @param loggerName name of the logger that prints messages
     * @param logLevel level of the message
     * @return formatted message
     */
    public @NotNull String getFormattedMessage(@NotNull String message, @NotNull String loggerName, @Range(from=0, to=7) int logLevel) {

        STAMPS.put("msg", () -> message);
        STAMPS.put("log-name", () -> loggerName);
        STAMPS.put("lvl", () -> JC4JLogLevel.getLevelName(logLevel));

        var formatted = new StringBuilder();
        var waitingForClosing = false;
        var stamp = new StringBuilder();
        for (char c : pattern.toCharArray()) {
            if (waitingForClosing && c != '`') {
                stamp.append(c);
                continue;
            }
            if (waitingForClosing) {
                formatted.append(STAMPS.getOrDefault(stamp.toString(), String::new).formatted());
                stamp = new StringBuilder();
                waitingForClosing = false;
                continue;
            }
            if (c == '`') {
                waitingForClosing = true;
                continue;
            }
            formatted.append(c);
        }

        return formatted.toString();
    }

}
