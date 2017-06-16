package bitflow4j.algorithms;

import bitflow4j.sample.AbstractSampleSink;
import bitflow4j.sample.Sample;
import bitflow4j.sample.SampleSink;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.logging.Level;

public class AbstractAlgorithm<T> extends AbstractSampleSink implements Algorithm<T> {

    protected SampleSink output;

    protected SampleSink output() {
        if (this.output == null) {
            throw new IllegalStateException("The output for this SampleSource has not yet been initialized");
        }
        return output;
    }

    @Override
    public String toString() {
        return "a " + getClass().getSimpleName();
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        output(); // Throw exception, if the output field is not set yet
        super.start(pool);
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        output().writeSample(sample);
    }

    protected void doClose() throws IOException {
        // Nothing by default
    }

    @Override
    public synchronized final void close() {
        // Make sure the close call is propagated to the output, even if an exception occurs
        try {
            doClose();
        } catch (IOException e) {
            logger.log(Level.SEVERE, this + ": Failed to close", e);
        }
        if (output != null) {
            output.close();
        }
        super.close();
    }

    @Override
    public synchronized void waitUntilClosed() {
        super.waitUntilClosed();
        if (output != null) {
            output.waitUntilClosed();
        }
    }

    @Override
    public void setOutgoingSink(SampleSink sink) {
        if (this.output != null) {
            System.out.println("outclass: "+output.getClass().getCanonicalName()+ " , algorithm: "+ this.getClass().getCanonicalName());
            throw new IllegalStateException("This sink for this SampleSource was already initialized "+ sink.getClass().getCanonicalName());
        }
        this.output = sink;
    }

}
