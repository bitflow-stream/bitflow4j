package bitflow4j.main.script;

import bitflow4j.script.Main;
import bitflow4j.script.registry.AnalysisRegistration;
import com.google.gson.Gson;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MainTest {
    private ByteArrayOutputStream baos;
    private PrintStream originalOut;
    private PrintStream originalErr;
    private String testSamples = "time,val\n" +
            "2006-01-02 15:04:05.999999980,11111\n" +
            "2006-01-02 15:04:05.999999981,22222\n" +
            "2006-01-02 15:04:05.999999982,33333\n" +
            "2006-01-02 15:04:05.999999983,44444\n";

    private File dataFile;

    @Before
    public void setup() {
        try {
            dataFile = File.createTempFile("test-data-", ".csv");
            dataFile.deleteOnExit();
            PrintWriter writer = new PrintWriter(dataFile, "UTF-8");
            writer.write(testSamples);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String writeScriptFile(String script) throws IOException {
        File tempFile = File.createTempFile("test-pipeline-", ".bf");
        tempFile.deleteOnExit();
        PrintWriter writer = new PrintWriter(tempFile, "UTF-8");
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

    public void testCapabilitiesPrinting() {
        String console = callMainWithArgs("--capabilities");

        assertTrue(console.contains("MixedParamStep:"));
        assertTrue(console.contains("RequiredParamStep:"));
    }

    public void testJSONCapabilitiesPrinting() {
        String console = callMainWithArgs("--json-capabilities");

        assertTrue(console.contains("MixedParamStep"));
        assertTrue(console.contains("RequiredParamStep"));
        // throws Exception if not valid json
        new Gson().fromJson(console, AnalyisList.class);
    }

    @Test(timeout = 2000000)
    public void testFileInputAndOutput() throws IOException {
        Path tempDir = Files.createTempDirectory("bitflow-test-directory");
        tempDir.toFile().deleteOnExit();
        String outFile = tempDir.toAbsolutePath() + "/output-file";

        String scriptFileName = writeScriptFile(dataFile.getAbsolutePath() + "->MixedParamStep->MixedParamStep->" + outFile);

        String console = callMainWithArgs("--file ", scriptFileName);

        assertFalse(console.contains("Error"));
        assertFalse(console.contains("Exception"));
    }

    @Test(timeout = 2000000)
    public void testFileInputAndConsoleOutput() throws IOException {
        String scriptFileName = writeScriptFile(dataFile.getAbsolutePath() + "->MixedParamStep->MixedParamStep->-");
        String console = callMainWithArgs("--file", scriptFileName);

        assertFalse(console.contains("Error"));
        assertFalse(console.contains("Exception"));

        // assert console contains each sample value
        assertTrue(console.contains("11111"));
        assertTrue(console.contains("22222"));
        assertTrue(console.contains("33333"));
        assertTrue(console.contains("44444"));
    }


    private String callMainWithArgs(String... args) {
        try {
            String[] extraArgs = new String[]{"--scan-packages", "bitflow4j.main.registry"};

            startCaptureOutput();
            Main.main(ArrayUtils.addAll(extraArgs, args));
            return stopCaptureOutput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class AnalyisList extends ArrayList<AnalysisRegistration> {
    }
}
