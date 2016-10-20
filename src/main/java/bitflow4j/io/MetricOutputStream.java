package bitflow4j.io;

import bitflow4j.Sample;

import java.io.IOException;

/**
 * Basic interface for writing a stream of Samples.
 *
 * @author fschmidt
 */
public interface MetricOutputStream {

    void writeSample(Sample sample) throws IOException;

    void close() throws IOException;

    void waitUntilClosed();

}
