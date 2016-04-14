package metrics.main;

import metrics.algorithms.*;
import metrics.io.FileMetricReader;
import metrics.io.OutputMetricPlotter;
import metrics.io.ScatterPlotter;

import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

/**
 * Created by anton on 4/14/16.
 */
public class DimensionReductionApp implements App {

    private static final String COMBINED_FILE = "1-combined.csv";
    private static final String VARIANCE_FILE = "2-variance-filtered.csv";
    private static final String CORR_FILE = "3-correlation.csv";
    private static final String CORR_STATS_FILE = "4-correlation-stats.csv";
    private static final String PCA_FILE = "5-pca.csv";

    private static final double MIN_VARIANCE = 0.02;
    private static final double SIGNIFICANT_CORRELATION = 0.7;
    private static final int PCA_COLS = -1; // Can be set to 2 to force at least 2 components
    private static final double PCA_VARIANCE = 0.99;

    private final ExperimentBuilder.Host host;
    private final Config config;
    private File outputDir;

    public DimensionReductionApp(Config config, ExperimentBuilder.Host host) throws IOException {
        this.host = host;
        this.config = config;
    }

    private String outputDirname(int suffix) {
        String result = "DimensionReduction-" + host.name;
        if (suffix > 0) result += "-" + suffix;
        return result;
    }

    private File makeOutputDir(String root) throws IOException {
        File output;
        int suffix = 0;
        do {
            output = new File(root + "/" + outputDirname(suffix));
            suffix++;
            if (suffix >= 1000) {
                throw new IOException("Failed to create output directory");
            }
        } while (!output.mkdirs());
        return output;
    }

    private File getInputDirectory() {
        String prefix = outputDirname(0);
        TreeSet<File> candidates = new TreeSet<>();
        for (File child : new File(config.outputFolder).listFiles()) {
            if (child.isDirectory() && child.getName().startsWith(prefix)) {
                candidates.add(child);
            }
        }
        File result = candidates.pollLast();
        if (result == null) {
            throw new IllegalStateException("Could not find foldre like '" + prefix + "*' inside " + config.outputFolder);
        }
        return result;
    }

    private File getInputFile(String filename) {
        File dir = getInputDirectory();
        return new File(dir, filename);
    }

    private File getOutputFile(String filename) {
        return new File(outputDir, filename);
    }

    public void runAll(AppBuilder sourceDataBuilder) throws IOException {
        outputDir = makeOutputDir(config.outputFolder);
        System.err.println("Writing results to " + outputDir);
        combineData(sourceDataBuilder);
        varianceFilter();
        correlation();
        correlationStatistics();
        pca();
    }

    private AppBuilder newBuilder(File inputFile) throws IOException {
        return new AppBuilder(inputFile, FileMetricReader.FILE_NAME);
    }

    private void message(String msg) {
        System.err.println("===================== " + msg);
    }

    private void combineData(AppBuilder builder) throws IOException {
        builder.addAlgorithm(new NoopAlgorithm());
        File output = getOutputFile(COMBINED_FILE);
        builder.setFileOutput(output, "CSV");
        message("Writing combined host metrics to " + output.toString());
        builder.runAndWait();
    }

    private void varianceFilter() throws IOException {
        AppBuilder builder = newBuilder(getOutputFile(COMBINED_FILE));
        builder.addAlgorithm(new VarianceFilterAlgorithm(MIN_VARIANCE, true));
        File output = getOutputFile(VARIANCE_FILE);
        builder.setFileOutput(output, "CSV");
        message("Writing variance-filtered data to " + output.toString());
        builder.runAndWait();
    }

    private void correlation() throws IOException {
        AppBuilder builder = newBuilder(getOutputFile(VARIANCE_FILE));
        builder.addAlgorithm(new CorrelationAlgorithm(false));
        File output = getOutputFile(CORR_FILE);
        builder.setFileOutput(output, "CSV");
        message("Writing correlation data to " + output.toString());
        builder.runAndWait();
    }

    private void correlationStatistics() throws IOException {
        AppBuilder builder = newBuilder(getOutputFile(CORR_FILE));
        builder.addAlgorithm(new CorrelationSignificanceAlgorithm(SIGNIFICANT_CORRELATION));
        File output = getOutputFile(CORR_STATS_FILE);
        builder.setFileOutput(output, "CSV");
        message("Writing correlation data to " + output.toString());
        builder.runAndWait();
    }

    private void pca() throws IOException {
        AppBuilder builder = newBuilder(getOutputFile(COMBINED_FILE));
        builder.addAlgorithm(new PCAAlgorithm(-1, PCA_COLS, PCA_VARIANCE));
        File output = getOutputFile(PCA_FILE);
        builder.setFileOutput(output, "CSV");
        message("Writing PCA data to " + output.toString());
        builder.runAndWait();
    }

    void plotPca() throws IOException {
        AppBuilder builder = newBuilder(getInputFile(PCA_FILE));
        builder.addAlgorithm(new NoopAlgorithm());
        builder.setOutput(new OutputMetricPlotter(0, 1, new ScatterPlotter()));
        builder.runAndWait();
    }

}
