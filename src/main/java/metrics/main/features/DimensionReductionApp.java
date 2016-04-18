package metrics.main.features;

import com.google.common.io.ByteArrayDataOutput;
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

    public final AnalysisStep FILTERED = new VarianceFilter(0.02, LABELLED);
    public final AnalysisStep FILTERED_SCALED = new ScaleData(FILTERED);
    public final AnalysisStep SCALED = new ScaleData(LABELLED);
    public final AnalysisStep SCALED_FILTERED = new VarianceFilter(0.02, SCALED);
    public final AnalysisStep STANDARDIZED = new StandardizeData(LABELLED);

    public final AnalysisStep CORRELATION = new CorrelationAnalysis(FILTERED);
    public final AnalysisStep CORRELATION_STATS = new CorrelationStats(0.7, CORRELATION);

    public final PcaAnalysis PCA = new PcaAnalysis(STANDARDIZED, -1, 0.99, true, TRANSFORMATION_TYPE);
    public final PcaAnalysis PCA_SCALED = new PcaAnalysis(SCALED, -1, 0.99, true, TRANSFORMATION_TYPE);
    public final PcaAnalysis PCA_RAW = new PcaAnalysis(LABELLED, -1, 0.99, true, TRANSFORMATION_TYPE);
    public final PcaAnalysis PCA_SCALED_FILTERED = new PcaAnalysis(SCALED_FILTERED, -1, 0.99, true, TRANSFORMATION_TYPE);
    public final PcaAnalysis PCA_FILTERED_SCALED = new PcaAnalysis(FILTERED_SCALED, -1, 0.99, true, TRANSFORMATION_TYPE);

    public final AnalysisStep PCA_PLOT = new ScatterPlot(PCA);

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
            super("1.labelled.csv", null);
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

        @Override
        protected void hashParameters(ByteArrayDataOutput bytes) {
            bytes.writeInt(warmupMins);
            bytes.writeChars(defaultLabel);
        }
    }

    public class VarianceFilter extends AnalysisStep {
        private final double minVariance;

        VarianceFilter(double minVariance, AnalysisStep inputStep) {
            super("2.filtered.csv", inputStep);
            this.minVariance = minVariance;
        }

        @Override
        public String toString() {
            return "variance-filter";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new VarianceFilterAlgorithm(minVariance, true));
        }

        @Override
        protected void hashParameters(ByteArrayDataOutput bytes) {
            bytes.writeDouble(minVariance);
        }
    }

    public class StandardizeData extends AnalysisStep {
        StandardizeData(AnalysisStep inputStep) {
            super("2.standardized.csv", inputStep);
        }

        @Override
        public String toString() {
            return "feature standardization";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new FeatureStandardizer());
        }
    }

    public class ScaleData extends AnalysisStep {
        ScaleData(AnalysisStep inputStep) {
            super("2.scaled.csv", inputStep);
        }

        @Override
        public String toString() {
            return "feature min-max scaling";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new FeatureMinMaxScaler());
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

        @Override
        protected void hashParameters(ByteArrayDataOutput bytes) {
            bytes.writeDouble(significantCorrelation);
        }
    }

    public class PcaAnalysis extends AnalysisStep {
        private final int cols;
        private final double variance;
        private final boolean center;
        private final com.mkobos.pca_transform.PCA.TransformationType transformationType;

        public PcaAnalysis(AnalysisStep inputStep, int cols, double variance,
                           boolean center, com.mkobos.pca_transform.PCA.TransformationType transformationType) {
            super("5.pca.csv", inputStep);
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

        @Override
        protected void hashParameters(ByteArrayDataOutput bytes) {
            bytes.writeInt(cols);
            bytes.writeDouble(variance);
            bytes.writeBoolean(center);
            bytes.writeChars(transformationType.toString());
        }
    }

    public class ScatterPlot extends AnalysisStep {
        private final int[] cols;

        public ScatterPlot(AnalysisStep inputStep, int... cols) {
            super("6.pca-plot.png", inputStep);
            this.cols = cols;
        }

        public ScatterPlot(AnalysisStep inputStep) {
            this(inputStep, 0, 1);
        }

        @Override
        public String toString() {
            return "scatter plot";
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

        @Override
        protected void hashParameters(ByteArrayDataOutput bytes) {
            bytes.writeInt(cols.length);
            for (int col : cols)
                bytes.writeInt(col);
        }
    }

}
