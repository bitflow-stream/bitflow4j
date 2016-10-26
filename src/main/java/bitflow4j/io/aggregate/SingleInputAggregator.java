package bitflow4j.io.aggregate;

import bitflow4j.Sample;
import bitflow4j.io.InputStreamClosedException;
import bitflow4j.io.MetricInputStream;

import java.io.IOException;

/**
 * Simple implementation of MetricInputAggregator that wraps a single
 * MetricInputStream, reading from it directly.
 * <p>
 * Created by anton on 4/28/16.
 */
public class SingleInputAggregator extends MetricInputAggregator {

    private final MetricInputStream input;
    private boolean running = true;

    public SingleInputAggregator(MetricInputStream input) {
        this.input = input;
    }

    @Override
    public boolean hasRunningInput() {
        return running;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    protected void waitForNewInput() {
        // Nothing
    }

    @Override
    protected Sample doReadSample() throws IOException {
        try {
            return input.readSample();
        } catch (InputStreamClosedException exc) {
            running = false;
            throw exc;
        }
    }

    @Override
    public void addInput(String name, MetricInputStream input) {
        throw new IllegalStateException("Cannot add more inputs to SingleInputAggregator");
    }

}
