package bitflow4j.filter;

import bitflow4j.algorithms.Algorithm;
import bitflow4j.io.MetricOutputStream;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by anton on 23.12.16.
 */
public class SimpleFilter implements Filter {

    private static final Logger logger = Logger.getLogger(SimpleFilter.class.getName());

    private Algorithm algorithm;
    private MetricOutputStream output;

    @Override
    public void start(Algorithm algorithm, MetricOutputStream output) throws IOException {
        if (this.algorithm != null) {
            throw new IllegalStateException("SimpleFilter has already been started for " + algorithm);
        }
        logger.info("Starting " + algorithm + "...");
        this.algorithm = algorithm;
        algorithm.setOutput(output);
    }

    @Override
    public void close() throws Exception {
        Filter.closeAlgorithm(algorithm);
        algorithm = null;
    }

    @Override
    public String toString() {
        if (algorithm == null)
            return "Unstarted SimpleFilter";
        else
            return "SimpleFilter for algorithm: " + algorithm;
    }

}
