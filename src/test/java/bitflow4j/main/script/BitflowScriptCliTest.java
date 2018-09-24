package bitflow4j.main.script;

import bitflow4j.main.registry.AnalysisRegistration;
import com.google.gson.Gson;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BitflowScriptCliTest {
    private ByteArrayOutputStream baos;
    private PrintStream originalOut;
    private PrintStream originalErr;


    public void startCaptureOutput() {
        originalErr = System.err;
        originalOut = System.out;
        baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        System.setErr(new PrintStream(baos));
    }

    public String stopCaptureOutput() {
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
    public void testFileInput() throws IOException {
        String fileName = "test-pipeline-" + UUID.randomUUID().toString();
        String args = "--file " + fileName;
        PrintWriter writer = new PrintWriter(fileName, "UTF-8");
        writer.println("./inputFile->MixedParamStep->MixedParamStep->./outputfile");
        writer.close();
        try {
            String console = callMainWithArgs(args);

            assertTrue(!console.contains("Error"));
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
