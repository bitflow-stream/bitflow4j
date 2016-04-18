package metrics.io;

import metrics.Sample;

import java.io.IOException;

/**
 * @author fschmidt
 */
public interface MetricOutputStream {

    void writeSample(Sample sample) throws IOException;

    void close() throws IOException;

    void waitUntilClosed();

}
