package bitflow4j.script;

import bitflow4j.Pipeline;
import bitflow4j.misc.Config;
import bitflow4j.misc.TreeFormatter;
import bitflow4j.script.endpoints.EndpointFactory;
import bitflow4j.script.registry.RegisteredPipelineStep;
import bitflow4j.script.registry.Registry;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Main parses a BitflowScript and executes the resulting Pipeline.
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
            System.err.println(e.getMessage());
            e.usage();
            return;
        }
        if (args.length == 0 || cmdArgs.printHelp) {
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

        if (!cmdArgs.hasValidScript()) {
            logger.severe("Please provide a Bitflow script either as file (-f parameter) or ");
            logger.severe(jc.toString());
            return;
        }
        String rawScript = cmdArgs.getRawScript();
        EndpointFactory endpoints = new EndpointFactory();
        BitflowScriptCompiler compiler = new BitflowScriptCompiler(registry, endpoints);
        Pipeline pipe;
        try {
            pipe = compiler.parseScript(rawScript);
        } catch (CompilationException exc) {
            logger.severe("Failed to parse Bitflow script:");
            exc.getErrors().stream().map(s -> "\t" + s).forEach(logger::severe);
            return;
        }

        for (String line : TreeFormatter.standard.formatLines(pipe)) {
            logger.info(line);
        }
        if (cmdArgs.printPipeline)
            return;
        pipe.runAndWait();
    }

    private static void logCapabilities(Registry registry) {
        List<RegisteredPipelineStep> cap = Lists.newArrayList(registry.getCapabilities());
        cap.sort((a1, a2) -> a1.name.compareToIgnoreCase(a2.name));
        logIfNotEmpty("Stream-Mode Processing Steps:", cap.stream().filter(RegisteredPipelineStep::supportsStreamOnly));
        logIfNotEmpty("Batch-Mode Processing Steps:", cap.stream().filter(RegisteredPipelineStep::supportsBatchOnly));
        logIfNotEmpty("Mixed-Mode Processing Steps:", cap.stream().filter(RegisteredPipelineStep::supportsBothModes));
    }

    private static void logIfNotEmpty(String title, Stream<RegisteredPipelineStep> reg) {
        Iterator<RegisteredPipelineStep> iter = reg.iterator();
        if (!iter.hasNext())
            return;
        logger.info("");
        logger.info(title);
        iter.forEachRemaining(Main::logCapability);
    }

    private static void logCapability(RegisteredPipelineStep registeredPipelineStep) {
        logger.info(" - " + registeredPipelineStep.name);
        logger.info("     Bitflow-Script: " + registeredPipelineStep.conventionName);
        logger.info("     Description: " + registeredPipelineStep.description);
        if (!registeredPipelineStep.requiredParameters.isEmpty())
            logger.info("     Required parameters: " + registeredPipelineStep.requiredParameters);
        if (!registeredPipelineStep.optionalParameters.isEmpty())
            logger.info("     Optional parameters: " + registeredPipelineStep.optionalParameters);
        if (registeredPipelineStep.hasGenericConstructor())
            logger.info("     Accepts any parameters");
    }

    private static class CmdArgs {
        @Parameter(names = {"-h", "--help"}, help = true, order = 0)
        public boolean printHelp = false;
        @Parameter(names = {"-f", "--file"}, description = "A file containing the script to parse (cannot be used with -s).", order = 1)
        public String fileName = null;
        @Parameter(names = {"-s", "--script"}, description = "The Bitflow script to execute (cannot be used with -f).")
        public String script = null;
        @Parameter(names = {"-P", "--scan"}, description = "Package names that will be scanned automatically. Wildcards allowed.", order = 2)
        public List<String> packagesToScan = Lists.newArrayList("bitflow4j");
        @Parameter(names = {"-c", "--capabilities"}, description = "Prints the capabilities of this jar in a human readable format.")
        public boolean printCapabilities = false;
        @Parameter(names = {"-j", "--json-capabilities"}, description = "Prints the capabilities of this jar in json format.")
        public boolean printJsonCapabilities = false;
        @Parameter(names = {"-p", "--pipeline"}, description = "Prints the pipeline steps resulting from parsing the input script and exits.")
        public boolean printPipeline = false;
        @Parameter(names = {"-v", "--verbose"}, description = "Set the log level to FINER.")
        public boolean verboseLogging = false;
        @Parameter(names = {"-q", "--quiet"}, description = "Set the log level to WARNING.")
        public boolean silentLogging = false;

        public boolean hasValidScript() {
            boolean hasFile = fileName != null;
            boolean hasScript = script != null;
            return (hasFile || hasScript) && !(hasFile && hasScript);
        }

        public String getRawScript() throws IOException {
            if (fileName != null) {
                return readRawScript(fileName);
            }
            return script;
        }

        private String readRawScript(String fileName) throws IOException {
            return new String(Files.readAllBytes(Paths.get(fileName)));
        }

        public String[] cleanPackagesToScan() {
            return packagesToScan.toArray(new String[0]);
        }

        public void configureLogging() {
            if (verboseLogging)
                Config.setDefaultLogLevel(Level.FINER);
            else if (silentLogging)
                Config.setDefaultLogLevel(Level.WARNING);
        }

    }

}
