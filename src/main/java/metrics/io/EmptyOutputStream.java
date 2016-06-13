package metrics.io;

import metrics.Sample;

import java.io.IOException;

/**
 * Created by anton on 4/22/16.
 *
 * OutputStream dropping all incoming Samples.
 */
public class EmptyOutputStream extends AbstractOutputStream {

    @Override
    public void writeSample(Sample sample) throws IOException {
        // Nothing
    }

}
