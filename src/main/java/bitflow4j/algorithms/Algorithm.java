package bitflow4j.algorithms;

import bitflow4j.Sample;
import bitflow4j.main.ParameterHash;

import java.io.IOException;

/**
 * An Algorithm reads Samples from a MetricInputStream and writes Samples to a MetricOutputStream.
 * The way it does this is completely open: many (or even all) samples can be read before outputting
 * anything, or every sample can be modified and forwarded independently.
 */
public interface Algorithm extends AutoCloseable {

    void init(FilterImpl worker);

    String toString();

    /**
     * Start a separate Thread and read from input until it throws InputStreamClosedException
     * or too many other Exceptions. Write any number of results to output.
     * After finishing, output.close() must be called.
     */
//    @Deprecated
//    void start(MetricInputStream input, MetricOutputStream output) throws IOException;

    AlgorithmModel<?> getModel();

    void setModel(AlgorithmModel<?> model);

    /**
     * Write the next sample to the inbound queue.
     * This method will be called by the predecessor of this algorithm.
     *
     * @param sample The next sample.
     */
    Sample writeSample(Sample sample) throws IOException;

    @Override
    void close() throws IOException;

}
