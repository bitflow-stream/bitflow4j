package metrics.main;

import metrics.algorithms.*;
import metrics.io.FileMetricReader;
import metrics.io.OutputMetricPlotter;
import metrics.io.ScatterPlotter;

import java.io.File;
import java.io.IOException;

/**
 * Created by anton on 4/14/16.
 */
public class DimensionReductionApp implements App {

    private static final String COMBINED_FILE = "1-combined.csv";
    private static final String VARIANCE_FILE = "2-variance-filtered.csv";
    private static final String CORR_FILE = "3-correlation.csv";
    private static final String CORR_STATS_FILE = "4-correlation-stats.csv";
    private static final String PCA_FILE = "5-pca.csv";
    private static final String PCA_PLOT_FILE = "6-pca.eps";

    private static final double MIN_VARIANCE = 0.02;
    private static final double SIGNIFICANT_CORRELATION = 0.7;
    private static final int PCA_COLS = -1; // Can be set to 2 to force at least 2 components
    private static final double PCA_VARIANCE = 0.99;

    private final ExperimentBuilder.Host host;
    private final File outputDir;
    private final AppBuilder sourceDataBuilder;

    public DimensionReductionApp(Config config, ExperimentBuilder.Host host, AppBuilder sourceData) throws IOException {
        this.sourceDataBuilder = sourceData;
        this.host = host;
        this.outputDir = makeOutputDir(config.outputFolder);
        System.err.println("Writing results to " + this.outputDir);
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

    private File getOutputFile(String filename) {
        return new File(outputDir, filename);
    }

    public void runAll() throws IOException {
        combineData();
        varianceFilter();
        correlation();
        correlationStatistics();
        pca();
        pcaPlot();
    }

    private AppBuilder newBuilder(File inputFile) throws IOException {
        return new AppBuilder(inputFile, FileMetricReader.FILE_NAME);
    }

    private void message(String msg) {
        System.err.println("===================== " + msg);
    }

    private void combineData() throws IOException {
        AppBuilder builder = sourceDataBuilder;
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

    private void pcaPlot() throws IOException {
        AppBuilder builder = newBuilder(getOutputFile(PCA_FILE));
        builder.addAlgorithm(new NoopAlgorithm());
        File output = getOutputFile(PCA_PLOT_FILE);
        builder.setOutput(new OutputMetricPlotter(0, 1,new ScatterPlotter(),output.toString()));
        message("Writing PCA plot to " + output.toString());
        builder.runAndWait();
    }

}
