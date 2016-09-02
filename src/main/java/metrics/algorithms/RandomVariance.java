package metrics.algorithms;

import metrics.Sample;
import metrics.main.misc.ParameterHash;

import java.io.IOException;

/**
 * Randomly vary all metrics of all samples coming through this algorithm.
 * The change happens within a certain percentage interval based on the incoming metrics value.
 *
 * Created by anton on 5/2/16.
 */
public class RandomVariance extends AbstractAlgorithm {

    private final float variance;

    public RandomVariance(float variance) {
        this.variance = variance;
    }

    @Override
    protected Sample executeSample(Sample sample) throws IOException {
        double inputValues[] = sample.getMetrics();
        double values[] = new double[inputValues.length];
        for (int i = 0; i < inputValues.length; i++) {
            double value = inputValues[i];
            double maxChange = Math.abs(value) * variance;
            double change = (Math.random() - 0.5) * 2 * maxChange;
            values[i] = value + change;
        }
        return new Sample(sample.getHeader(), values, sample);
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeFloat(variance);
    }

    @Override
    public String toString() {
        return "random variance";
    }

}
