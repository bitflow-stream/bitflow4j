package bitflow4j.steps.fork;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.PipelineStep;
import bitflow4j.Sample;

import java.io.IOException;

/**
 * Created by anton on 13.02.17.
 */
public class SynchronizingSink extends AbstractPipelineStep {

    // This is intentionally public, so that implementations of PipelineBuilder can
    // configure the sink of the sub-pipeline depending on what comes directly after the fork.
    public final PipelineStep originalOutgoingSink;

    private final Object lock;

    public SynchronizingSink(Object lock, PipelineStep sink) {
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
