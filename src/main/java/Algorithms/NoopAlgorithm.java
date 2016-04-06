package Algorithms;

import MetricIO.MetricInputStream;
import MetricIO.MetricOutputStream;

import java.io.IOException;

/**
 * Created by anton on 4/6/16.
 */
public class NoopAlgorithm implements Algorithm {

    public void execute(MetricInputStream input, MetricOutputStream output) throws IOException, AlgorithmException {
        output.writeSample(input.readSample());
    }

    public String getName() {
        return "No-op Algorithm";
    }

}
