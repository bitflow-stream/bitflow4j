package bitflow4j.filter;

import bitflow4j.algorithms.Algorithm;
import bitflow4j.io.MetricOutputStream;

import java.io.IOException;

/**
 * Created by malcolmx on 30.11.16.
 */
public interface Filter extends AutoCloseable {

    void start(Algorithm algorithm, MetricOutputStream output) throws IOException;

    static void closeAlgorithm(Algorithm algorithm) throws IOException {
        if (algorithm != null) {
            algorithm.close();
            algorithm.waitUntilClosed();
        }
    }

}
