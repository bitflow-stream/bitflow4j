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

    public final AnalysisStep COMBINE_DATA = new CombineData();
    public final AnalysisStep VARIANCE_FILTER = new VarianceFilter();
    public final AnalysisStep CORRELATION = new CorrelationAnalysis();
    public final AnalysisStep CORRELATION_STATS = new CorrelationStats();
    public final PcaAnalysis PCA = new PcaAnalysis(0.99);
    public final AnalysisStep PCA_PLOT = new PlotPca(PCA, 0, 1);

    public DimensionReductionApp(Config config, ExperimentData data) throws IOException {
        super(config, data);
    }

    @Override
    public String toString() {
        return "DimensionReduction";
    }

    public class CombineData extends AnalysisStep {
        private static final int WARMUP_MINS = 2;
        private static final String DEFAULT_LABEL = "idle";

        CombineData() {
            super("1-combined.csv");
        }

        @Override
        public String toString() {
            return "combine data";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new ExperimentLabellingAlgorithm(WARMUP_MINS, DEFAULT_LABEL));
        }

        @Override
        protected AnalysisStep getInputStep() {
            return null;
        }
    }

    public class VarianceFilter extends AnalysisStep {
        private static final double MIN_VARIANCE = 0.02;

        VarianceFilter() {
            super("2-variance-filtered.csv");
        }

        @Override
        public String toString() {
            return "variance filter";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new VarianceFilterAlgorithm(MIN_VARIANCE, true));
        }

        @Override
        protected AnalysisStep getInputStep() {
            return COMBINE_DATA;
        }
    }

    public class CorrelationAnalysis extends AnalysisStep {
        CorrelationAnalysis() {
            super("3-correlation.csv");
        }

        @Override
        public String toString() {
            return "correlation analysis";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new CorrelationAlgorithm(false));
        }

        @Override
        protected AnalysisStep getInputStep() {
            return VARIANCE_FILTER;
        }
    }

    public class CorrelationStats extends AnalysisStep {
        private static final double SIGNIFICANT_CORRELATION = 0.7;

        CorrelationStats() {
            super("4-correlation-stats.csv");
        }

        @Override
        public String toString() {
            return "correlation statistics";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new CorrelationSignificanceAlgorithm(SIGNIFICANT_CORRELATION));
        }

        @Override
        protected AnalysisStep getInputStep() {
            return CORRELATION;
        }
    }

    public class PcaAnalysis extends AnalysisStep {
        private final int cols;
        private final double variance;
        private final boolean center;
        private final com.mkobos.pca_transform.PCA.TransformationType transformationType;

        public PcaAnalysis(int cols) {
            super("5-pca-" + cols + "cols.csv");
            this.cols = cols;
            this.variance = -1;
            this.center = true;
            this.transformationType = com.mkobos.pca_transform.PCA.TransformationType.WHITENING;
        }

        public PcaAnalysis(double variance) {
            this(variance, true, com.mkobos.pca_transform.PCA.TransformationType.WHITENING);
        }

        public PcaAnalysis(double variance, boolean center, com.mkobos.pca_transform.PCA.TransformationType transformationType) {
            super("5-pca-" + variance + "var.csv");
            this.cols = -1;
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
            builder.addAlgorithm(new PCAAlgorithm(-1, cols, variance));
        }

        @Override
        protected AnalysisStep getInputStep() {
            return COMBINE_DATA;
        }
    }

    public class PlotPca extends AnalysisStep {
        private final PcaAnalysis input;
        private final int[] cols;

        public PlotPca(PcaAnalysis input, int ...cols) {
            super("6-pca-plot.esv");
            this.input = input;
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
        protected AnalysisStep getInputStep() {
            return input;
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
