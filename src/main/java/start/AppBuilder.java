package start;

import Algorithms.Algorithm;
import Algorithms.AlgorithmRunner;
import MetricIO.AggregatingMetricInputStream;
import MetricIO.MetricInputStream;
import MetricIO.MetricOutputStream;
import MetricIO.MetricPipe;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fschmidt
 */
public class AppBuilder {

    public int pipeBuffer = 128;

    private final List<Algorithm> algorithms = new ArrayList<>();
    private final List<MetricInputStream> inputs = new ArrayList<>();
    MetricOutputStream output;

    private final List<AlgorithmRunner> algorithmRunners = new ArrayList<>();

    public void addAlgorithm(Algorithm algo) {
        algorithms.add(algo);
    }

    public void addInput(MetricInputStream input) {
        inputs.add(input);
    }

    public void setOutput(MetricOutputStream output) {
        this.output = output;
    }

    public void runApp() {
        if (algorithms.size() == 0) {
            System.err.println("No algorithms selected");
            return;
        }
        if (inputs.size() == 0) {
            System.err.println("No inputs selected");
            return;
        }
        if (output == null) {
            System.err.println("No output selected");
            return;
        }
        this.doRun();
    }

    private void doRun() {
        AggregatingMetricInputStream aggregator = new AggregatingMetricInputStream();
        for (MetricInputStream inputStream : inputs) {
            aggregator.addInput(inputStream);
        }
        aggregator.start();

        MetricInputStream runningInput = aggregator;
        for (int i = 0; i < algorithms.size(); i++) {
            Algorithm algo = algorithms.get(i);
            MetricInputStream input = runningInput;
            MetricOutputStream output;

            if (i < algorithms.size() - 1) {
                MetricPipe pipe = pipeBuffer > 0 ? new MetricPipe(pipeBuffer) : new MetricPipe();
                runningInput = pipe;
                output = pipe;
            } else {
                output = this.output;
            }

            AlgorithmRunner runner = new AlgorithmRunner(algo, input, output);
            runner.start();
            algorithmRunners.add(runner);
        }
    }

}
