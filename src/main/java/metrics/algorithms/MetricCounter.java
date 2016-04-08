package metrics.algorithms;

import metrics.Sample;
import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;

import java.io.IOException;

/**
 * Created by anton on 4/8/16.
 */
public class MetricCounter implements Algorithm {

    private static final String[] header = new String[] { "metrics" };

    public String getName() {
        return "metric counter";
    }

    @Override
    public void execute(MetricInputStream input, MetricOutputStream output) throws IOException, AlgorithmException {
        Sample sample = input.readSample();
        double num = (double) sample.getMetrics().length;
        output.writeSample(new Sample(header, sample.getTimestamp(), new double[] { num } ));
    }

}
