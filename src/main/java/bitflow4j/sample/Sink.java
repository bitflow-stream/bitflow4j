package bitflow4j.sample;

import bitflow4j.task.Task;

import java.io.IOException;

/**
 * Basic interface for writing a stream of Samples.
 *
 * @author fschmidt
 */
public interface Sink extends Task {

    void writeSample(Sample sample) throws IOException;

    void waitUntilClosed();

    void close();

}
