package metrics.main;

import metrics.algorithms.NoopAlgorithm;
import metrics.io.plot.OutputMetricPlotter;
import metrics.io.plot.ScatterPlotter;

import java.io.IOException;

public class Main {

    static final Config conf = new Config();

    private static final AbstractExperimentBuilder.Host bono = new AbstractExperimentBuilder.Host("bono.ims", "virtual");
    private static final AbstractExperimentBuilder.Host wally131 = new AbstractExperimentBuilder.Host("wally131", "physical");

    static class OldExpFactory extends OldExperimentBuilder.Factory {
        public AbstractExperimentBuilder makeExperimentBuilder(AbstractExperimentBuilder.Host host) throws IOException {
            return new OldExperimentBuilder(conf, host.name, true,false,false);
        }
    }

    static class NewExpFactory extends ExperimentBuilder.Factory {
        public AbstractExperimentBuilder makeExperimentBuilder(AbstractExperimentBuilder.Host host) throws IOException {
            return new ExperimentBuilder(conf, host, false);
        }
    }

    public static void main(String[] args) throws IOException {
//        ExperimentBuilder.Host host = bono;
        AbstractExperimentBuilder.Host host = wally131;

//        AppBuilder source = new AppBuilder(9999, "BIN");
        DimensionReductionApp.ExperimentBuilderFactory exp = new NewExpFactory();
//        DimensionReductionApp.ExperimentBuilderFactory exp = new OldExpFactory();

        DimensionReductionApp app = new DimensionReductionApp(conf, exp);
//        CodeApp app = new CodeApp(conf, host, source);

        app.runAll();
//        app.runForHost(wally131);
//        app.plotPca(wally131);
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
            builder.addAlgorithm(new NoopAlgorithm());
//        builder.addAlgorithm(new VarianceFilterAlgorithm(0.02, true));
//        builder.addAlgorithm(new CorrelationAlgorithm(false));
//        builder.addAlgorithm(new CorrelationSignificanceAlgorithm(0.7));
//        builder.addAlgorithm(new MetricCounter());
//            builder.addAlgorithm(new PCAAlgorithm(0.99));

            builder.setOutput(new OutputMetricPlotter(new ScatterPlotter(), 0, 1));
//            builder.setConsoleOutput("CSV");
//        builder.setFileOutput(outputFile, "CSV");

            builder.runApp();
        }
    }

}
