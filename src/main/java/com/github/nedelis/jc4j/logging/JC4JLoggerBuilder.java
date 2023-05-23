package com.github.nedelis.jc4j.logging;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.nio.file.Path;

@SuppressWarnings("unused")
public final class JC4JLoggerBuilder {

    private final String loggerName;
    private Path pathToLogFile = null;
    private int logLevel = JC4JLogLevel.DEBUG;
    private JC4JLoggerMessagePattern pattern = new JC4JLoggerMessagePattern();

    public JC4JLoggerBuilder(@NotNull String loggerName) {
        this.loggerName = loggerName;
    }

    public JC4JLoggerBuilder setPathToLogFile(@NotNull Path path) {
        this.pathToLogFile = path;
        return this;
    }

    public JC4JLoggerBuilder setLogLevel(@Range(from=0, to=7) int level) {
        this.logLevel = level;
        return this;
    }

    public JC4JLoggerBuilder setPattern(@NotNull JC4JLoggerMessagePattern pattern) {
        this.pattern = pattern;
        return this;
    }

    @Contract(" -> new")
    public @NotNull JC4JLogger build() {
        return new JC4JLogger(loggerName, pathToLogFile, logLevel, pattern);
    }

}
