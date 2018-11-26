package bitflow4j.steps.misc;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.IOException;

/**
 * Randomly vary all metrics of all samples coming through this pipelin step.
 * The change happens within a certain percentage interval based on the incoming metrics value.
 * <p>
 * Created by anton on 5/2/16.
 */
public class RandomVariance extends AbstractPipelineStep {

    private final float variance;

    public RandomVariance(float variance) {
        this.variance = variance;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        double inputValues[] = sample.getMetrics();
        double values[] = new double[inputValues.length];
        for (int i = 0; i < inputValues.length; i++) {
            double value = inputValues[i];
            double maxChange = Math.abs(value) * variance;
            double change = (Math.random() - 0.5) * 2 * maxChange;
            values[i] = value + change;
        }
        output.writeSample(new Sample(sample.getHeader(), values, sample));
    }

    @Override
    public String toString() {
        return "random variance";
    }

}
