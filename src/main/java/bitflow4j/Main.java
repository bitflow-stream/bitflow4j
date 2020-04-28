package bitflow4j;

import bitflow4j.registry.ConstructionException;
import bitflow4j.registry.RegisteredStep;
import bitflow4j.registry.Registry;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.IParameterSplitter;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Collections;
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
        LoggingConfig.initializeLogger();
    }

    public static void main(String[] args) {
        try {
            new Main().executeMain(args);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Fatal error", t);
            System.exit(1);
        }
    }

    private static class NoopArgSplitter implements IParameterSplitter {
        public List<String> split(String value) {
            return Collections.singletonList(value);
        }
    }

    @Parameter(names = {"-step"}, description = "Name of the processing step to execute.")
    public String stepName = null;

    @Parameter(names = {"-args"}, variableArity = true, splitter = NoopArgSplitter.class, description = "Arguments to pass to the processing step. Accepts multiple values. Format: -args a=b x=y")
    public List<String> stepArgs = null;

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

    @Parameter(names = {"-fork-tag"}, description = "Execute one instance of the defined processing for every distinct value of the given tag.")
    public String forkTag = null;

    public void executeMain(String[] args) throws IOException, ConstructionException {
        parseArguments(args);
        configureLogging();

        Registry registry = new Registry();
        registry.scanForProcessingSteps(packagesToScan.toArray(new String[0]));
        if (printCapabilities(registry)) {
            return;
        }

        ProcessingStep step = makeStep(registry);
        SampleChannel channel = new SampleChannel(System.in, System.out);
        Runner runner = new Runner();
        runner.run(step, channel);
    }

    private ProcessingStep makeStep(Registry registry) throws ConstructionException, IOException {
        // Instantiate one processing step to check if it exists. Use empty tag value.
        ProcessingStep step = registry.instantiateStep(stepName, stepArgs);
        if (step == null) {
            logger.info("Known processing steps: " +
                    registry.getAllRegisteredSteps().stream().map(RegisteredStep::getStepName).collect(Collectors.toList()));
            logger.severe("Unknown processing step: " + stepName);
            System.exit(1);
        }

        if (forkTag != null && !forkTag.isEmpty()) {
            step = new MainFork(forkTag, () ->
            {
                try {
                    return registry.instantiateStep(stepName, stepArgs);
                } catch (ConstructionException e) {
                    logger.log(Level.SEVERE, "Failed to construct step " + stepName, e);
                    return null;
                }
            });
        }
        return step;
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
        if (shortLogMessages) {
            // Re-initialize the logging system with new properties
            LoggingConfig.initializeLogger(true);
        }
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
