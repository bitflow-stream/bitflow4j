package bitflow4j.sample;

import bitflow4j.sample.AbstractSampleSink;
import bitflow4j.sample.Sample;

import java.io.IOException;

/**
 * Created by anton on 4/22/16.
 * <p>
 * OutputStream dropping all incoming Samples.
 */
public class EmptySink extends AbstractSampleSink {

    @Override
    public void writeSample(Sample sample) throws IOException {
        // Nothing
    }

}
