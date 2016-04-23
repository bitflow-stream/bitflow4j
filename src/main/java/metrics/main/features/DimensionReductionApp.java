package metrics.main.features;

import com.google.common.io.ByteArrayDataOutput;
import metrics.algorithms.*;
import metrics.io.plot.AbstractPlotter;
import metrics.io.plot.JFreeChart.AbstractJFreePlotter;
import metrics.io.plot.OutputMetricPlotter;
import metrics.io.plot.plotFX.AbstractFxPlotter;
import metrics.io.plot.plotGral.ScatterPlotter;
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

    public final AnalysisStep LABELLED = new LabelData(2, "cooldown", true);

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

    public final AnalysisStep PCA_PLOT = new ScatterPlot(PCA, 0, 1);

    public final AnalysisStep FX_PLOT_PCA = new FxPlotPca(PCA, "fx-scaled", 0, 1);
    public final AnalysisStep JFREE_PLOT_PCA = new FxPlotPca(PCA, "jfree-scaled", 0, 1);

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
        private final boolean removeDefaultLabel;

        LabelData(int warmupMins, String defaultLabel, boolean removeDefaultLabel) {
            super("1.labelled.csv", null);
            this.warmupMins = warmupMins;
            this.defaultLabel = defaultLabel;
            this.removeDefaultLabel = removeDefaultLabel;
        }

        @Override
        public String toString() {
            return "labelling";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new ExperimentLabellingAlgorithm(warmupMins, defaultLabel));
            if (removeDefaultLabel) {
                builder.addAlgorithm(new SampleFilterAlgorithm(
                        (sample) -> !defaultLabel.equals(sample.getLabel())
                ));
            }
        }

        @Override
        protected void hashParameters(ByteArrayDataOutput bytes) {
            bytes.writeInt(warmupMins);
            bytes.writeChars(defaultLabel);
            bytes.writeBoolean(removeDefaultLabel);
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

    public abstract class AbstractPlot extends AnalysisStep {
        final int[] cols;

        public AbstractPlot(String filename, AnalysisStep inputStep, int... cols) {
            super(filename, inputStep);
            this.cols = cols;
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new NoopAlgorithm());
        }

        @Override
        protected void setInMemoryOutput(AppBuilder builder) {
            builder.setOutput(new OutputMetricPlotter<>(createPlotter(), cols));
        }

        @Override
        protected void setFileOutput(AppBuilder builder, File output) throws IOException {
            builder.setOutput(new OutputMetricPlotter<>(createPlotter(), output.toString(), cols));
        }

        @Override
        protected void hashParameters(ByteArrayDataOutput bytes) {
            bytes.writeInt(cols.length);
            for (int col : cols)
                bytes.writeInt(col);
        }

        abstract AbstractPlotter<?> createPlotter();
    }

    public class ScatterPlot extends AbstractPlot {
        public ScatterPlot(AnalysisStep inputStep, int... cols) {
            super("6.plot.png", inputStep, cols);
        }

        public ScatterPlot(AnalysisStep inputStep) {
            this(inputStep, 0, 1);
        }

        @Override
        public String toString() {
            return "scatter plot";
        }

        @Override
        AbstractPlotter<?> createPlotter() {
            return new ScatterPlotter();
        }
    }

    public class FxPlot extends AbstractPlot {
        public FxPlot(AnalysisStep inputStep, int... cols) {
            super("6.fx-plot.png", inputStep, cols);
        }

        public FxPlot(AnalysisStep inputStep) {
            this(inputStep, 0, 1);
        }

        @Override
        public String toString() {
            return "FX scatter plot";
        }

        @Override
        AbstractPlotter<?> createPlotter() {
            return new AbstractFxPlotter();
        }
    }

    public class FxPlotPca extends AnalysisStep {
        private final int[] cols;

        public FxPlotPca(AnalysisStep inputStep, String name, int... cols) {
            super("6.pca-plotFX-" + name + ".png", inputStep);
            this.cols = cols;
        }

        @Override
        public String toString() {
            return "PCA plotFX";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new NoopAlgorithm());
        }

        @Override
        protected void setInMemoryOutput(AppBuilder builder) {
            builder.setOutput(new OutputMetricPlotter<>(new AbstractFxPlotter(), cols));
        }

        @Override
        protected void setFileOutput(AppBuilder builder, File output) throws IOException {
            throw new UnsupportedOperationException("fx file writing not supported");
        }
    }

    public class JFreePlotPca extends AnalysisStep {
        private final int[] cols;

        public JFreePlotPca(AnalysisStep inputStep, String name, int... cols) {
            super("6.pca-plotJFree-" + name + ".png", inputStep);
            this.cols = cols;
        }

        @Override
        public String toString() {
            return "PCA plotJFree";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new NoopAlgorithm());
        }

        @Override
        protected void setInMemoryOutput(AppBuilder builder) {
            builder.setOutput(new OutputMetricPlotter<>(new AbstractJFreePlotter(), cols));
        }

        @Override
        protected void setFileOutput(AppBuilder builder, File output) throws IOException {
            throw new UnsupportedOperationException("jfree file writing not supported");
        }
    }

    public class FeatureAggregator extends AnalysisStep {

        public FeatureAggregator(AnalysisStep inputStep) {
            super("aggregated.csv", inputStep);
        }

        @Override
        public String toString() {
            return "feature aggregator";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new metrics.algorithms.FeatureAggregator());
        }

    }

}
