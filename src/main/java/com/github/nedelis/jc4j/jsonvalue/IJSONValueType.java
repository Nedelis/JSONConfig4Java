package com.github.nedelis.jc4j.jsonvalue;

/**
 * This interface represents any convertible json value
 * @param <RT> type that you want to be returned after conversion
 */
public interface IJSONValueType<RT> {

    /**
     * This method should return true if the given object is instanceof RT, false otherwise
     * @param toCheck object to check for instance
     * @return whether the given object is instance of RT (or something else) or not
     */
    boolean checkForInstance(Object toCheck);

    /**
     * This method should convert the given object to a definite java value
     * @param toConvert object to be converted
     * @param def default object that should be returned if the conversion fails
     * @return converted object with RT type
     */
    RT convert(Object toConvert, RT def);

}
