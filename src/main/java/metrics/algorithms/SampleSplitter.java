package metrics.algorithms;

import metrics.Sample;
import metrics.io.*;

import java.io.IOException;
import java.util.Random;

/**
 * Created by anton on 4/23/16.
 */
public class SampleSplitter extends GenericAlgorithm implements InputStreamProducer {

    private final boolean copy;
    private final float redirectedPortion;
    private final MetricPipe pipe = new MetricPipe(200);
    private final Random rnd = new Random();

    /**
     * Randomly distribute incoming samples between the MetricOutputStream given in
     * {@link #start(MetricInputStream, MetricOutputStream)} and a second MetricPipe.
     */
    public SampleSplitter(float redirectedPortion) {
        if (redirectedPortion < 0 || redirectedPortion > 1) {
            throw new IllegalArgumentException("redirectedPortion must be in 0..1: " + redirectedPortion);
        }
        this.redirectedPortion = redirectedPortion;
        copy = false;
    }

    /**
     * Every incoming sample will be copied to both output streams.
     */
    public SampleSplitter() {
        this.redirectedPortion = 0;
        copy = true;
    }

    @Override
    protected void executeStep(MetricInputStream input, MetricOutputStream output) throws IOException {
        Sample sample;
        try {
            sample = input.readSample();
        } catch (InputStreamClosedException exc) {
            pipe.close();
            throw exc;
        }

        Sample outputSample = executeSample(sample); // Give subclasses chance to modify
        if (copy) {
            pipe.writeSample(outputSample);
            output.writeSample(outputSample);
        } else {
            if (rnd.nextFloat() < redirectedPortion) {
                pipe.writeSample(outputSample);
            } else {
                output.writeSample(outputSample);
            }
        }
    }

    @Override
    public String toString() {
        return "sample splitter";
    }

    @Override
    public void start(MetricInputAggregator aggregator) {
        aggregator.addInput(toString(), pipe);
    }

}
