package bitflow4j.main.script;

import bitflow4j.main.registry.AnalysisRegistration;
import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BitflowScriptCliTest {
    private ByteArrayOutputStream baos;
    private PrintStream originalOut;
    private PrintStream originalErr;
    private String testSamples = "time,val\n" +
            "2006-01-02 15:04:05.999999980,11111\n" +
            "2006-01-02 15:04:05.999999981,22222\n" +
            "2006-01-02 15:04:05.999999982,33333\n" +
            "2006-01-02 15:04:05.999999983,44444\n";

    @Before
    public void setup(){
        try {
            PrintWriter writer = new PrintWriter("./test_data", "UTF-8");
            writer.write(testSamples);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    @After
    public void cleanup(){
        try {
            Files.deleteIfExists(Paths.get("./test_data"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String writeScriptFile(String script) throws FileNotFoundException, UnsupportedEncodingException {
        String fileName = "test-pipeline-" + UUID.randomUUID().toString();
        PrintWriter writer = new PrintWriter(fileName, "UTF-8");
        writer.println(script);
        writer.close();
        return fileName;
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
    public void testCapabilitiesPrinting() {
        String args = "--capabilities";

        String console = callMainWithArgs(args);

        assertTrue(console.contains("MixedParamStep:"));
        assertTrue(console.contains("RequiredParamStep:"));
    }

    @Test
    public void testJSONCapabilitiesPrinting() {
        String args = "--json-capabilities";

        String console = callMainWithArgs(args);

        assertTrue(console.contains("MixedParamStep"));
        assertTrue(console.contains("RequiredParamStep"));
        // throws Exception if not valid json
        new Gson().fromJson(console, AnalyisList.class);
    }

    @Test
    public void testFileInputAndOutput() throws IOException {
        String fileName = writeScriptFile("./test_data->MixedParamStep->MixedParamStep->./test_outputfile");
        String args = "--file " + fileName;
        try {
            String console = callMainWithArgs(args);

            assertFalse(console.contains("Error"));
            assertFalse(console.contains("Exception"));
        } finally {
            Files.delete(Paths.get(fileName));
            Files.delete(Paths.get("./test_outputfile-0"));
        }
    }


    @Test
    public void testFileInputAndConsoleOutput() throws IOException {
        String fileName = writeScriptFile("./test_data->MixedParamStep->MixedParamStep->-");
        String args = "--file " + fileName;

        try {
            String console = callMainWithArgs(args);

            assertFalse(console.contains("Error"));
            assertFalse(console.contains("Exception"));
            // assert console contains each sample value
            assertTrue(console.contains("11111"));
            assertTrue(console.contains("22222"));
            assertTrue(console.contains("33333"));
            assertTrue(console.contains("44444"));
        } finally {
            Files.delete(Paths.get(fileName));
        }
    }


    private String callMainWithArgs(String args) {
        try {
            args = "--scan-packages bitflow4j.main.registry " + args;
            startCaptureOutput();
            BitflowScriptCli.main(args.split(" "));
            return stopCaptureOutput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class AnalyisList extends ArrayList<AnalysisRegistration> {
    }
}
