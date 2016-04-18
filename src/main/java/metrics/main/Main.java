package metrics.main;

import com.mkobos.pca_transform.PCA;
import metrics.algorithms.PCAAlgorithm;
import metrics.io.plot.OutputMetricPlotter;
import metrics.io.plot.ScatterPlotter;
import metrics.main.features.DimensionReductionApp;

import java.io.IOException;

public class Main {

    static final Config conf = new Config();

    private static final ExperimentData.Host bono = new ExperimentData.Host("bono.ims", "virtual");
    private static final ExperimentData.Host wally131 = new ExperimentData.Host("wally131", "physical");

    private static final ExperimentData oldData = new OldExperimentData(conf, true, false, false);
    private static final ExperimentData newData = new NewExperimentData(conf, false);

    public static void main(String[] args) throws IOException {
        DimensionReductionApp oldDimensionReduction = new DimensionReductionApp(conf, oldData);
        DimensionReductionApp newDimensionReduction = new DimensionReductionApp(conf, newData);

//        oldDimensionReduction.PCA.execute();

        newDimensionReduction.new PlotPca(newDimensionReduction.
                new PcaAnalysis(0.99, false, PCA.TransformationType.WHITENING), 0, -1).executeInMemory(bono);

//        oldDimensionReduction.PCA.executeCached();

//        new CodeApp(conf, newData.makeBuilder(bono)).runAll();
//        new CodeApp(conf, new AppBuilder(9999, "BIN")).runAll();
    }

    static class CodeApp {
        final Config config;
        final AppBuilder builder;

        CodeApp(Config config, AppBuilder builder) {
            this.config = config;
            this.builder = builder;
        }

        public void runAll() throws IOException {

//        builder.addAlgorithm(new MetricFilterAlgorithm(0, 1, 2, 3));
//        builder.addAlgorithm(new NoopAlgorithm());
//        builder.addAlgorithm(new VarianceFilterAlgorithm(0.02, true));
//        builder.addAlgorithm(new CorrelationAlgorithm(false));
//        builder.addAlgorithm(new CorrelationSignificanceAlgorithm(0.7));
//        builder.addAlgorithm(new MetricCounter());
            builder.addAlgorithm(new PCAAlgorithm(0.99));

            builder.setOutput(new OutputMetricPlotter(new ScatterPlotter(), 0, 1));
//        builder.setConsoleOutput("CSV");
//        builder.setFileOutput(outputFile, "CSV");

            builder.runApp();
        }
    }

}
