package metrics.algorithms;

import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;

import java.io.IOException;

/**
 * Created by anton on 4/7/16.
 */
public class ZeroFilterAlgorithm implements Algorithm {

    public double varianceThreshold = 0.01;

    public String getName() {
        return "zero-filter algorithm";
    }

    public void execute(MetricInputStream input, MetricOutputStream output) throws IOException, AlgorithmException {

    }

}
