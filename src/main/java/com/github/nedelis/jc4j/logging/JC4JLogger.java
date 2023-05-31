package com.github.nedelis.jc4j.logging;

import com.github.nedelis.jc4j.util.NoParamsFunction;
import com.github.nedelis.jc4j.util.Procedure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom logger of the library. This logger is wrapper for the System.out.println() function
 * @see JC4JLoggerBuilder
 * @see JC4JLogLevel
 */

@SuppressWarnings("unused")
public class JC4JLogger {
    private final String name;
    private final int logLevel;
    private final JC4JLoggerMessagePattern pattern;

    private final List<String> messages = new ArrayList<>();
    private boolean isEnabled = true;

    protected JC4JLogger(String name, Path pathToLogFile, @Range(from=0, to=7) int logLevel, JC4JLoggerMessagePattern pattern) {
        this.name = name;
        this.logLevel = logLevel;
        this.pattern = pattern;

        if (pathToLogFile != null) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try (var writer = Files.newBufferedWriter(pathToLogFile, StandardCharsets.UTF_8)) {
                    for (var line : messages) {
                        writer.write(line + "\n");
                    }
                } catch (IOException e) {
                    error("Failed to create log file!", e);
                }
            }, "SaveLogMessagesToFile"));
        }
    }

    /**
     * Prints any throwable to the console and saves the message into {@link #messages}
     * @param t throwable to print and save its message
     */
    private void printThrowable(@NotNull Throwable t) {
        var sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        System.out.println("\u001B[31m" + sw + "\u001B[0m");
        messages.add(sw.toString());
    }

    /**
     * Returns whether the message with given level can be printed or not
     * @param msgLevel level of the message that tries to be printed
     * @return true if the message level less or equal to the logger level
     */
    private boolean canPrint(@Range(from=0, to=7) int msgLevel) {
        return isEnabled() && logLevel <= msgLevel;
    }

    public void trace(String msg) {
        if (canPrint(JC4JLogLevel.TRACE)) {
            var formattedMessage = pattern.getFormattedMessage(msg, name(), JC4JLogLevel.TRACE);
            System.out.println("\u001B[37m" + formattedMessage + "\u001B[0m");
            messages.add(formattedMessage);
        }
    }

    public void trace(String msg, Throwable t) {
        if (canPrint(JC4JLogLevel.TRACE)) {
            trace(msg);
            printThrowable(t);
        }
    }

    public void debug(String msg) {
        if (canPrint(JC4JLogLevel.DEBUG)) {
            var formattedMessage = pattern.getFormattedMessage(msg, name(), JC4JLogLevel.DEBUG);
            System.out.println("\u001B[36m" + formattedMessage + "\u001B[0m");
            messages.add(formattedMessage);
        }
    }

    public void debug(String message, Throwable t) {
        if (canPrint(JC4JLogLevel.DEBUG)) {
            debug(message);
            printThrowable(t);
        }
    }

    public void info(String msg) {
        if (canPrint(JC4JLogLevel.INFO)) {
            var formattedMessage = pattern.getFormattedMessage(msg, name(), JC4JLogLevel.INFO);
            System.out.println("\u001B[32m" + formattedMessage + "\u001B[0m");
            messages.add(formattedMessage);
        }
    }

    public void info(String msg, @NotNull Throwable t) {
        if (canPrint(JC4JLogLevel.INFO)) {
            info(msg);
            printThrowable(t);
        }
    }

    public void warn(String msg) {
        if (canPrint(JC4JLogLevel.WARN)) {
            var formattedMessage = pattern.getFormattedMessage(msg, name(), JC4JLogLevel.WARN);
            System.out.println("\u001B[33m" + formattedMessage + "\u001B[0m");
        }
    }

    public void warn(String msg, @NotNull Throwable t) {
        if (canPrint(JC4JLogLevel.WARN)) {
            warn(msg);
            printThrowable(t);
        }
    }

    public void error(String msg) {
        if (canPrint(JC4JLogLevel.ERROR)) {
            var formattedMessage = pattern.getFormattedMessage(msg, name(), JC4JLogLevel.ERROR);
            System.out.println("\u001B[31m" + formattedMessage + "\u001B[0m");
            messages.add(formattedMessage);
        }
    }

    public void error(String msg, @NotNull Throwable t) {
        if (canPrint(JC4JLogLevel.ERROR)) {
            error(msg);
            printThrowable(t);
        }
    }

    /**
     * Prints the given message, stores it in the {@link #messages} and shuts down the program
     * @param msg message to be printed
     */
    public void fatal(String msg) {
        if (canPrint(JC4JLogLevel.FATAL)) {
            var formattedMessage = pattern.getFormattedMessage(msg, name(), JC4JLogLevel.FATAL);
            System.out.println("\u001B[31m" + formattedMessage + "\u001B[0m");
            messages.add(formattedMessage);
            System.exit(1);
        }
    }

    /**
     * Do the same thing as {@link #fatal(String)}, but also prints the given throwable
     * @param t throwable to be printed
     * @param msg message to be printed
     */
    public void fatal(String msg, @NotNull Throwable t) {
        if (canPrint(JC4JLogLevel.FATAL)) {
            var formattedMessage = pattern.getFormattedMessage(msg, name(), JC4JLogLevel.FATAL);
            System.out.println("\u001B[31m" + formattedMessage + "\u001B[0m");
            messages.add(formattedMessage);

            printThrowable(t);

            System.exit(1);
        }
    }

    /**
     * This function allows to do any other function without logging
     * @param func returnable function without params
     * @return result of the given function
     * @param <R> type of the function's return value
     */
    public <R> R doWithoutLogging(@NotNull NoParamsFunction<R> func) {
        disable();
        var result = func.apply();
        enable();
        return result;
    }

    /**
     * This procedure allows to do any other procedure without logging
     * @param proc procedure that will be executed
     */
    public void doWithoutLogging(@NotNull Procedure proc) {
        disable();
        proc.apply();
        enable();
    }

    /**
     * Disables logger
     */
    public void disable() {
        this.isEnabled = false;
    }

    /**
     * Enables logger
     */
    public void enable() {
        this.isEnabled = true;
    }

    /**
     * Checks whether the logger is enabled or not
     * @return true if the logger is enabled
     */
    public boolean isEnabled() {
        return this.isEnabled;
    }

    /**
     * @return name of the logger
     */
    public String name() {
        return this.name;
    }

    /**
     * @return level of the logger
     */
    public int logLevel() {
        return this.logLevel;
    }

}
