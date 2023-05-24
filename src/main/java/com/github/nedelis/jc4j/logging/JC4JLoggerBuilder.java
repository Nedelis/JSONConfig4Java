package com.github.nedelis.jc4j.logging;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.file.Path;

/**
 * This class helps to create JC4JLogger
 */

@SuppressWarnings("unused")
public final class JC4JLoggerBuilder {

    private final String loggerName;
    private Path pathToLogFile = null;
    private int logLevel = JC4JLogLevel.ALL;
    private JC4JLoggerMessagePattern pattern = new JC4JLoggerMessagePattern();

    public JC4JLoggerBuilder() {
        this("JC4J");
    }

    public JC4JLoggerBuilder(@NotNull String loggerName) {
        this.loggerName = loggerName;
    }

    /**
     * Specifies the path to the log file that will be stored
     * @param path path to the log file
     * @return current builder with reassigned path
     */
    public JC4JLoggerBuilder setPathToLogFile(@NotNull Path path) {
        this.pathToLogFile = path;
        return this;
    }

    /**
     * Specifies the level of the logger (min level of the messages). You must put value between 0 and 7.
     * @param level the level of the logger
     * @return current builder with reassigned logger level
     * @see JC4JLogLevel
     */
    public JC4JLoggerBuilder setLogLevel(@Range(from=0, to=7) int level) {
        this.logLevel = level;
        return this;
    }

    /**
     * Specifies the message pattern of the logger.
     * @param pattern custom message pattern
     * @return current builder with the reassigned message pattern
     * @see JC4JLoggerMessagePattern
     */
    public JC4JLoggerBuilder setPattern(@NotNull JC4JLoggerMessagePattern pattern) {
        this.pattern = pattern;
        return this;
    }

    /**
     * Builds a new logger
     * @return new logger with specified properties
     */
    @Contract(" -> new")
    public @NotNull JC4JLogger build() {
        return new JC4JLogger(loggerName, pathToLogFile, logLevel, pattern);
    }

}
