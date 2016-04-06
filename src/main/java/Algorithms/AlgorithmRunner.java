package Algorithms;

import MetricIO.InputStreamClosedException;
import MetricIO.MetricInputStream;
import MetricIO.MetricOutputStream;

import java.io.IOException;

/**
 * Created by anton on 4/6/16.
 */
public class AlgorithmRunner extends Thread {

    private final Algorithm algorithm;
    private final MetricInputStream input;
    private final MetricOutputStream output;

    public AlgorithmRunner(Algorithm algo, MetricInputStream input, MetricOutputStream output) {
        this.algorithm = algo;
        this.input = input;
        this.output = output;
        this.setDaemon(false);
    }

    @Override
    public void run() {
        while (true) {
            String name = this.algorithm.getName();
            try {
                this.algorithm.execute(input, output);
            } catch (InputStreamClosedException exc) {
                System.err.println("Input closed for algorithm " + name);
                return;
            } catch (IOException e) {
                System.err.println("IO Error executing algorithm " + name);
                e.printStackTrace();
            } catch (AlgorithmException e) {
                System.err.println("Error executing algorithm " + name);
                e.printStackTrace();
            }
        }
    }

}
