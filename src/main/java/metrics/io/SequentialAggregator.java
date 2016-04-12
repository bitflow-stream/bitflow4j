package metrics.io;

import metrics.Sample;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by anton on 4/11/16.
 */
public class SequentialAggregator extends MetricInputAggregator {

    private NamedInputStream currentInput = null;
    private final Queue<NamedInputStream> inputs = new LinkedList<>();

    @Override
    public synchronized void addInput(String name, MetricInputStream input) {
        // TODO the name given here is stored but not used for result Samples
        inputs.offer(new NamedInputStream(input, name));
        notifyAll();
    }

    @Override
    public boolean hasRunningInput() {
        return !inputs.isEmpty() || currentInput != null;
    }

    @Override
    public int size() {
        return inputs.size();
    }

    @Override
    protected synchronized void waitForNewInput() {
        while (!hasRunningInput()) {
            try {
                wait();
            } catch (InterruptedException e) {
                // nothing
            }
        }
    }

    @Override
    protected Sample doReadSample() throws IOException {
        if (currentInput == null)
            currentInput = nextInputStream();
        try {
            Sample sample = currentInput.input.readSample();
            if (unifiedSource != null) {
                sample.setSource(unifiedSource);
            }
            return sample;
        } catch (InputStreamClosedException exc) {
            inputFinished(currentInput.name, null);
            currentInput = null;
            return doReadSample();
        }
    }

    private NamedInputStream nextInputStream() throws InputStreamClosedException {
        NamedInputStream named = inputs.poll();
        if (named == null)
            throw new InputStreamClosedException();
        return named;
    }

    private static class NamedInputStream {
        final String name;
        final MetricInputStream input;

        public NamedInputStream(MetricInputStream input, String name) {
            this.input = input;
            this.name = name;
        }
    }

}