package bitflow4j.script;

import bitflow4j.Pipeline;
import bitflow4j.misc.Config;
import bitflow4j.misc.TreeFormatter;
import bitflow4j.script.endpoints.EndpointFactory;
import bitflow4j.script.registry.AnalysisRegistration;
import bitflow4j.script.registry.Registry;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Main parses a BitflowScript and executes the resulting Pipeline.
 *
 * <pre>
 * Usage: bitflow4j.script.Main [options] [Bitflow Script]
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
 *     --pipeline
 *       Prints the pipeline steps resulting from parsing the input script and exits.
 *       Default: false
 * </pre>
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    static {
        Config.initializeLogger();
    }

    public static void main(String[] args) {
        try {
            executeMain(args);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Fatal error", t);
        }
    }

    public static void executeMain(String[] args) throws IOException {
        CmdArgs cmdArgs = new CmdArgs();
        JCommander jc = JCommander.newBuilder()
                .allowAbbreviatedOptions(true)
                .programName(Main.class.getCanonicalName())
                .addObject(cmdArgs).build();
        try {
            jc.parse(args);
        } catch (ParameterException e) {
            logger.severe(e.getMessage());
            e.usage();
            return;
        }
        if (args.length == 0 | cmdArgs.printHelp) {
            jc.usage();
            return;
        }
        cmdArgs.configureLogging();

        Registry registry = new Registry();
        registry.scanForPipelineSteps(cmdArgs.cleanPackagesToScan());

        if (cmdArgs.printJsonCapabilities) {
            System.out.println(new Gson().toJson(registry.getCapabilities()));
            return;
        } else if (cmdArgs.printCapabilities) {
            logCapabilities(registry);
            return;
        }

        if (!cmdArgs.isScriptValid()) {
            logger.severe("Please provide a script either as file using the -f parameter or as positional arguments");
            jc.usage();
        }
        String rawScript = cmdArgs.getRawScript();
        EndpointFactory endpoints = new EndpointFactory();
        BitflowScriptCompiler compiler = new BitflowScriptCompiler(registry, endpoints);
        BitflowScriptCompiler.CompileResult res = compiler.parseScript(rawScript);

        if (res.hasErrors()) {
            logger.severe("Failed to parse Bitflow script:");
            res.getErrors().stream().map(s -> "\t" + s).forEach(logger::severe);
            return;
        }

        Pipeline pipe = res.getPipeline();
        for (String line : TreeFormatter.standard.formatLines(pipe)) {
            logger.info(line);
        }
        if (cmdArgs.printPipeline)
            return;
        pipe.runAndWait();
    }

    private static String readRawScript(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    private static void logCapabilities(Registry registry) {
        List<AnalysisRegistration> cap = Lists.newArrayList(registry.getCapabilities());
        cap.sort((a1, a2) -> a1.getName().compareToIgnoreCase(a2.getName()));
        logIfNotEmpty("Stream-Mode Processing Steps:", cap.stream().filter(AnalysisRegistration::supportsStreamOnly));
        logIfNotEmpty("Batch-Mode Processing Steps:", cap.stream().filter(AnalysisRegistration::supportsBatchOnly));
        logIfNotEmpty("Mixed-Mode Processing Steps:", cap.stream().filter(AnalysisRegistration::supportsBothModes));
    }

    private static void logIfNotEmpty(String title, Stream<AnalysisRegistration> reg) {
        Iterator<AnalysisRegistration> iter = reg.iterator();
        if (!iter.hasNext())
            return;
        logger.info("");
        logger.info(title);
        iter.forEachRemaining(Main::logCapability);
    }

    private static void logCapability(AnalysisRegistration analysisRegistration) {
        logger.info(" - " + analysisRegistration.getName());
        if (!analysisRegistration.getRequiredParameters().isEmpty())
            logger.info("     Required parameters: " + analysisRegistration.getRequiredParameters());
        if (!analysisRegistration.getOptionalParameters().isEmpty())
            logger.info("     Optional parameters: " + analysisRegistration.getOptionalParameters());
        if (analysisRegistration.hasGenericConstructor())
            logger.info("     Accepts any parameters");
    }

    private static class CmdArgs {
        @Parameter(names = {"-h", "--help"}, help = true, order = 0)
        private boolean printHelp = false;
        @Parameter(names = {"-f", "--file"}, description = "A file containing the script to parse.", order = 1)
        private String fileName = null;
        @Parameter(names = {"-P", "--scan"}, description = "Package names that will be scanned automatically. Wildcards allowed.", order = 2)
        private List<String> packagesToScan = Lists.newArrayList("bitflow4j");
        @Parameter(names = {"-c", "--capabilities"}, description = "Prints the capabilities of this jar in a human readable format.")
        private boolean printCapabilities = false;
        @Parameter(names = {"-j", "--json-capabilities"}, description = "Prints the capabilities of this jar in json format.")
        private boolean printJsonCapabilities = false;
        @Parameter(names = {"-p", "--pipeline"}, description = "Prints the pipeline steps resulting from parsing the input script and exits.")
        private boolean printPipeline = false;
        @Parameter(names = {"-v", "--verbose"}, description = "Set the log level to FINER.")
        private boolean verboseLogging = false;
        @Parameter(names = {"-s", "--silent"}, description = "Set the log level to WARNING.")
        private boolean silentLogging = false;
        @Parameter(description = "[Bitflow Script]")
        private List<String> scriptParts = new ArrayList<>();

        public boolean isScriptValid() {
            return fileName != null || (scriptParts != null && !scriptParts.isEmpty());
        }

        public String getRawScript() throws IOException {
            if (fileName != null) {
                return readRawScript(fileName);
            } else {
                return String.join(" ", scriptParts);
            }
        }

        public String[] cleanPackagesToScan() {
            return packagesToScan.toArray(new String[0]);

//            if (packagesToScan.equals("*")) {
//                return null;
//            }
//            return packagesToScan.split(",");
        }

        public void configureLogging() {
            if (verboseLogging)
                Config.setDefaultLogLevel(Level.FINER);
            else if (silentLogging)
                Config.setDefaultLogLevel(Level.WARNING);
        }

    }

}
