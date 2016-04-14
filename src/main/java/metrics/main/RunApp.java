package metrics.main;

import metrics.algorithms.PCAAlgorithm;

import java.io.IOException;

/**
 * @author fschmidt
 */
public class RunApp {

    private static final ExperimentBuilder.Host bono = new ExperimentBuilder.Host("bono.ims", "virtual");
    private static final ExperimentBuilder.Host wally131 = new ExperimentBuilder.Host("wally131", "physical");

    public static void main(String[] args) throws IOException {
        Config conf = new Config();

        ExperimentBuilder.Host host = bono;
//        ExperimentBuilder.Host host = wally131;

//        AppBuilder source = new ExperimentBuilder(Config.config, host, false);
        AppBuilder source = new OldExperimentBuilder(conf, host.name, true, false, false);
        App app = new DimensionReductionApp(conf, host, source);

//        App app = new CodeApp(Config.config, host);

        app.runAll();
    }

    static class CodeApp implements App {
        final ExperimentBuilder.Host host;
        final Config config;

        CodeApp(Config config, ExperimentBuilder.Host host) {
            this.host = host;
            this.config = config;
        }

        public void runAll() throws IOException {

//        AppBuilder builder = new AppBuilder(9999, "BIN");
            AppBuilder builder = new OldExperimentBuilder(config, host.name, true, false, false);
//        AppBuilder builder = new ExperimentBuilder(Config.experimentFolder, host, true);
//        builder.setUnifiedSource(HOST);

//        builder.addAlgorithm(new MetricFilterAlgorithm(0, 1, 2, 3));
//        builder.addAlgorithm(new NoopAlgorithm());
//        builder.addAlgorithm(new VarianceFilterAlgorithm(0.02, true));
//        builder.addAlgorithm(new CorrelationAlgorithm(false));
//        builder.addAlgorithm(new CorrelationSignificanceAlgorithm(0.7));
//        builder.addAlgorithm(new MetricCounter());
            builder.addAlgorithm(new PCAAlgorithm(0.99));

//            builder.setOutput(new OutputMetricScatterPlotter(0, 1));
            builder.setConsoleOutput("CSV");
//        builder.setFileOutput(outputFile, "CSV");

            builder.runApp();

        }
    }

}
