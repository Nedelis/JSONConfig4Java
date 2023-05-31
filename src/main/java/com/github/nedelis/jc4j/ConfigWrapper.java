package com.github.nedelis.jc4j;

import com.github.nedelis.jc4j.jsonvalue.IJSONValueType;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * Wrapper for config files and default config files
 */
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
     * Creates new {@link ConfigWrapper} from provided path to config(-s) directory, config file name without extension
     * and default config file
     * @param configFileName config file name without extension
     * @param defaultConfig default config file (you may want to put it in your mod's resources folder)
     * @param pathToConfigDir path to directory, that contains config(-s)
     * @return new {@link ConfigWrapper} with loaded config values
     */
    public static @NotNull ConfigWrapper of(@NotNull String pathToConfigDir, @NotNull String configFileName, @NotNull File defaultConfig) {
        return new ConfigWrapper(new File(pathToConfigDir + File.separator + configFileName + ".json"), defaultConfig);
    }

    /**
     * Creates new {@link ConfigWrapper} from provided config file and provided default config file
     * @param configFile config file to be wrapped
     * @param defaultConfigFile default config file to be wrapped
     * @return new {@link ConfigWrapper} with loaded config values
     */
    public static @NotNull ConfigWrapper of(@NotNull File configFile, @NotNull File defaultConfigFile) {
        return new ConfigWrapper(configFile, defaultConfigFile);
    }

    /**
     * Creates new {@link ConfigWrapper} from provided config file and default config map (default config values)
     * @param configFile config file to be wrapped
     * @param defaultConfigMap default config map (default config values)
     * @return new {@link ConfigWrapper} with loaded config values
     */
    public static @NotNull ConfigWrapper of(@NotNull File configFile, @NotNull Map<?, ?> defaultConfigMap) {
        return new ConfigWrapper(configFile, defaultConfigMap);
    }

    /**
     * Creates new {@link ConfigWrapper} from provided path to config(-s) directory, config file name without extension
     * and default config map (default config values)
     * @param pathToConfigDir path to config(-s) directory
     * @param configFileName config file name without extension
     * @param defaultConfigMap default config map (default config values)
     * @return new {@link ConfigWrapper} with loaded config values
     */
    public static @NotNull ConfigWrapper of(@NotNull String pathToConfigDir, @NotNull String configFileName, @NotNull Map<?, ?> defaultConfigMap) {
        return new ConfigWrapper(new File(pathToConfigDir + File.separator + configFileName + ".json"), defaultConfigMap);
    }

    /**
     * You shouldn't use this constructor directly, 'of' method instead.
     * Initializes config wrapper with provided config file and provided default config file
     * @param config config file
     * @param defaultConfig default config file
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
            } catch (IOException ioe) {
                isBroken = true;
                JSONConfig4Java.LOGGER.error("Failed to load [" + config.getPath() + "] config file", ioe);
            }
        }

        if (isBroken)
            this.config.putAll(this.defaultConfig);

        this.configFile = config;
    }

    /**
     * You shouldn't use this constructor directly, 'of' method instead.
     * Initializes config wrapper with provided config file and provided default config content
     * @param config config file
     * @param defaultConfig default config content
     * @see #of(String, String, File)
     */
    private ConfigWrapper(@NotNull File config, @NotNull Map<?, ?> defaultConfig) {
        this.defaultConfig.putAll(defaultConfig.entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey().toString(),
                entry -> JSONValue.of(entry.getValue())
        )));

        if (!config.exists()) {
            try {
                createConfig(config.getPath(), defaultConfig);
            } catch (IOException e) {
                isBroken = true;
                JSONConfig4Java.LOGGER.error("Failed to generate [" + config.getPath() + "] config file!", e);
            }
        }

        if (!isBroken) {
            try {
                this.config.putAll(loadConfig(config.getPath()));
            } catch (IOException ioe) {
                isBroken = true;
                JSONConfig4Java.LOGGER.error("Failed to load [" + config.getPath() + "] config file", ioe);
            }
        }

        if (isBroken)
            this.config.putAll(this.defaultConfig);

        this.configFile = config;
    }

    /**
     * This constructor should be called to create a copy of the wrapper
     * @param config content of the config/config_file (usually {@link #config})
     * @param defaultConfig content of the default_config/default_config_file (usually {@link #defaultConfig})
     * @param configFile config file, that will be stored in wrapper (usually {@link #configFile})
     */
    private ConfigWrapper(@NotNull Map<String, JSONValue> config, @NotNull Map<String, JSONValue> defaultConfig, @NotNull File configFile) {
        this.config.putAll(config);
        this.defaultConfig.putAll(defaultConfig);
        this.configFile = configFile;
    }

    /**
     * Quires a value from config, returns null if the key does not exist
     * @param key key to get the value
     * @return value of corresponding key
     * @deprecated Use {@link #getRawOrDefault(String, Object)} instead
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Nullable
    public Object getRaw(@NotNull String key) {
        var val = this.config.get(key);
        return val != null ? val.value() : null;
    }

    /**
     * Tries to get the value of corresponding key and if there is no such key in config,
     * returns provided default value
     * @param key key to get the value
     * @param def default value
     * @return value of corresponding key or default value
     * @see #getRawOrDefault(String)
     */
    @Nullable
    public Object getRawOrDefault(@NotNull String key, Object def) {
        var val = getRaw(key);
        return val != null ? val : def;
    }

    /**
     * Returns raw value corresponding to the provided key if config contains such key,
     * otherwise returns raw value from default config corresponding to the same key
     * @param key key to get value
     * @return raw value from config or raw value from default config corresponding to the same key
     */
    @Nullable
    public Object getRawOrDefault(@NotNull String key) {
        return getRawOrDefault(key, getRawFromDefault(key));
    }

    /**
     * Returns raw value from default config
     * If default config doesn't contain provided key, method will return null
     * @param key key to get value from default config
     * @return raw value from default config or null
     * @see #getFromDefault(String)
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    @Nullable
    public Object getRawFromDefault(@NotNull String key) {
        var val = this.defaultConfig.get(key);
        return val != null ? val.value() : null;
    }

    /**
     * Returns wrapped value from the config corresponding to the provided key
     * @param key key to get value
     * @return wrapped value from the config corresponding to the provided key
     */
    public JSONValue get(@NotNull String key) {
        return JSONValue.of(getRaw(key));
    }

    /**
     * Returns wrapped value from the config corresponding to the provided key if the config contains such key,
     * otherwise returns provided default value
     * @param key key to get value
     * @param def default value to return if key does not exist
     * @return wrapped value from the config corresponding to the provided key or default value
     * @see #getOrDefault(String)
     */
    public JSONValue getOrDefault(@NotNull String key, Object def) {
        return JSONValue.of(getRawOrDefault(key, def));
    }

    /**
     * Returns wrapped value from the config corresponding to the provided key if the config contains such key,
     * otherwise returns value from default config corresponding to the same key
     * @param key key to get value
     * @return wrapped value from the config corresponding to the provided key
     * or default value from default config corresponding to the same key
     */
    public JSONValue getOrDefault(@NotNull String key) {
        return getOrDefault(key, getFromDefault(key));
    }

    /**
     * Returns wrapped value from default config corresponding to the provided key
     * @param key key to get value
     * @return wrapped value from default config
     */
    public JSONValue getFromDefault(@NotNull String key) {
        return JSONValue.of(getRawFromDefault(key));
    }

    /**
     * Shortcut for "get(String).toJavaValue(Object)"
     * @param key key to get value from config
     * @param def default value that will be returned if conversion would fail
     * @param <T> type of required value
     * @return converted value corresponding to the provided key
     * @see JSONValue#toJavaValue(Object)
     */
    public <T> T getAsJavaValue(@NotNull String key, T def) {
        return get(key).toJavaValue(def);
    }

    /**
     * Shortcut for "get(String).toJavaValue(IJSONValueType)".
     * If there is provided key in default config, it'll use convert function with default value,
     * if not it'll use convert function with null as default value
     * @param key key to get value from config (and from default config if it has such key)
     * @param type converter
     * @return value corresponding to the provided key or default value from default config or null
     * @param <T> type of required value
     * @see JSONValue#toJavaValue(Object)
     * @see IJSONValueType#convert(Object, Object)
     */
    public <T> T getAsJavaValue(@NotNull String key, @NotNull IJSONValueType<T> type) {
        return type.convert(getRaw(key), type.convert(getRawFromDefault(key), null));
    }

    /**
     * Puts provided value to the config under the given key.
     * After using this method, you should call {@link #saveConfig()} function to save all changes to the config file
     * @param key key which will represent provided value
     * @param value value to be stored
     */
    public void putInConfig(@NotNull String key, JSONValue value) {
        this.config.put(key, value);
        JSONConfig4Java.LOGGER.debug("Config was updated in program memory. Please, save it to file via saveConfig function");
    }

    /**
     * Puts all provided values to the config.
     * After using this method, you should call {@link #saveConfig()} to save all changes to the config file
     * @param toPut values to put
     */
    public void putAllInConfig(@NotNull Map<String, JSONValue> toPut) {
        this.config.putAll(toPut);
        JSONConfig4Java.LOGGER.debug("Config was updated in program memory. Please, save it to file via saveConfig function");
    }

    /**
     * This method saves the {@link #config} to the config file
     * @return true if method can save changes to the config file, false otherwise
     */
    public boolean saveConfig() {
        JSONConfig4Java.LOGGER.debug("Trying to save config to file...");
        JSONConfig4Java.LOGGER.doWithoutLogging(this::delete);
        var temp = new LinkedHashMap<>();
        var keys = new ArrayList<>(config.keySet().stream().toList());
        Collections.reverse(keys);
        for (var key : keys)
            temp.put(key, getRaw(key));
        try {
            createConfig(configFile.getPath(), temp);
            JSONConfig4Java.LOGGER.debug("Successfully saved config to file");
            return true;
        } catch (IOException ioe) {
            JSONConfig4Java.LOGGER.error("Unable to save config to the file!", ioe);
            return false;
        }
    }

    /**
     * This method puts provided value to the config under the given key and immediately saves changes to the config file
     * @param key key which will represent provided value
     * @param value value to be stored
     * @return true if method can save changes to the config file, false otherwise
     * @see #putInConfig(String, JSONValue)
     * @see #saveConfig()
     */
    public boolean putInConfigAndSave(@NotNull String key, JSONValue value) {
        putInConfig(key, value);
        return saveConfig();
    }

    /**
     * Puts all provided values to the config and immediately saves changes to the config file
     * @param toPut values to put
     * @return true if method can save changes to the config file, false otherwise
     * @see #putInConfig(String, JSONValue)
     * @see #saveConfig()
     */
    public boolean putAllInConfigAndSave(@NotNull Map<String, JSONValue> toPut) {
        putAllInConfig(toPut);
        return saveConfig();
    }

    /**
     * Creates a copy of whole json config
     * @return a copy of json config
     */
    public @NotNull ConfigWrapper copy() {
        return new ConfigWrapper(this.config, this.defaultConfig, this.configFile);
    }

    /**
     * Creates a copy of the {@link #config} and returns it
     * @return a copy of the config
     */
    public @NotNull Map<String, JSONValue> config() {
        return Map.copyOf(this.config);
    }

    /**
     * Creates a copy of the {@link #defaultConfig} and returns it
     * @return a copy of the default config
     */
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
