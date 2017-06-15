package bitflow4j.sample;

import java.io.IOException;

/**
 * Created by anton on 4/22/16.
 * <p>
 * OutputStream dropping all incoming Samples.
 */
public class EmptySink extends AbstractSink {

    @Override
    public void writeSample(Sample sample) throws IOException {
        // Nothing
    }

}
