import com.github.nedelis.jc4j.ConfigWrapper;
import com.github.nedelis.jc4j.jsonvalue.JSONValueType;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ConfigWrapperTest {

    public static final File defaultConfig = new File(Objects.requireNonNull(ConfigWrapperTest.class.getClassLoader().getResource("def_config.json")).getPath());
    public static final String pathToResources;
    public static final ConfigWrapper wrapper;
    static {
        var pathArr = new ArrayList<>(Arrays.stream(defaultConfig.getPath().split("\\\\")).toList());
        pathArr.remove(pathArr.size() - 1);
        pathToResources = String.join("\\", pathArr);
        wrapper = ConfigWrapper.of(pathToResources, "config.json", defaultConfig);
    }

    @Test
    void testConfigGetFunctionsAndConversion() {
        Assertions.assertEquals(10, wrapper.getAsJavaValue("var1", JSONValueType.INT));
    }

    @Test
    void testConfigRuntimeChange() {

    }
}
