package bitflow4j.script;

import bitflow4j.Pipeline;
import bitflow4j.misc.TreeFormatter;
import bitflow4j.script.endpoints.EndpointFactory;
import bitflow4j.script.registry.Registry;
import com.google.common.collect.Lists;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ScriptCompilationTest extends TestCase {

    File dataFile;
    String dataFileName;

    @Before
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
        registry.scanForPipelineSteps("bitflow4j");
        BitflowScriptCompiler compiler = new BitflowScriptCompiler(registry, new EndpointFactory());
        BitflowScriptCompiler.CompileResult res = compiler.parseScript(script);
        if (fail) {
            assertTrue(res.hasErrors());
            assertNull(res.getPipeline());
            Assert.assertArrayEquals(expected, res.getErrors().toArray(new String[0]));
        } else {
            assertFalse("Unexpected compilation errors: " + res.getErrors(), res.hasErrors());
            Pipeline pipe = res.getPipeline();
            List<String> expectedList = Lists.newArrayList(expected);
            expectedList.add(0, "Pipeline");
            List<String> steps = TreeFormatter.standard.formatLines(pipe);
            Assert.assertArrayEquals(expectedList.toArray(new String[0]), steps.toArray(new String[0]));
        }
    }

    public void testEmpty() {
        script(true, "", "Line 1 (0) '<EOF>': mismatched input '<EOF>' expecting {'{', STRING, NAME}");
    }

    public void testInput() {
        script(false, dataFileName, "└─Reading file: " + dataFileName + " (format CSV)");
        script(false, ":8888", "└─Listen for incoming samples on :8888 (format BIN)");
        script(false, "192.168.0.1:7777", "└─Download samples from [192.168.0.1:7777] (format BIN)");
        script(false, "-", "└─Reading from stdin (format CSV)");
    }

    public void testNet() {
        script(false, " :7777 -> csv://- ",
                "├─Listen for incoming samples on :7777 (format BIN)",
                "└─Writing to stdout (format CSV)");
    }

    public void testUnwrapStrings() {
        script(false, " ':7777' -> `MixedParamStep`( `doubleArg` = '1.00', \"booleanArg\" = `true` ) -> \"csv://-\" ",
                "├─Listen for incoming samples on :7777 (format BIN)",
                "├─a MixedParamStep",
                "└─Writing to stdout (format CSV)");
    }

    public void testSomeSteps() {
        script(false, dataFileName + " -> MixedParamStep()->inter_file ->MixedParamStep()->out.bin",
                "├─Reading file: " + dataFileName + " (format CSV)",
                "├─a MixedParamStep",
                "├─Writing file inter_file (append: false, deleteFiles: false, format: CSV)",
                "├─a MixedParamStep",
                "└─Writing file out.bin (append: false, deleteFiles: false, format: BIN)");
        script(false, "  " + dataFileName + "  ->  MixedParamStep   ( )  ->  MixedParamStep (  )  ->  out.bin   ",
                "├─Reading file: " + dataFileName + " (format CSV)",
                "├─a MixedParamStep",
                "├─a MixedParamStep",
                "└─Writing file out.bin (append: false, deleteFiles: false, format: BIN)");
    }

}
