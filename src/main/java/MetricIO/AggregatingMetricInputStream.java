package MetricIO;

import Metrics.Sample;

import java.io.IOException;

/**
 * Created by anton on 4/6/16.
 */
public class AggregatingMetricInputStream implements MetricInputStream {

    // TODO multiple inputs
    private MetricInputStream input;

    public void addInput(MetricInputStream input) {
        if (this.input != null) {
            throw new IllegalStateException("Only one input is supported now");
        }
        this.input = input;
    }

    public Sample readSample() throws IOException {
        return input.readSample();
    }

    public void start() {
        // TODO
    }

}
