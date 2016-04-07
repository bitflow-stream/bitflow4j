package Algorithms;

import MetricIO.MetricInputStream;
import MetricIO.MetricOutputStream;

import java.io.IOException;

/**
 * Created by anton on 4/6/16.
 */
public class NoopAlgorithm implements Algorithm {

    public long sleepTime = 0l;

    public NoopAlgorithm() {
    }

    public NoopAlgorithm(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void execute(MetricInputStream input, MetricOutputStream output) throws IOException, AlgorithmException {
        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
            }
        }
        output.writeSample(input.readSample());
    }

    public String getName() {
        return "No-op Algorithm";
    }

}
