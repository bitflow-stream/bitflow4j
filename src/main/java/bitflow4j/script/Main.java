package bitflow4j.script;

import bitflow4j.Pipeline;
import bitflow4j.misc.Config;
import bitflow4j.script.registry.Registry;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Main parses a BitflowScript and executes the resulting Pipeline.
 *
 * <pre>
 * Usage: Main [options] A raw bitflow script to be processed.
 *       Alternatively use the File option.
 *   Options:
 *     --help
 *
 *     -f, --file
 *       A file containing the script to parse.
 *     -p, --scan-packages
 *       Comma-separated package names that will be scanned automatically.
 *       Wildcards allowed.
 *       Default: *
 *     --capabilities
 *       Prints the capabilities of this jar in a human readable format.
 *       Default: false
 *     --json-capabilities
 *       Prints the capabilities of this jar in json format.
 *       Default: false
 *
 * </pre>
 */
public class Main {

    static {
        Config.initializeLogger();
    }

    public static void main(String[] args) throws IOException {
        CmdArgs cmdArgs = new CmdArgs();
        JCommander jc = JCommander.newBuilder()
                .allowAbbreviatedOptions(true)
                .programName("bitflow4j")
                .addObject(cmdArgs).build();

        try {
            jc.parse(args);
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            e.usage();
            return;
        }
        if (args.length == 0 | cmdArgs.printHelp) {
            jc.usage();
            return;
        }

        String rawScript = cmdArgs.getRawScript();

        Registry registry = new Registry();
        registry.scanForPipelineSteps(cmdArgs.cleanPackagesToScan());

        if (cmdArgs.printJsonCapabilities) {
            System.out.println(new Gson().toJson(registry.getCapabilities()));
            return;
        } else if (cmdArgs.printCapabilities) {
            registry.getCapabilities().forEach(a -> System.out.println(a.toString()));
            return;
        }

        BitflowScriptCompiler compiler = new BitflowScriptCompiler(registry);
        BitflowScriptCompiler.CompileResult res = compiler.ParseScript(rawScript);

        if (res.hasErrors()) {
            System.err.println("Errors occurred during execution:");
            res.getErrors().stream().map(s -> "\t" + s).forEach(System.err::println);
            return;
        }

        Pipeline pipe = res.getPipeline();
        pipe.logFormattedSteps();
        pipe.runAndWait();
    }

    private static String readRawScript(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    private static class CmdArgs {
        @Parameter(names = "--help", help = true, order = 0)
        private boolean printHelp = false;
        @Parameter(names = {"-f", "--file"}, description = "A file containing the script to parse.", order = 1)
        private String fileName = null;
        @Parameter(names = {"-p", "--scan-packages"}, description = "Comma-separated package names that will be scanned automatically. Wildcards allowed.", order = 2)
        private String packagesToScan = "*";
        @Parameter(names = "--capabilities", description = "Prints the capabilities of this jar in a human readable format.")
        private boolean printCapabilities = false;
        @Parameter(names = "--json-capabilities", description = "Prints the capabilities of this jar in json format.")
        private boolean printJsonCapabilities = false;
        @Parameter(description = "A raw bitflow script to be processed. Alternatively use the File option.")
        private List<String> scriptParts = new ArrayList<>();

        public String getRawScript() throws IOException {
            if (fileName != null) {
                return readRawScript(fileName);
            } else {
                return String.join(" ", scriptParts);
            }
        }

        public String[] cleanPackagesToScan() {
            if (packagesToScan.equals("*")) {
                return null;
            }
            return packagesToScan.split(",");
        }
    }

}
