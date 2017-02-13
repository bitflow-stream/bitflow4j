package bitflow4j.algorithms;

import bitflow4j.sample.Sample;

import java.io.IOException;

/**
 * Created by anton on 4/6/16.
 * <p>
 * Algorithm doing nothing but forwarding received samples to the output stream.
 */
public class NoopAlgorithm extends AbstractAlgorithm {

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (output != null) {
            output.writeSample(sample);
        }
    }

}
