package com.github.nedelis.jc4j.jsonvalue;

import com.github.nedelis.jc4j.JSONConfig4Java;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class allows you to convert any value from a json to a java value;
 * You also can register your own java value types by using {@link JSONValueType#register(String, IJSONValueType)}
 * <p>
 * All registered types will be stored in the {@link JSONValueType#TYPES}; If you need to get definite
 * IJSONValueType you can use saved constants of them
 */
@SuppressWarnings("unused")
public final class JSONValueType {

    private static final Map<String, IJSONValueType<?>> TYPES = new HashMap<>();

    public static final IJSONValueType<String> STR = register("STR", new IJSONValueType<>() {
        @Override
        public boolean checkForInstance(Object toCheck) {
            return toCheck instanceof String;
        }

        @Override
        public String convert(Object toConvert, String def) {
            JSONConfig4Java.LOGGER.debug("Trying to convert '" + toConvert + "' to a string...");
            if (toConvert instanceof String str) {
                JSONConfig4Java.LOGGER.debug("Successfully converted '" + str + "' to a string");
                return str;
            }
            JSONConfig4Java.LOGGER.debug("Unable to convert '" + toConvert + "' to a string, because it is not a string!");
            return def;
        }
    });
    public static final IJSONValueType<Integer> INT = register("INT", new IJSONValueType<>() {
        @Override
        public boolean checkForInstance(Object toCheck) {
            return toCheck instanceof Integer;
        }

        @Override
        public Integer convert(Object toConvert, Integer def) {
            JSONConfig4Java.LOGGER.debug("Trying to convert '" + toConvert + "' to an integer...");
            var d = JSONConfig4Java.LOGGER.doWithoutLogging(() -> DOUBLE.convert(toConvert, null));
            if (d == null) {
                JSONConfig4Java.LOGGER.debug("Unable to convert '" + toConvert + "' to an integer, because it is not a number!");
                return def;
            }
            JSONConfig4Java.LOGGER.debug("Successfully converted '" + d.intValue() + "' to an integer");
            return d.intValue();
        }
    });
    public static final IJSONValueType<Double> DOUBLE = register("DOUBLE", new IJSONValueType<>() {
        @Override
        public boolean checkForInstance(Object toCheck) {
            return toCheck instanceof Double;
        }

        @Override
        public Double convert(Object toConvert, Double def) {
            JSONConfig4Java.LOGGER.debug("Trying to convert '" + toConvert + "' to a double...");
            if (toConvert instanceof Double d) {
                JSONConfig4Java.LOGGER.debug("Successfully converted '" + d + "' to a double");
                return d;
            }
            JSONConfig4Java.LOGGER.debug("Unable to convert '" + toConvert + "' to a double, because it is not a double!");
            return def;
        }
    });
    public static final IJSONValueType<Boolean> BOOL = register("BOOL", new IJSONValueType<>() {
        @Override
        public boolean checkForInstance(Object toCheck) {
            return toCheck instanceof Boolean;
        }

        @Override
        public Boolean convert(Object toConvert, Boolean def) {
            JSONConfig4Java.LOGGER.debug("Trying to convert '" + toConvert + "' to a boolean...");
            if (toConvert instanceof Boolean bool) {
                JSONConfig4Java.LOGGER.debug("Successfully converted '" + bool + "' to a boolean");
                return bool;
            }
            JSONConfig4Java.LOGGER.debug("Unable to convert '" + toConvert + "' to a double, because it is not a boolean!");
            return def;
        }
    });
    public static final IJSONValueType<ArrayList<JSONValue>> JS_VAL_LIST = register("JS_VAL_LIST", new IJSONValueType<>() {
        @Override
        public boolean checkForInstance(Object toCheck) {
            return toCheck instanceof List;
        }

        @Override
        public ArrayList<JSONValue> convert(Object toConvert, ArrayList<JSONValue> def) {
            JSONConfig4Java.LOGGER.debug("Trying to convert '" + toConvert + "' to a list of JSON values...");
            if (toConvert instanceof List<?> list) {
                JSONConfig4Java.LOGGER.debug("Successfully converted '" + list + "' to a list of JSON values");
                return new ArrayList<>(JSONValue.objectsToJsonValues(list));
            }
            JSONConfig4Java.LOGGER.debug("Unable to convert '" + toConvert + "' to a list of JSON values, because it is not a list!");
            return def;
        }
    });
    public static final IJSONValueType<HashMap<String, JSONValue>> JS_VAL_MAP = register("JS_VAL_MAP", new IJSONValueType<>() {
        @Override
        public boolean checkForInstance(Object toCheck) {
            return toCheck instanceof Map;
        }

        @Override
        public HashMap<String, JSONValue> convert(Object toConvert, HashMap<String, JSONValue> def) {
            JSONConfig4Java.LOGGER.debug("Trying to convert '" + toConvert + "' to a map of JSON values...");
            if (toConvert instanceof Map<?, ?> map) {
                JSONConfig4Java.LOGGER.debug("Successfully converted '" + map + "' to a map of JSON values");
                return new HashMap<>(map.entrySet().stream().collect(
                        Collectors.toMap(entry -> entry.getKey().toString(), entry -> JSONValue.of(entry.getValue()))
                ));
            }
            JSONConfig4Java.LOGGER.debug("Unable to convert '" + toConvert + "' to a map of JSON values, because it is not a map!");
            return def;
        }
    });

    /**
     * @param name name of a json value type
     * @return {@link IJSONValueType} from {@link #TYPES} associated with the given name
     */
    private static IJSONValueType<?> get(String name) {
        return TYPES.get(name);
    }

    /**
     * This method tries to find IJSONValueType by calling
     * {@link IJSONValueType#checkForInstance(Object)} for each
     * registered IJSONValueType
     * @param instance instance object
     * @return definite IJSONValueType registered in {@link #TYPES} if provided object is instanceof its type
     * @param <T> specifies the type of returned IJSONValueType
     */
    @SuppressWarnings("unchecked")
    private static <T> @Nullable IJSONValueType<T> getByInstance(T instance) {
        for (var type : TYPES.values())
            if (type.checkForInstance(instance))
                return (IJSONValueType<T>) type;
        JSONConfig4Java.LOGGER.debug("Couldn't find JSONValueType for object '" + instance + "'!");
        return null;
    }

    /**
     * Registers a new IJSONValueType (puts it in {@link #TYPES} and returns it)
     * @param name name that will be associated with new IJSONValueType
     * @param type IJSONValueType that will be registered
     * @return provided IJSONValueType
     * @param <T> specifies the type of returned IJSONValueType
     */
    public static <T> IJSONValueType<T> register(String name, IJSONValueType<T> type) {
        TYPES.put(name, type);
        return type;
    }

    /**
     * This method tries to find correct
     * JSONValueType for "T def" and converts given value to java value; If it can't find
     * correct JSONValueType for "T def", then it'll issue a warning and return given default value
     * @param jsonValue value to convert
     * @param def default value that will be returned if conversion failed; specifies the "T"
     * @return converted json value
     * @param <T> specifies which type the java value will be
     */
    public static <T> T toJavaValue(Object jsonValue, T def) {
        var type = getByInstance(def);
        if (type != null)
            return type.convert(jsonValue, def);
        JSONConfig4Java.LOGGER.debug(
                "Error during converting '" +
                        jsonValue + "' to a '" + def.getClass().getName() +
                        "', because there is no such value type as '" +
                        def.getClass().getName() + "'!"
        );
        return def;
    }

    public static <T> T toJavaValue(Object jsonValue, @NotNull IJSONValueType<T> valueType) {
        return valueType.convert(jsonValue, null);
    }

    public static <T> T toJavaValue(Object jsonValue, T def, @NotNull IJSONValueType<T> valueType) {
        return valueType.convert(jsonValue, def);
    }

}
