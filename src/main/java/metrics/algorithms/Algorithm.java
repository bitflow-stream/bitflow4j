package metrics.algorithms;

import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;

import java.io.IOException;

/**
 *
 * @author fschmidt
 */
public interface Algorithm {

    String getName();

    /**
     * Read one sample from the input and optionally output one sample to the output.
     *
     * @param input
     * @param output
     * @throws IOException
     * @throws AlgorithmException
     */
    void execute(MetricInputStream input, MetricOutputStream output) throws IOException, AlgorithmException;
    
}
