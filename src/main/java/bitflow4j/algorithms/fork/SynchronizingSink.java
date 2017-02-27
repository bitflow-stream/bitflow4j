package bitflow4j.algorithms.fork;

import bitflow4j.sample.AbstractSampleSink;
import bitflow4j.sample.Sample;
import bitflow4j.sample.SampleSink;

import java.io.IOException;

/**
 * Created by anton on 13.02.17.
 */
public class SynchronizingSink extends AbstractSampleSink {

    // This is intentionally public, so that implementations of PipelineBuilder can
    // configure the sink of the sub-pipeline depending on what comes directly after the fork.
    public final SampleSink originalOutgoingSink;

    public SynchronizingSink(SampleSink sink) {
        this.originalOutgoingSink = sink;
    }

    @Override
    public synchronized void writeSample(Sample sample) throws IOException {
        // Make sure to synchronize.
        originalOutgoingSink.writeSample(sample);
    }

}
