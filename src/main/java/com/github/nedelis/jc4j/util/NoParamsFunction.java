package com.github.nedelis.jc4j.util;

/**
 * Allows to create functions without arguments using lambda
 * @param <R> Type of the function's return value
 */
@FunctionalInterface
public interface NoParamsFunction<R> {

    R apply();

}
