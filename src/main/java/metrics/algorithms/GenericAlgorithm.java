package metrics.algorithms;

import metrics.Sample;
import metrics.io.InputStreamClosedException;
import metrics.io.MetricInputStream;
import metrics.io.MetricOutputStream;

import java.io.IOException;

/**
 * Generic implementation of the Algorithm interface.<br/>
 * Gives subclasses different levels of control to implement the actual algorithm logic by overriding methods.
 * Every invocation of {@link #start(MetricInputStream, MetricOutputStream) start()} spawns a new Thread.
 * If you need control over Thread creation, implement {@link metrics.algorithms.Algorithm Algorithm} directly.
 * One of the following methods should be overridden:
 * <ul>
 * <li>{@link #execute(MetricInputStream, MetricOutputStream) execute()}: Executes the entire algorithm
 * from beginning to end</li>
 * <li>{@link #executeStep(MetricInputStream, MetricOutputStream) executeStep()}: Called within endless
 * loop, can use input/output repeatedly</li>
 * <li>{@link #executeSample(Sample) executeSample()}: Convert a single Sample to an output Sample</li>
 * </ul>
 * {@link InputStreamClosedException} should not be caught (or always rethrown).<br/>
 * When using GenericAlgorithm directly, all samples from {@link MetricInputStream} are directly forwarded
 * to {@link MetricOutputStream}.
 */
public abstract class GenericAlgorithm implements Algorithm {

    public boolean catchExceptions = false;

    public abstract String toString();

    public final void start(MetricInputStream input, MetricOutputStream output) {
        Runner thread = new Runner(input, output);
        thread.setDaemon(false);
        thread.setName("Algorithm Thread '" + toString() + "'");
        thread.start();
    }

    protected void execute(MetricInputStream input, MetricOutputStream output) throws IOException {
        while (true) {
            try {
                executeStep(input, output);
            } catch (InputStreamClosedException exc) {
                throw exc;
            } catch (IOException exc) {
                if (catchExceptions) {
                    System.err.println("IO Error executing " + toString());
                    exc.printStackTrace();
                } else {
                    throw exc;
                }
            }
        }
    }

    protected void executeStep(MetricInputStream input, MetricOutputStream output) throws IOException {
        Sample sample = input.readSample();
        Sample outputSample = executeSample(sample);
        output.writeSample(outputSample);
    }

    protected Sample executeSample(Sample sample) throws IOException {
        return sample;
    }

    private class Runner extends Thread {

        private final MetricInputStream input;
        private final MetricOutputStream output;

        Runner(MetricInputStream input, MetricOutputStream output) {
            this.input = input;
            this.output = output;
        }

        public void run() {
            String name = GenericAlgorithm.this.toString();
            try {
                execute(input, output);
            } catch (InputStreamClosedException exc) {
                System.err.println("Input closed for algorithm " + name);
            } catch (Throwable exc) {
                System.err.println("Error in " + getName());
                exc.printStackTrace();
            } finally {
                try {
                    output.close();
                } catch (IOException e) {
                    System.err.println("Error closing output of algorithm " + name);
                    e.printStackTrace();
                }
            }
        }

    }

}
