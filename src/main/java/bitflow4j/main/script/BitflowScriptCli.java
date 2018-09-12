package bitflow4j.main.script;

import bitflow4j.main.registry.Registry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class BitflowScriptCli {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Script not specified.\nSample usages:\n  java -jar bitflow.jar <script>\n  java -jar bitflow.jar [-f|--file] <filename>");
            return;
        }

        String rawScript = readRawScript(args);

        Registry registry = new Registry();
        registry.scanForPipelineSteps();
        BitflowScriptCompiler compiler = new BitflowScriptCompiler(registry);
        BitflowScriptCompiler.CompileResult res = compiler.ParseScript(rawScript);

        if (res.hasErrors()) {
            System.err.println("Errors occurred during execution:");
            res.getErrors().stream().map(s -> "\t" + s).forEach(System.err::println);
            return;
        }

        res.getPipeline().runAndWait(true);
    }

    private static String readRawScript(String[] args) throws IOException {
        if ("-f".equals(args[0]) || "--file".equals(args[0])) {
            File file = new File(args[1]);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } else {
            return args[0];
        }
    }
}
