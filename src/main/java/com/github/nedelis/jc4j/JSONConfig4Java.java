package com.github.nedelis.jc4j;

import com.github.nedelis.jc4j.logging.JC4JLogLevel;
import com.github.nedelis.jc4j.logging.JC4JLogger;
import com.github.nedelis.jc4j.logging.JC4JLoggerBuilder;

/**
 * Contains all global constants/variables and methods
 */
public class JSONConfig4Java {

    public static JC4JLogger LOGGER = new JC4JLoggerBuilder("JC4J").setLogLevel(JC4JLogLevel.ALL).build();

}
