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
    public final AnalysisStep PCA = new PcaAnalysis();
    public final AnalysisStep PCA_PLOT = new PlotPca();

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
        private static final int PCA_COLS = -1; // Can be set to 2 to force at least 2 components
        private static final double PCA_VARIANCE = 0.99;

        PcaAnalysis() {
            super("5-pca.csv");
        }

        @Override
        public String toString() {
            return "PCA analysis";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(new PCAAlgorithm(-1, PCA_COLS, PCA_VARIANCE));
        }

        @Override
        protected AnalysisStep getInputStep() {
            return COMBINE_DATA;
        }
    }

    public class PlotPca extends AnalysisStep {
        PlotPca() {
            super("6-pca-plot.esv");
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
            return PCA;
        }

        @Override
        protected void setInMemoryOutput(AppBuilder builder) {
            builder.setOutput(new OutputMetricPlotter(new ScatterPlotter(), 0, 1));
        }

        @Override
        protected void setFileOutput(AppBuilder builder, File output) throws IOException {
            builder.setOutput(new OutputMetricPlotter(new ScatterPlotter(), output.toString(), 0, 1));
        }
    }

}
