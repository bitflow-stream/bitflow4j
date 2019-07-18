package bitflow4j.script;

import bitflow4j.script.registry.PipelineConstructorRegistryTest;
import com.google.gson.Gson;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringContains.containsString;

public class MainTest {

    private ByteArrayOutputStream baos;
    private PrintStream originalOut;
    private PrintStream originalErr;

    private File dataFile;
    private static final String testSamples = "time,val\n" +
            "2006-01-02 15:04:05.999999980,11111\n" +
            "2006-01-02 15:04:05.999999981,22222\n" +
            "2006-01-02 15:04:05.999999982,33333\n" +
            "2006-01-02 15:04:05.999999983,44444\n";

    @BeforeEach
    public void setup() {
        try {
            dataFile = File.createTempFile("test-data-", ".csv");
            dataFile.deleteOnExit();
            PrintWriter writer = new PrintWriter(dataFile, StandardCharsets.UTF_8);
            writer.write(testSamples);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String writeScriptFile(String script) throws IOException {
        File tempFile = File.createTempFile("test-pipeline-", ".bf");
        tempFile.deleteOnExit();
        PrintWriter writer = new PrintWriter(tempFile, StandardCharsets.UTF_8);
        writer.println(script);
        writer.close();
        return tempFile.getAbsolutePath();
    }

    private void startCaptureOutput() {
        originalErr = System.err;
        originalOut = System.out;
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        System.setErr(new PrintStream(baos));
    }

    private String stopCaptureOutput() {
        System.out.flush();
        System.err.flush();
        System.setOut(originalOut);
        System.setErr(originalErr);
        String output = baos.toString();
        System.out.println(output);
        return output;
    }

    @Test
    public void testCapabilitiesPrinting() throws IOException {
        String console = callMainWithArgs("--capabilities");
        assertThat(console, containsString("- test-no-params"));
        assertThat(console, containsString("- test-optional-params"));
        assertThat(console, containsString("- test-params-marked-constructor"));
        assertThat(console, containsString("- test-params-single-constructor"));
    }

    @Test
    public void testJSONCapabilitiesPrinting() throws IOException {
        String console = callMainWithArgs("--json-capabilities");

        assertThat(console, containsString("test-no-params"));
        assertThat(console, containsString("test-optional-params"));
        assertThat(console, containsString("test-params-marked-constructor"));
        assertThat(console, containsString("test-params-single-constructor"));

        // throws Exception if not valid json
        new Gson().fromJson(console, HashMap.class);
    }

    @Test
    @Timeout(5)
    public void testFileInputAndOutput() throws IOException {
        Path tempDir = Files.createTempDirectory("bitflow-test-directory");
        tempDir.toFile().deleteOnExit();
        String outFile = tempDir.toAbsolutePath() + "/output-file";

        String scriptFileName = writeScriptFile(dataFile.getAbsolutePath() + " ->test-no-params()->test-no-params()->" + outFile);
        String console = callMainWithArgs("--file ", scriptFileName);

        assertThat(console, not(containsString("Error")));
        assertThat(console, not(containsString("Exception")));
    }

    @Test
    @Timeout(5)
    public void testFileInputAndConsoleOutput() throws IOException {
        String scriptFileName = writeScriptFile(dataFile.getAbsolutePath() + " ->test-no-params()->test-no-params()-> -");
        String console = callMainWithArgs("--file", scriptFileName);

        assertThat(console, not(containsString("Error")));
        assertThat(console, not(containsString("Exception")));

        // assert console contains each sample value
        assertThat(console, containsString("11111"));
        assertThat(console, containsString("22222"));
        assertThat(console, containsString("33333"));
        assertThat(console, containsString("44444"));
    }

    private String callMainWithArgs(String... args) throws IOException {
        String[] extraArgs = new String[]{"--scan", PipelineConstructorRegistryTest.SCRIPT_PACKAGE};
        startCaptureOutput();
        new Main().executeMain(ArrayUtils.addAll(extraArgs, args));
        return stopCaptureOutput();
    }

}
