package metrics.main.features;

import metrics.algorithms.*;
import metrics.io.plot.OutputMetricPlotter;
import metrics.io.plot.ScatterPlotter;
import metrics.main.AppBuilder;
import metrics.main.Config;
import metrics.main.DataAnalyser;
import metrics.main.ExperimentData;

import java.io.File;
import java.io.IOException;

/**
 * Created by anton on 4/14/16.
 */
public class DimensionReductionApp extends DataAnalyser {

    public final AnalysisStep LABELLED = new LabelData(2, "idle");

    public final AnalysisStep FILTERED = new VarianceFilter(0.02, LABELLED, "filtered");
    public final AnalysisStep FILTERED_SCALED = new ScaleData(FILTERED, "filteredScaled");
    public final AnalysisStep SCALED = new ScaleData(LABELLED, "scaled");
    public final AnalysisStep SCALED_FILTERED = new VarianceFilter(0.02, SCALED, "scaledFiltered");

    public final AnalysisStep CORRELATION = new CorrelationAnalysis(FILTERED);
    public final AnalysisStep CORRELATION_STATS = new CorrelationStats(0.7, CORRELATION);

    public final PcaAnalysis PCA = new PcaAnalysis(SCALED, "scaled", -1, 0.99, true, TRANSFORMATION_TYPE);
    public final PcaAnalysis PCA_UNSCALED = new PcaAnalysis(LABELLED, "unscaled", -1, 0.99, true, TRANSFORMATION_TYPE);
    public final PcaAnalysis PCA_SCALED_FILTERED = new PcaAnalysis(SCALED_FILTERED, "scaledFiltered", -1, 0.99, true, TRANSFORMATION_TYPE);
    public final PcaAnalysis PCA_FILTERED_SCALED = new PcaAnalysis(FILTERED_SCALED, "filteredScaled", -1, 0.99, true, TRANSFORMATION_TYPE);

    public final AnalysisStep PCA_PLOT = new PlotPca(PCA, "scaled", 0, 1);
    public final AnalysisStep PCA_PLOT_UNSCALED = new PlotPca(PCA_UNSCALED, "unscaled", 0, 1);
    public final AnalysisStep PCA_PLOT_SCALED_FILTERED = new PlotPca(PCA_SCALED_FILTERED, "scaledFiltered", 0, 1);
    public final AnalysisStep PCA_PLOT_FILTERED_SCALED = new PlotPca(PCA_FILTERED_SCALED, "filteredScaled", 0, 1);

    private static final com.mkobos.pca_transform.PCA.TransformationType TRANSFORMATION_TYPE =
            com.mkobos.pca_transform.PCA.TransformationType.ROTATION;

    public DimensionReductionApp(Config config, ExperimentData data) throws IOException {
        super(config, data);
    }

    @Override
    public String toString() {
        return "DimensionReduction";
    }

    public class LabelData extends AnalysisStep {
        private final int warmupMins;
        private final String defaultLabel;

        LabelData(int warmupMins, String defaultLabel) {
            super("1.labelled_" + warmupMins + "_" + defaultLabel + ".csv", null);
            this.warmupMins = warmupMins;
            this.defaultLabel = defaultLabel;
        }

        @Override
        public String toString() {
            return "labelling";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new ExperimentLabellingAlgorithm(warmupMins, defaultLabel));
        }
    }

    public class VarianceFilter extends AnalysisStep {
        private final double minVariance;

        VarianceFilter(double minVariance, AnalysisStep inputStep, String name) {
            super("2." + name + "_" + minVariance + ".csv", inputStep);
            this.minVariance = minVariance;
        }

        @Override
        public String toString() {
            return "variance filter";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new VarianceFilterAlgorithm(minVariance, true));
        }
    }

    public class ScaleData extends AnalysisStep {
        ScaleData(AnalysisStep inputStep, String name) {
            super("2." + name + ".csv", inputStep);
        }

        @Override
        public String toString() {
            return "feature scaling";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new FeatureScalingAlgorithm());
        }
    }

    public class CorrelationAnalysis extends AnalysisStep {
        CorrelationAnalysis(AnalysisStep inputStep) {
            super("3.correlation.csv", inputStep);
        }

        @Override
        public String toString() {
            return "correlation analysis";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new CorrelationAlgorithm(false));
        }
    }

    public class CorrelationStats extends AnalysisStep {
        private final double significantCorrelation;

        CorrelationStats(double significantCorrelation, AnalysisStep inputStep) {
            super("4.correlation-stats.csv", inputStep);
            this.significantCorrelation = significantCorrelation;
        }

        @Override
        public String toString() {
            return "correlation statistics";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new CorrelationSignificanceAlgorithm(significantCorrelation));
        }
    }

    public class PcaAnalysis extends AnalysisStep {
        private final int cols;
        private final double variance;
        private final boolean center;
        private final com.mkobos.pca_transform.PCA.TransformationType transformationType;

        public PcaAnalysis(AnalysisStep inputStep, String name,
                           int cols, double variance,
                           boolean center, com.mkobos.pca_transform.PCA.TransformationType transformationType) {
            super("5.pca_" + name + "_" + cols + "_" + variance + "_" + center + "_" + transformationType + ".csv", inputStep);
            this.cols = cols;
            this.variance = variance;
            this.center = center;
            this.transformationType = transformationType;
        }

        @Override
        public String toString() {
            return "PCA analysis";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new PCAAlgorithm(-1, cols, variance, center, transformationType));
        }
    }

    public class PlotPca extends AnalysisStep {
        private final int[] cols;

        public PlotPca(AnalysisStep inputStep, String name, int... cols) {
            super("6.pca-plot-" + name + ".png", inputStep);
            this.cols = cols;
        }

        @Override
        public String toString() {
            return "PCA plot";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new NoopAlgorithm());
        }

        @Override
        protected void setInMemoryOutput(AppBuilder builder) {
            builder.setOutput(new OutputMetricPlotter(new ScatterPlotter(), cols));
        }

        @Override
        protected void setFileOutput(AppBuilder builder, File output) throws IOException {
            builder.setOutput(new OutputMetricPlotter(new ScatterPlotter(), output.toString(), cols));
        }
    }

}
