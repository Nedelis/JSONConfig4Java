# JSONConfig4Java

Welcome to the JC4J! This library provides you with easy management of your .json configuration files!
With JC4J you can create configs and easily read them avoiding unsafe casts and boilerplate code.

<a style="margin-left:20px" href="https://github.com/nedelis/jsonconfig4java">
    <!--suppress CheckImageSize -->
    <img src="./logo.png" alt="logo of the project" width="200" height="200" title="logo">
</a>

**This README is for the version 3.4b!**

**If you have any questions, or you have found bugs, please contact with me in my discord — `Nedelis#9496`!**

**You can find docs [here](https://nedelis.github.io/JSONConfig4Java/)**

## Table of contents

1. [Installation](#installation)
2. [Set up the Logger](#set-up-the-logger)
3. [Usage](#usage)
4. [Future updates](#future-updates)

## Installation

You have 2 options to install this library.

#### First

1. Download the latest release from the [releases page](https://github.com/nedelis/jsonconfig4java/releases)
2. Import downloaded `.JAR` file into your project
3. That's all.

#### Second

*soon...*

[Return to the table of contents](#table-of-contents)

## Set up the Logger

This library uses custom logging system, that can be configured in the way you want.
By default, logger has name `JC4J`, logging level `ALL` and message pattern `HH:mm:ss logger-name [message-log-level] message`.
To change these settings, you need reassign `LOGGER` variable that can be found in the class `JSONConfig4Java`.
Here is an example:

```java
// package ...;

// import ...;

@SuppressWarnings("ALL")
public class Foo {

    // You should customize logger in the initialization function (for example, here, I do it in the main function).
    public static void main(String[] args) {

        // first, create a new logger
        JC4JLogger NEW_LOGGER = new JC4JLoggerBuilder(/*here you can put custom name*/).
                setPathToLogFile(Path.of("./logs/log.log")).
                setLogLevel(JC4JLogLevel.ALL).
                /*you can also customize message pattern by setPattern()*/
                        build();

        // next, reassign the default logger
        JSONConfig4Java.LOGGER = NEW_LOGGER;

    }

}
```

That's all. If you want to find out details about logging system, you can read javadocs.

[Return to the table of contents](#table-of-contents)

## Usage

In this section, I'll give you 2 examples. First will be simple and second will be hard.
But, at first, I recommend you to read [javadocs](https://nedelis.github.io/JSONConfig4Java/) during reading this examples.

[Return to the table of contents](#table-of-contents)

Examples list:

1. [Simple Example](#simple-example)
2. [Hard Example](#hard-example)

### Simple Example

Well, let's imagine that we have this default config, at the directory `./resources/default_settings.json`:

```json
{
  "window-size": "1000:1000",
  "loudness": 100,
  "gui-size-amplifier": 1.25,
  "auto-save": true
}
```

So, now we need to get all these settings. Let's create a new class that will contain all settings.

```java
// package ...;

// import... ;

public class Settings {
    
    // At first, let's wrap the config with wrapper
    private static final ConfigWrapper configWrapper = ConfigWrapper.of(
            Path.of("./config").normalize().toString(),
            "config",
            Path.of("./resources/default_settings.json").normalize().toFile()
    );
    // The first parameter is the path to the modifiable config file that will be generated
    // Second is the name of the modifiable config file
    // Third is the default config file
    
    // Now, we are able to get all the settings
    public static final String WINDOW_SIZE = configWrapper.get("window-size").toJavaValue(JSONConfigValueType.STR);
    // So, at first we get parameter from the config, and then we convert it to java value.
    // p.s. function toJavaValue() at the JSONValue class has 2 variations:
    // here we used variation, that specifies return type, but also we could use default value instead
    public static final int LOUDNESS = configWrapper.getAsJavaValue("loudness", JSONConfigValueType.INT);
    public static final double GUI_AMPLIFIER = configWrapper.getAsJavaValue("gui-size-amplifier", JSONConfigValueType.DOUBLE);
    public static final boolean AUTO_SAVE = configWrapper.getAsJavaValue("auto-save", JSONConfigValueType.BOOL);
    
}

```

That's all. You can find other ways to get config's values by reading [javadocs](https://nedelis.github.io/JSONConfig4Java/).

[Return to the table of contents](#table-of-contents)

[Return to the list of examples](#usage)

### Hard Example

Well, now let's imagine, that our last config became bigger:

```json
{
  "main-settings": {
    "gui": {
      "gui-size-amplifier": 1.25
    },
    "graphics": {
      "textures-resolution": "medium",
      "antialiasing": "MSAA 2x",
      "reflections": "medium",
      "sky-resolution": "medium",
      "draw-distance": "medium",
      "load-distance": "medium",
      "shadows": "ultra",
      "particles": "all",
      "ray-tracing": false
    },
    "sound": {
      "type": "stereo",
      "master-volume": 100,
      "surrounding": 100,
      "music": 100,
      "entities": 100,
      "objects": 100,
      "weather": 100
    }
  },
  "other-settings": {
    "auto-save": true
  },
  "modifications-list": [
    "com.example.mods:optimization-pack-v3.4"
  ]
}
```

Looks pretty big, isn't it? Well, now let's imagine that at some moment we need to get entities loudness, antialiasing type, modifications list and check whether auto-save is enabled or not.

```java
// package... ;

// import... ;

public class Settings {

    // Create wrapper
    private static final ConfigWrapper configWrapper = ConfigWrapper.of(
            Path.of("./config").normalize().toString(),
            "config",
            Path.of("./resources/default_settings.json").normalize().toFile()
    );
    
    public static final int SOUND_ENTITIES = configWrapper.
            getAsJavaValue("main-settings", JSONValueType.JS_VAL_MAP).
            get("sound").toJavaValue(JSONValueType.JS_VAL_MAP).
            get("entities").toJavaValue(JSONValueType.INT);
    // So, here, at first, we got the 'main-settings' map from the config
    // Then we got 'sound' map from the 'main-settings' map
    // And then we finally found 'entities' sound value
    public static final String ANTIALIASING_TYPE = configWrapper.
            getAsJavaValue("main-settings", JSONValueType.JS_VAL_MAP).
            get("graphics").toJavaValue(JSONValueType.JS_VAL_MAP).
            get("antialiasing").toJavaValue(JSONValueType.STR);
    
    public static final List<?> MODIFICATIONS = configWrapper.getAsJavaValue("modifications-list", JSONValueType.JS_VAL_LIST);
    public static final boolean AUTO_SAVE = configWrapper.
            getAsJavaValue("other-settings", JSONValueType.JS_VAL_MAP).
            get("auto-save").toJavaValue(JSONValueType.BOOL);
}
```

Phew, that's all! All other settings can be obtained in the same way. I hope that now you understand how to use JC4J!

[Return to the table of contents](#table-of-contents)

[Return to the list of examples](#usage)

## Future updates

Well, in future I'm going to add:
1. [ ] Config bundles
2. [x] Runtime config change
3. [ ] Links system that will allow you to get any config value by special string.

[Return to the table of contents](#table-of-contents)
