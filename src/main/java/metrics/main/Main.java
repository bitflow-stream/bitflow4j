package metrics.main;

import metrics.algorithms.NoopAlgorithm;
import metrics.io.OutputMetricPlotter;
import metrics.io.ScatterPlotter;

import java.io.IOException;

/**
 * @author fschmidt
 */
public class Main {

    private static final ExperimentBuilder.Host bono = new ExperimentBuilder.Host("bono.ims", "virtual");
    private static final ExperimentBuilder.Host wally131 = new ExperimentBuilder.Host("wally131", "physical");

    public static void main(String[] args) throws IOException {
        Config conf = new Config();

//        ExperimentBuilder.Host host = bono;
        ExperimentBuilder.Host host = wally131;

//        AppBuilder source = new AppBuilder(9999, "BIN");
        AppBuilder source = new ExperimentBuilder(conf, host, false);
//        AppBuilder source = new OldExperimentBuilder(conf, host.name, true, false, false);

        DimensionReductionApp app = new DimensionReductionApp(conf, host, source);
//        App app = new CodeApp(conf, host, source);

//        app.runAll();
        app.plotPca();
    }

    static class CodeApp implements App {
        final ExperimentBuilder.Host host;
        final Config config;
        final AppBuilder builder;

        CodeApp(Config config, ExperimentBuilder.Host host, AppBuilder builder) {
            this.host = host;
            this.config = config;
            this.builder = builder;
        }

        public void runAll() throws IOException {

//        builder.addAlgorithm(new MetricFilterAlgorithm(0, 1, 2, 3));
            builder.addAlgorithm(new NoopAlgorithm());
//        builder.addAlgorithm(new VarianceFilterAlgorithm(0.02, true));
//        builder.addAlgorithm(new CorrelationAlgorithm(false));
//        builder.addAlgorithm(new CorrelationSignificanceAlgorithm(0.7));
//        builder.addAlgorithm(new MetricCounter());
//            builder.addAlgorithm(new PCAAlgorithm(0.99));

            builder.setOutput(new OutputMetricPlotter(0, 1, new ScatterPlotter(), OutputMetricPlotter.IN_FRAME));
//            builder.setConsoleOutput("CSV");
//        builder.setFileOutput(outputFile, "CSV");

            builder.runApp();
        }
    }

}
