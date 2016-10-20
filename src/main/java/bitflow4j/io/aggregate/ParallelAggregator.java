package bitflow4j.io.aggregate;

import bitflow4j.Sample;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Created by anton on 4/28/16.
 * <p>
 * Incoming Samples are given to receivers separately, but all individual inputs are read in parallel.
 */
public class ParallelAggregator extends AbstractParallelAggregator {

    // LinkedHashSet is used like a queue that preserves uniqueness of its elements
    private final LinkedHashSet<AggregatingThread> newInputs = new LinkedHashSet<>();

    protected void waitForNewInput() {
        synchronized (newInputs) {
            while (newInputs.isEmpty()) {
                try {
                    newInputs.wait();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
    }

    void notifyNewInput(AggregatingThread input) {
        synchronized (newInputs) {
            if (input != null)
                newInputs.add(input);
            newInputs.notifyAll();
        }
    }

    @Override
    void receivedNewSample(AggregatingThread thread, Sample sample) {
        synchronized (newInputs) {
            thread.sample = sample;
            notifyNewInput(thread);
        }
    }

    @Override
    protected Sample doReadSample() throws IOException {
        synchronized (newInputs) {
            Iterator<AggregatingThread> iter = newInputs.iterator();
            AggregatingThread nextInput = iter.next();
            iter.remove();
            return nextInput.sample;
        }
    }

}
