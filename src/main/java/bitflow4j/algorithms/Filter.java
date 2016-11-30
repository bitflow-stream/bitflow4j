package bitflow4j.algorithms;

import bitflow4j.Sample;
import bitflow4j.io.MetricInputStream;
import bitflow4j.io.MetricOutputStream;
import bitflow4j.main.ParameterHash;

import java.io.IOException;

/**
 * Created by malcolmx on 30.11.16.
 */
public interface Filter<T extends Algorithm> extends AutoCloseable {
    /**
     * Get the next unused free id.
     *
     * @return the next unused id.
     */
    static int getNextId() {
        return FilterImpl.ID_COUNTER++;
    }

    Filter catchExceptions();

    Filter reset();

    void start(MetricInputStream input, MetricOutputStream output) throws IOException;

    T getAlgorithm();

    @Override
    String toString();

    /**
     * Unused artifact.
     * If this method is used again, the call should be delegated to the underlying algorithm.
     *
     * @param hash
     */
    @Deprecated
    void hashParameters(ParameterHash hash);

    /**
     * This method can be called by the underlying algorithm in order to write a new sample to the Filters output pipe.
     *
     * @param sample the sample to write
     */
    void writeSample(Sample sample) throws IOException;
}
