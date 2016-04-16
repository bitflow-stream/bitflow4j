package metrics.main;

import metrics.algorithms.*;
import metrics.io.FileMetricReader;
import metrics.io.plot.OutputMetricPlotter;
import metrics.io.plot.ScatterPlotter;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by anton on 4/14/16.
 */
public class DimensionReductionApp {

    private static final String COMBINED_FILE = "1-combined.csv";
    private static final String VARIANCE_FILE = "2-variance-filtered.csv";
    private static final String CORR_FILE = "3-correlation.csv";
    private static final String CORR_STATS_FILE = "4-correlation-stats.csv";
    private static final String PCA_FILE = "5-pca.csv";

    private static final double MIN_VARIANCE = 0.02;
    private static final double SIGNIFICANT_CORRELATION = 0.7;
    private static final int PCA_COLS = -1; // Can be set to 2 to force at least 2 components
    private static final double PCA_VARIANCE = 0.99;
    private static final int WARMUP_MINS = 2;
    private static final String DEFAULT_LABEL = "idle";

    private final Config config;
    private final ExperimentBuilderFactory builderFactory;

    public interface ExperimentBuilderFactory {
        // TODO needs refactoring
        AbstractExperimentBuilder makeExperimentBuilder(AbstractExperimentBuilder.Host host) throws IOException;

        List<AbstractExperimentBuilder.Host> getAllHosts();
    }

    public DimensionReductionApp(Config config, ExperimentBuilderFactory builderFactory) throws IOException {
        this.config = config;
        this.builderFactory = builderFactory;
    }

    public String getName() {
        return "DimensionReduction";
    }

    private File makeOutputDir(AbstractExperimentBuilder builder, AbstractExperimentBuilder.Host host) throws IOException {
        String filename = config.outputFolder + "/" + getName();
        filename += "-" + builder.getName();
        filename += "/" + host.name;
        File result = new File(filename);
        if (result.exists() && !result.isDirectory())
            throw new IOException("Not a directory: " + filename);
        if (!result.exists() && !result.mkdirs())
            throw new IOException("Failed to create output directory " + filename);
        return result;
    }

    private File getOutputFile(File outputDir, String filename) {
        return new File(outputDir, filename);
    }

    public void runAll() throws IOException {
        List<AbstractExperimentBuilder.Host> hosts = builderFactory.getAllHosts();
        message("Running " + getName() + " for " + hosts.size() + " hosts");
        for (AbstractExperimentBuilder.Host host : hosts) {
            AbstractExperimentBuilder builder = builderFactory.makeExperimentBuilder(host);
            doRunForHost(builder, host);
        }
    }

    public void runForHost(AbstractExperimentBuilder.Host host) throws IOException {
        AbstractExperimentBuilder builder = builderFactory.makeExperimentBuilder(host);
        doRunForHost(builder, host);
    }

    private void doRunForHost(AbstractExperimentBuilder builder, AbstractExperimentBuilder.Host host) throws IOException {
        File outputDir = makeOutputDir(builder, host);
        message("Analysing " + host + " into " + outputDir);
        combineData(builder, outputDir);
        varianceFilter(outputDir);
        correlation(outputDir);
        correlationStatistics(outputDir);
        pca(outputDir);
    }

    private AppBuilder newBuilder(File inputFile) throws IOException {
        return new AppBuilder(inputFile, FileMetricReader.FILE_NAME);
    }

    private void message(String msg) {
        System.err.println("===================== " + msg);
    }

    private void combineData(AppBuilder builder, File outputDir) throws IOException {
        builder.addAlgorithm(new ExperimentLabellingAlgorithm(WARMUP_MINS, DEFAULT_LABEL));
        File output = getOutputFile(outputDir, COMBINED_FILE);
        builder.setFileOutput(output, "CSV");
        message("Writing combined host metrics to " + output.toString());
        builder.runAndWait();
    }

    private void varianceFilter(File outputDir) throws IOException {
        AppBuilder builder = newBuilder(getOutputFile(outputDir, COMBINED_FILE));
        builder.addAlgorithm(new VarianceFilterAlgorithm(MIN_VARIANCE, true));
        File output = getOutputFile(outputDir, VARIANCE_FILE);
        builder.setFileOutput(output, "CSV");
        message("Writing variance-filtered data to " + output.toString());
        builder.runAndWait();
    }

    private void correlation(File outputDir) throws IOException {
        AppBuilder builder = newBuilder(getOutputFile(outputDir, VARIANCE_FILE));
        builder.addAlgorithm(new CorrelationAlgorithm(false));
        File output = getOutputFile(outputDir, CORR_FILE);
        builder.setFileOutput(output, "CSV");
        message("Writing correlation data to " + output.toString());
        builder.runAndWait();
    }

    private void correlationStatistics(File outputDir) throws IOException {
        AppBuilder builder = newBuilder(getOutputFile(outputDir, CORR_FILE));
        builder.addAlgorithm(new CorrelationSignificanceAlgorithm(SIGNIFICANT_CORRELATION));
        File output = getOutputFile(outputDir, CORR_STATS_FILE);
        builder.setFileOutput(output, "CSV");
        message("Writing correlation data to " + output.toString());
        builder.runAndWait();
    }

    private void pca(File outputDir) throws IOException {
        AppBuilder builder = newBuilder(getOutputFile(outputDir, COMBINED_FILE));
        builder.addAlgorithm(new PCAAlgorithm(-1, PCA_COLS, PCA_VARIANCE));
        File output = getOutputFile(outputDir, PCA_FILE);
        builder.setFileOutput(output, "CSV");
        message("Writing PCA data to " + output.toString());
        builder.runAndWait();
    }

    void plotPca(AbstractExperimentBuilder.Host host) throws IOException {
        File outputDir = makeOutputDir(builderFactory.makeExperimentBuilder(host), host);
        AppBuilder builder = newBuilder(getOutputFile(outputDir, PCA_FILE));
        builder.addAlgorithm(new NoopAlgorithm());
        builder.setOutput(new OutputMetricPlotter(new ScatterPlotter(), 0, 1));
        builder.runAndWait();
    }

}
