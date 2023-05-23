package com.github.nedelis.jc4j;

import com.github.nedelis.jc4j.jsonvalue.JSONValue;
import com.github.nedelis.jc4j.jsonvalue.JSONValueType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ConfigWrapper {

    private final Map<String, JSONValue> config = new HashMap<>();
    private final Map<String, JSONValue> defaultConfig = new HashMap<>();
    private final File configFile;
    private boolean isBroken = false;

    /**
     * Creates a new config file in config files directory and fills it with default values
     * @param pathToConfigFile path to config file that will be created
     * @param content content of the config file that will be created
     * @throws IOException thrown if file cannot be created
     */
    private void createConfig(@NotNull String pathToConfigFile, @NotNull Map<?, ?> content) throws IOException {
        var path = Path.of(pathToConfigFile);
        Files.createFile(path);
        try (var writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting().serializeNulls().create().toJson(content, writer);
        }
    }

    /**
     * Loads the configuration file values to {@link #config}
     * @param pathToConfigFile path to config file that will be loaded
     * @throws IOException if config file is not found or any other I/O error occurs
     */
    private Map<String, JSONValue> loadConfig(@NotNull String pathToConfigFile) throws IOException {
        try (var reader = Files.newBufferedReader(Path.of(pathToConfigFile))) {
            return JSONValueType.JS_VAL_MAP.convert(
                    new Gson().fromJson(reader, Map.class),
                    new HashMap<>()
            );
        }
    }

    /**
     * Creates new ConfigWrapper from path to config directory, config file name and default config file
     * @param configFileName config file name without extension
     * @param defaultConfig default config file (you may want to put it in your mod's resources folder)
     * @param pathToConfigDir path to directory, that contains configs
     * @return new ConfigWrapper with loaded config values
     */
    public static @NotNull ConfigWrapper of(@NotNull String pathToConfigDir, @NotNull String configFileName, @NotNull File defaultConfig) {
        return new ConfigWrapper(new File(pathToConfigDir + File.separator + configFileName + ".json"), defaultConfig);
    }

    /**
     * You shouldn't use this constructor directly, 'of' method instead
     * @see #of(String, String, File)
     */
    private ConfigWrapper(@NotNull File config, @NotNull File defaultConfig) {
        try {
            this.defaultConfig.putAll(loadConfig(defaultConfig.getPath()));
        } catch (IOException e) {
            JSONConfig4Java.LOGGER.error("Failed to load default config file [" + defaultConfig.getPath() + "]!", e);
        }

        if (!config.exists()) {
            try {
                var defConfigMap = new Gson().fromJson(
                        Files.newBufferedReader(defaultConfig.toPath(), StandardCharsets.UTF_8),
                        Map.class);
                createConfig(config.getPath(), defConfigMap
                );
            } catch (IOException e) {
                isBroken = true;
                JSONConfig4Java.LOGGER.error("Failed to generate [" + config.getPath() + "] config file!", e);
            }
        }

        if (!isBroken) {
            try {
                this.config.putAll(loadConfig(config.getPath()));
            } catch (IOException e) {
                isBroken = true;
                JSONConfig4Java.LOGGER.error("Failed to load [" + config.getPath() + "] config file", e);
            }
        }

        if (isBroken)
            this.config.putAll(this.defaultConfig);

        this.configFile = config;
    }

    private ConfigWrapper(@NotNull Map<String, JSONValue> config, @NotNull Map<String, JSONValue> defaultConfig, @NotNull File configFile) {
        this.config.putAll(config);
        this.defaultConfig.putAll(defaultConfig);
        this.configFile = configFile;
    }

    /**
     * Quires a value from config, returns null if the key does not exist
     *
     * @return value of corresponding key
     * @deprecated Use {@link #getRawOrDefault(String, Object)} instead
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Nullable
    public Object getRaw(String key) {
        var val = this.config.get(key);
        return val != null ? val.value() : null;
    }

    public Object getRawOrDefault(String key, Object def) {
        var val = getRaw(key);
        return val != null ? val : def;
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Nullable
    public Object getRawFromDefault(String key) {
        var val = this.defaultConfig.get(key);
        return val != null ? val.value() : null;
    }

    public JSONValue get(String key) {
        return JSONValue.of(getRaw(key));
    }

    public JSONValue getOrDefault(String key, Object def) {
        return JSONValue.of(getRawOrDefault(key, def));
    }

    public JSONValue getFromDefault(String key) {
        return JSONValue.of(getRawFromDefault(key));
    }

    /**
     * Shortcut for "JSONValue.of(config.get("some_key")).toJavaValue()"
     * @see JSONValue#toJavaValue(Object)
     */
    public <T> T getAsJavaValue(String key, T def) {
        return get(key).toJavaValue(def);
    }

    /**
     * Creates a copy of whole json config
     * @return a copy of json config
     */
    public @NotNull ConfigWrapper copy() {
        return new ConfigWrapper(this.config, this.defaultConfig, this.configFile);
    }

    public @NotNull Map<String, JSONValue> config() {
        return Map.copyOf(this.config);
    }

    public @NotNull Map<String, JSONValue> defaultConfig() {
        return Map.copyOf(this.defaultConfig);
    }

    /**
     * Check if the config is broken
     * @return {@link #isBroken}
     */
    public boolean isBroken() {
        return isBroken;
    }

    /**
     * Deletes the config file from the config files directory
     * @return value of File.delete() function
     */
    public boolean delete() {
        if (!configFile.exists()) {
            JSONConfig4Java.LOGGER.warn("Config file [" + configFile.getPath() + "] was already deleted!");
            return false;
        }
        JSONConfig4Java.LOGGER.warn("Config file [" + configFile.getName() + "] was deleted. Please, restart the game to regenerate it!");
        return configFile.delete();
    }
}
