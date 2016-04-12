package metrics.algorithms;

import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;

/**
 *
 * @author fschmidt
 */
public interface Algorithm {

    String toString();

    /**
     * Start a separate Thread and read from input until it throws InputStreamClosedException
     * or too many other Exceptions. Write any number of results to output.
     * After finishing, output.close() must be called.
     */
    void start(MetricInputStream input, MetricOutputStream output);
    
}
