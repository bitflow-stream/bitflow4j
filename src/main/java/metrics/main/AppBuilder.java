package metrics.main;

import metrics.algorithms.Algorithm;
import metrics.algorithms.AlgorithmRunner;
import metrics.io.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fschmidt
 */
public class AppBuilder {

    public int pipeBuffer = 128;

    private final List<Algorithm> algorithms = new ArrayList<>();
    private final List<InputStreamProducer> producers = new ArrayList<>();
    private final AbstractMetricAggregator aggregator;
    MetricOutputStream output;

    public AppBuilder(boolean lockStep) {
        if (lockStep) {
            aggregator = new LockstepMetricAggregator();
        } else {
            aggregator = new DecoupledMetricAggregator();
        }
    }

    private final List<AlgorithmRunner> algorithmRunners = new ArrayList<>();

    public void addAlgorithm(Algorithm algo) {
        algorithms.add(algo);
    }

    public void addInput(String name, MetricInputStream input) {
        aggregator.addInput(name, input);
    }

    public void addInputProducer(InputStreamProducer producer) {
        producers.add(producer);
    }

    public void setOutput(MetricOutputStream output) {
        this.output = output;
    }

    public void runApp() {
        if (aggregator.size() == 0 && producers.isEmpty()) {
            throw new IllegalStateException("No inputs selected");
        }
        if (output == null) {
            throw new IllegalStateException("No output selected");
        }
        if (algorithms.size() == 0) {
            throw new IllegalStateException("No algorithms selected");
        }
        this.doRun();
    }

    private void doRun() {
        for (InputStreamProducer producer : producers)
            producer.start(aggregator);

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
