package bitflow4j.io;

import bitflow4j.Sample;

import java.io.IOException;

/**
 * Created by anton on 4/22/16.
 * <p>
 * OutputStream dropping all incoming Samples.
 */
public class EmptyOutputStream extends AbstractOutputStream {

    @Override
    public void writeSample(Sample sample) throws IOException {
        // Nothing
    }

}
