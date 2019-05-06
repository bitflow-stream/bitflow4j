package bitflow4j.script;

import bitflow4j.Pipeline;
import bitflow4j.misc.Config;
import bitflow4j.misc.TreeFormatter;
import bitflow4j.script.endpoints.EndpointFactory;
import bitflow4j.script.registry.RegisteredStep;
import bitflow4j.script.registry.Registry;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main parses a BitflowScript and executes the resulting Pipeline.
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    static {
        try {
            Config.initializeLogger();
        } catch (IOException e) {
            System.err.println("Failed to initialize logger.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            int code = new Main().executeMainAndStop(args);
            if (code != 0) {
                System.exit(code);
            }
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Fatal error", t);
            System.exit(1);
        }
    }

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

    @Parameter(names = {"--shutdown-after"}, description = "After all tasks have finished, wait this number of milliseconds before forcefully shutting down the JVM. Set to <= 0 to disable.")
    public long shutdownTimeout = 3000L;

    private int executeMainAndStop(String[] args) throws IOException {
        int result = executeMain(args);
        Main.shutdownAfter(shutdownTimeout);
        return result;
    }

    /**
     * Start a daemon thread that calls System.exit() after the given number of milliseconds.
     * Used to ensure forceful application shutdown after all tasks have finished, even if some Thread is still running.
     */
    private static void shutdownAfter(long shutdownTimeout) {
        if (shutdownTimeout > 0) {
            Thread t = new Thread("shutdown timeout daemon thread") {
                @Override
                public void run() {
                    try {
                        Thread.sleep(shutdownTimeout);
                    } catch (InterruptedException ignore) {
                    }
                    logger.severe(String.format("Calling System.exit(): All tasks are finished, " +
                            "but threads failed to stop after %s milliseconds.", shutdownTimeout));
                    System.exit(1);
                }
            };
            t.setDaemon(true);
            t.start();
        }
    }

    public int executeMain(String[] args) throws IOException {
        JCommander jc = JCommander.newBuilder()
                .allowAbbreviatedOptions(true)
                .programName(Main.class.getCanonicalName())
                .addObject(this).build();
        try {
            jc.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            e.usage();
            return 1;
        }
        if (args.length == 0 || printHelp) {
            jc.usage();
            return 0;
        }
        configureLogging();

        Registry registry = new Registry();
        registry.scanForProcessingSteps(cleanPackagesToScan());

        if (printJsonCapabilities) {
            logJsonCapabilities(registry);
            return 0;
        } else if (printCapabilities) {
            logCapabilities(registry);
            return 0;
        }

        if (!hasValidScript()) {
            logger.severe("Please provide a Bitflow script either as file (-f parameter) or ");
            logger.severe(jc.toString());
            return 1;
        }
        String rawScript = getRawScript();
        EndpointFactory endpoints = new EndpointFactory();
        BitflowScriptCompiler compiler = new BitflowScriptCompiler(registry, endpoints);
        Pipeline pipe = compiler.parseScript(rawScript);

        for (String line : TreeFormatter.standard.formatLines(pipe)) {
            logger.info(line);
        }
        if (!printPipeline)
            pipe.runAndWait();
        return 0;
    }

    private static final Comparator<RegisteredStep> stepComparator = (a1, a2) -> a1.getStepName().compareToIgnoreCase(a2.getStepName());

    private static void logCapabilities(Registry registry) {
        logIfNotEmpty("Stream Processing Steps:", registry.getStreamCapabilities());
        logIfNotEmpty("Batch Processing Steps:", registry.getBatchCapabilities());
        logIfNotEmpty("Fork Steps:", registry.getForkCapabilities());
    }

    private static void logIfNotEmpty(String title, Collection<? extends RegisteredStep> reg) {
        if (reg.isEmpty())
            return;
        System.out.println(); // Fore new line
        System.out.println(title);
        reg.stream().sorted(stepComparator).forEach(Main::logCapability);
    }

    private static void logCapability(RegisteredStep registeredPipelineStep) {
        System.out.println(" - " + registeredPipelineStep.getStepName());
        System.out.println("     Description: " + registeredPipelineStep.getDescription());
        if (!registeredPipelineStep.getRequiredParameters().isEmpty())
            System.out.println("     Required parameters: " + registeredPipelineStep.getRequiredParameters());
        if (!registeredPipelineStep.getOptionalParameters().isEmpty())
            System.out.println("     Optional parameters: " + registeredPipelineStep.getOptionalParameters());
        if (registeredPipelineStep.hasGenericConstructor())
            System.out.println("     Accepts any parameters");
    }

    private static void logJsonCapabilities(Registry registry) {
        Map<String, Object> root = new HashMap<>();
        addJsonCapabilities(root, "stream_processing_steps", registry.getStreamCapabilities());
        addJsonCapabilities(root, "batch_processing_steps", registry.getBatchCapabilities());
        addJsonCapabilities(root, "fork_steps", registry.getForkCapabilities());
        System.out.println(new Gson().toJson(root));
    }

    private static void addJsonCapabilities(Map<String, Object> root, String name, Collection<? extends RegisteredStep> reg) {
        if (reg.isEmpty())
            return;
        Map<String, Object> capabilities = new HashMap<>();
        for (RegisteredStep<?> step : reg) {
            capabilities.put(step.getStepName(), new JsonCapability(step));
        }
        root.put(name, capabilities);
    }

    private static final class JsonCapability {
        public final String name;
        public final String description;
        public final List<String> required_parameters;
        public final List<String> optional_parameters;
        public final boolean accepts_generic_parameters;

        private JsonCapability(RegisteredStep<?> step) {
            this.name = step.getStepName();
            this.description = step.getDescription();
            this.required_parameters = step.getRequiredParameters();
            this.optional_parameters = step.getOptionalParameters();
            this.accepts_generic_parameters = step.hasGenericConstructor();
        }
    }

    private boolean hasValidScript() {
        boolean hasFile = fileName != null;
        boolean hasScript = script != null;
        return (hasFile || hasScript) && !(hasFile && hasScript);
    }

    private String getRawScript() throws IOException {
        if (fileName != null) {
            return readRawScript(fileName);
        }
        return script;
    }

    private String readRawScript(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    private String[] cleanPackagesToScan() {
        return packagesToScan.toArray(new String[0]);
    }

    private void configureLogging() {
        if (verboseLogging)
            Config.setDefaultLogLevel(Level.FINER);
        else if (silentLogging)
            Config.setDefaultLogLevel(Level.WARNING);
    }

}
