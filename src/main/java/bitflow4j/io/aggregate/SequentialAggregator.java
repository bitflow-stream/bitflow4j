package bitflow4j.io.aggregate;

import bitflow4j.Sample;
import bitflow4j.io.InputStreamClosedException;
import bitflow4j.io.MetricInputStream;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Multiple MetricInputStreams are read in sequential order, always waiting for the
 * current input stream to finish. Only useful for input streams that are known
 * to finish in finite time, like reading from a file.
 * <p>
 * Created by anton on 4/11/16.
 */
public class SequentialAggregator extends MetricInputAggregator {

    private NamedInputStream currentInput = null;
    private final Queue<NamedInputStream> inputs = new LinkedList<>();

    @Override
    public synchronized void addInput(String name, MetricInputStream input) {
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
            // TODO maybe use currentInput.name somehow?
            return currentInput.input.readSample();
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
