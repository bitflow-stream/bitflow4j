package metrics.main;

import metrics.algorithms.PCAAlgorithm;
import metrics.io.OutputMetricScatterPlotter;

import java.io.IOException;

/**
 * @author fschmidt
 */
public class RunApp {

    private static final ExperimentBuilder.Host bono = new ExperimentBuilder.Host("bono.ims", "virtual");
    private static final ExperimentBuilder.Host wally131 = new ExperimentBuilder.Host("wally131", "physical");

    public static void main(String[] args) throws IOException {
//        ExperimentBuilder.Host host = bono;
        ExperimentBuilder.Host host = wally131;

//        AppBuilder builder = new AppBuilder(9999, "BIN");
        AppBuilder builder = new OldExperimentBuilder(Config.EXPERIMENT_FOLDER, host.name, true, false, true);
//        AppBuilder builder = new ExperimentBuilder(Config.EXPERIMENT_FOLDER, host, true);
//        builder.setUnifiedSource(HOST);

//        builder.addAlgorithm(new MetricFilterAlgorithm(0, 1, 2, 3));
//        builder.addAlgorithm(new NoopAlgorithm());
//        builder.addAlgorithm(new VarianceFilterAlgorithm(0.02, true));
//        builder.addAlgorithm(new CorrelationAlgorithm(false));
//        builder.addAlgorithm(new CorrelationSignificanceAlgorithm(0.7));
//        builder.addAlgorithm(new MetricCounter());
        builder.addAlgorithm(new PCAAlgorithm(2));

        builder.setOutput(new OutputMetricScatterPlotter(0,1));
//        builder.setConsoleOutput("CSV");
//        builder.setFileOutput(OUTPUT_FILE, "CSV");

//        builder.runApp();
    }

}
