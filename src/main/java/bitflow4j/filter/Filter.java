package bitflow4j.filter;

import bitflow4j.algorithms.Algorithm;
import bitflow4j.io.MetricOutputStream;

import java.io.IOException;

/**
 * Created by malcolmx on 30.11.16.
 */
public interface Filter {

    void start(Algorithm algorithm, MetricOutputStream output) throws IOException;

}
