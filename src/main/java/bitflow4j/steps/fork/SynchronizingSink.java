package bitflow4j.steps.fork;

import bitflow4j.sample.AbstractSink;
import bitflow4j.sample.Sample;
import bitflow4j.sample.Sink;

import java.io.IOException;

/**
 * Created by anton on 13.02.17.
 */
public class SynchronizingSink extends AbstractSink {

    // This is intentionally public, so that implementations of PipelineBuilder can
    // configure the sink of the sub-pipeline depending on what comes directly after the fork.
    public final Sink originalOutgoingSink;

    private final Object lock;

    public SynchronizingSink(Object lock, Sink sink) {
        this.originalOutgoingSink = sink;
        this.lock = lock;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        // Make sure to synchronize.
        synchronized (lock) {
            originalOutgoingSink.writeSample(sample);
        }
    }

}
