package com.github.nedelis.jc4j.jsonvalue;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is used to wrap a json value; After wrapping a json value you can
 * call {@link #toJavaValue(Object)} which will return you converted JSONValue
 */
@SuppressWarnings("unused")
public record JSONValue(Object value) {

    /**
     * Shortcut for JSONValue constructor
     * @param value some json value
     * @return new JSONValue
     */
    @Contract("_ -> new")
    public static @NotNull JSONValue of(Object value) {
        return new JSONValue(value);
    }

    /**
     * Converts list of objects to a list of JSON values
     * @param values values to convert
     * @return list of JSON values
     */
    public static List<JSONValue> objectsToJsonValues(@NotNull List<?> values) {
        return values.stream().map(JSONValue::of).toList();
    }

    /**
     * Converts map of objects to a map of JSON values
     * @param objectsMap objects map to convert
     * @return map of JSON values
     */
    public static Map<String, JSONValue> objectsMapToJsonValuesMap(@NotNull Map<String, ?> objectsMap) {
        return objectsMap.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, entry -> JSONValue.of(entry.getValue()))
        );
    }

    /**
     * Converting some json value into its representation in java; if value can't be converted,
     * returns the given default value
     * @param def default value that will be returned if the conversion failed; specifies the "T"
     * @return converted json value
     * @param <T> specifies which type the java value will be
     */
    public <T> T toJavaValue(T def) {
        return JSONValueType.toJavaValue(value, def);
    }

    public <T> T toJavaValue(@NotNull IJSONValueType<T> type) {
        return type.convert(value, null);
    }

}
