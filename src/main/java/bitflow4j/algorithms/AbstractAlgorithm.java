package bitflow4j.algorithms;

import bitflow4j.io.AbstractOutputStream;
import bitflow4j.io.MetricOutputStream;

import java.io.IOException;

public abstract class AbstractAlgorithm extends AbstractOutputStream implements Algorithm {

    protected MetricOutputStream output;

    @Override
    public void setOutput(MetricOutputStream output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return "a " + getClass().getSimpleName();
    }

    public synchronized void close() throws IOException {
        super.close();
        if (output != null) {
            output.close();
        }
    }

    public synchronized void waitUntilClosed() {
        super.waitUntilClosed();
        if (output != null) {
            output.waitUntilClosed();
        }
    }

}
