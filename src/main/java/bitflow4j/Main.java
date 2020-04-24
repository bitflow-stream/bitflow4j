package bitflow4j;

import bitflow4j.registry.ConstructionException;
import bitflow4j.registry.RegisteredStep;
import bitflow4j.registry.Registry;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Main instantiates the requested ProcessingStep, parses the parameters, and starts reading samples from stdin.
 */
public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    static {
        try {
            LoggingConfig.initializeLogger();
        } catch (IOException e) {
            System.err.println("Failed to initialize logger.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            new Main().executeMain(args);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Fatal error", t);
            System.exit(1);
        }
    }

    @Parameter(names = {"-step"}, description = "Name of the processing step to execute.")
    public String stepName = null;

    @Parameter(names = {"-args"}, description = "Arguments to pass to the processing step. Format: 'a=b, x=y'")
    public String stepArgs = "";

    @Parameter(names = {"-h", "--help"}, help = true, order = 0)
    public boolean printHelp = false;

    @Parameter(names = {"-P", "--scan"}, description = "Package names that will be scanned automatically. Wildcards allowed.", order = 2)
    public List<String> packagesToScan = Lists.newArrayList("bitflow4j");

    @Parameter(names = {"-c", "--capabilities"}, description = "Prints the capabilities of this jar in a human readable format.")
    public boolean printCapabilities = false;

    @Parameter(names = {"-j", "--json-capabilities"}, description = "Prints the capabilities of this jar in json format.")
    public boolean printJsonCapabilities = false;

    @Parameter(names = {"-v", "--verbose"}, description = "Set the log level to FINER.")
    public boolean verboseLogging = false;

    @Parameter(names = {"-q", "--quiet"}, description = "Set the log level to WARNING.")
    public boolean silentLogging = false;

    @Parameter(names = {"-shortlog"}, description = "Do not print timestamps in logging output.")
    public boolean shortLogMessages = false;

    public void executeMain(String[] args) throws IOException, ConstructionException {
        parseArguments(args);
        configureLogging();

        Registry registry = new Registry();
        registry.scanForProcessingSteps(packagesToScan.toArray(new String[0]));
        if (printCapabilities(registry)) {
            return;
        }

        ProcessingStep step = registry.instantiateStep(stepName, stepArgs);
        if (step == null) {
            logger.info("Known processing steps: " +
                    registry.getAllRegisteredSteps().stream().map(RegisteredStep::getStepName).collect(Collectors.toList()));
            throw new IllegalArgumentException("Unknown processing step: " + stepName);
        }
        SampleChannel channel = new SampleChannel(System.in, System.out);
        Runner runner = new Runner();
        runner.run(step, channel);
    }

    private void parseArguments(String[] args) {
        JCommander jc = JCommander.newBuilder()
                .allowAbbreviatedOptions(true)
                .programName(Main.class.getCanonicalName())
                .addObject(this).build();
        try {
            jc.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
            e.usage();
            System.exit(1);
        }
        if (stepName == null || printHelp) {
            jc.usage();
            System.exit(0);
        }
    }

    private void configureLogging() {
        // shortLogMessages
        if (verboseLogging)
            LoggingConfig.setDefaultLogLevel(Level.FINER);
        else if (silentLogging)
            LoggingConfig.setDefaultLogLevel(Level.WARNING);
    }

    private boolean printCapabilities(Registry registry) {
        if (printJsonCapabilities) {
            registry.outputJsonCapabilities();
            return true;
        } else if (printCapabilities) {
            registry.outputCapabilities();
            return true;
        }
        return false;
    }

}
