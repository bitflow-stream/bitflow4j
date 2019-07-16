package bitflow4j.script;

import bitflow4j.Pipeline;
import bitflow4j.misc.TreeFormatter;
import bitflow4j.script.endpoints.EndpointFactory;
import bitflow4j.script.registry.Registry;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptCompilationTest {

    File dataFile;
    String dataFileName;

    @BeforeEach
    public void setUp() {
        try {
            dataFile = File.createTempFile("test-data-", ".csv");
            dataFile.deleteOnExit();
            dataFileName = dataFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void script(boolean fail, String script, String... expected) {
        Registry registry = new Registry();
        registry.scanForProcessingSteps("bitflow4j");
        BitflowScriptCompiler compiler = new BitflowScriptCompiler(registry, new EndpointFactory());
        CompilationException exc = null;
        Pipeline pipe = null;

        try {
            pipe = compiler.parseScript(script);
        } catch (CompilationException e) {
            exc = e;
        }

        if (fail) {
            assertNotNull(exc);
            assertNull(pipe);
            assertEquals(expected[0], exc.getMessage());
        } else {
            if (exc != null)
                throw exc;
            assertNotNull(pipe);
            List<String> expectedList = Lists.newArrayList(expected);
            expectedList.add(0, "Pipeline");
            List<String> steps = TreeFormatter.standard.formatLines(pipe);
            assertArrayEquals(expectedList.toArray(new String[0]), steps.toArray(new String[0]));
        }
    }

    @Test
    public void testEmpty() {
        script(true, "", "Line 1 (0) '<EOF>': mismatched input '<EOF>' expecting {'{', 'batch', STRING, IDENTIFIER}");
    }

    @Test
    public void testInput() {
        script(false, dataFileName, "└─Reading file: " + dataFileName + " (format CSV)");
        script(false, ":8888", "└─Listen for incoming samples on :8888 (format BIN)");
        script(false, "192.168.0.1:7777", "└─Download samples from [192.168.0.1:7777] (format BIN)");
        script(false, "-", "└─Reading from stdin (format CSV)");
    }

    @Test
    public void testNet() {
        script(false, " :7777 -> csv://- ",
                "├─Listen for incoming samples on :7777 (format BIN)",
                "└─Writing to stdout (format CSV)");
    }

    @Test
    public void testUnwrapStrings() {
        script(false, " ':7777' -> `test-optional-params`( `requiredArg` = '55', \"boolArg\" = `true` ) -> \"csv://-\" ",
                "├─Listen for incoming samples on :7777 (format BIN)",
                "├─a TestOptionalParamsStep",
                "└─Writing to stdout (format CSV)");
    }

    @Test
    public void testSomeSteps() {
        script(false, dataFileName + " -> test-no-params()->inter_file ->test-no-params()->out.bin",
                "├─Reading file: " + dataFileName + " (format CSV)",
                "├─a TestNoParamsStep",
                "├─Writing file inter_file (append: false, deleteFiles: false, format: CSV)",
                "├─a TestNoParamsStep",
                "└─Writing file out.bin (append: false, deleteFiles: false, format: BIN)");
        script(false, "  " + dataFileName + "  ->  test-no-params   ( )  ->  test-no-params (  )  ->  out.bin   ",
                "├─Reading file: " + dataFileName + " (format CSV)",
                "├─a TestNoParamsStep",
                "├─a TestNoParamsStep",
                "└─Writing file out.bin (append: false, deleteFiles: false, format: BIN)");
    }

}
