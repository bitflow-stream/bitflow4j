package bitflow4j.script.antlr;

import bitflow4j.main.AlgorithmPipeline;

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
        String rawScript;
        if ("-f".equals(args[0]) || "--file".equals(args[0])) {
            rawScript = readFile(args[1]);
        } else {
            rawScript = args[0];
        }


        AlgorithmPipeline algorithmPipeline = new BitflowScriptCompiler().ParseScript(rawScript);
        System.out.println(algorithmPipeline);
    }

    public static String readFile(String filename) throws IOException {
        File file = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }
}
