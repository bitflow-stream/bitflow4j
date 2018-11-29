package bitflow4j.script;

import bitflow4j.Pipeline;
import bitflow4j.misc.TreeFormatter;
import bitflow4j.script.endpoints.EndpointFactory;
import bitflow4j.script.registry.Registry;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Arrays;
import java.util.List;

public class ScriptCompilationTest extends TestCase {

    public void script(boolean fail, String script, String... expected) {
        Registry registry = new Registry();
        registry.scanForPipelineSteps();
        BitflowScriptCompiler compiler = new BitflowScriptCompiler(registry, new EndpointFactory());
        BitflowScriptCompiler.CompileResult res = compiler.parseScript(script);
        if (fail) {
            assertTrue(res.hasErrors());
            assertNull(res.getPipeline());
            Assert.assertArrayEquals(expected, res.getErrors().toArray(new String[0]));
        } else {
            assertFalse("Unexpected compilation errors: " + res.getErrors(), res.hasErrors());
            Pipeline pipe = res.getPipeline();
            List<String> steps = TreeFormatter.standard.formatLines(pipe);
            assertTrue(Arrays.deepEquals(expected, steps.toArray(new String[0])));
        }
    }

    public void testEmpty() {
        script(true, "", "Line 1 (0) '<EOF>': mismatched input '<EOF>' expecting {STRING, NUMBER, NAME}");
    }

    public void testInput() {
        script(false, "file.csv", "file.csv");
        script(false, ":8888", ":8888");
        script(false, "192.168.0.1:7777", "192.168.0.1:7777");
        script(false, "-", "-");
    }

}
