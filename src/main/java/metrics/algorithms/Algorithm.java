package metrics.algorithms;

import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;
import metrics.main.misc.ParameterHash;

import java.io.IOException;

/**
 * An Algorithm reads Samples from a MetricInputStream and writes Samples to a MetricOutputStream.
 * The way it does this is completely open: many (or even all) samples can be read before outputting
 * anything, or every sample can be modified and forwarded independently.
 */
public interface Algorithm {

    String toString();

    /**
     * Start a separate Thread and read from input until it throws InputStreamClosedException
     * or too many other Exceptions. Write any number of results to output.
     * After finishing, output.close() must be called.
     */
    void start(MetricInputStream input, MetricOutputStream output) throws IOException;

    default void hashParameters(ParameterHash hash) {
        hash.writeChars(toString());
    }

    Object getModel();

    void setModel(Object model);

}
