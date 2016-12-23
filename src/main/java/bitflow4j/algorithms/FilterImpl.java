package bitflow4j.algorithms;

import bitflow4j.Sample;
import bitflow4j.io.InputStreamClosedException;
import bitflow4j.io.MetricInputStream;
import bitflow4j.io.MetricOutputStream;
import bitflow4j.main.ParameterHash;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Generic implementation of the Algorithm interface.<br/>
 * Gives subclasses different levels of control to implement the actual algorithm logic by overriding methods.
 * Every invocation of {@link #start(MetricInputStream, MetricOutputStream) start()} spawns a new Thread.
 * If you need control over Thread creation, implement {@link bitflow4j.algorithms.Algorithm Algorithm} directly.
 * One of the following methods should be overridden:
 * <ul>
 * <li>{@link #execute() execute()}: Executes the entire algorithm
 * from beginning to end</li>
 * <li>{@link #executeStep() executeStep()}: Called within endless
 * loop, can use input/output repeatedly</li>
 * </ul>
 * {@link InputStreamClosedException} should not be caught (or always rethrown).<br/>
 * When using AbstractAlgorithm directly, all samples from {@link MetricInputStream} are directly forwarded
 * to {@link MetricOutputStream}.
 */
public class FilterImpl<T extends Algorithm> implements Filter<T> {
    private static final Logger logger = Logger.getLogger(FilterImpl.class.getName());
    static int algorithmInstanceCounter = 0;
    static int ID_COUNTER;
    private final int algorithmInstanceNumber = FilterImpl.nextInstanceNumber();
    public boolean catchExceptions = false;
    protected int id = Filter.getNextId();
    protected T algorithm;
    MetricInputStream input;
    MetricOutputStream output;

    //TODO set algorithm instance number
    private Exception startedStacktrace = null;
    private boolean started = false;

    public FilterImpl(T algorithm) {
        this.algorithm = algorithm;
        this.algorithm.init(this);
    }

    synchronized static int nextInstanceNumber() {
        return algorithmInstanceCounter++;
    }

    protected synchronized void printStarted() {
        if (!started) {
            logger.info("Starting " + toString() + "...");
            started = true;
        }
    }

    @Override
    public Filter catchExceptions() {
        catchExceptions = true;
        return this;
    }

    @Override
    public Filter reset() {
        this.startedStacktrace = null;
        return this;
    }

    @Override
    public synchronized void start(MetricInputStream input, MetricOutputStream output) throws IOException {
        this.input = input;
        this.output = output;
        if (startedStacktrace != null) {
            throw new IllegalStateException("Algorithm was already started: " + toString(), startedStacktrace);
        }
        startedStacktrace = new Exception("This is the stack when first starting this algorithm");
        printStarted();
        Runner thread = new Runner(input, output);
        thread.setDaemon(false);
        thread.setName("Algorithm Thread '" + toString() + "'");
        thread.start();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    protected void execute() throws IOException {
        if (!started) throw new IOException("Filter not started, cannot execute!");
        while (true) {
            try {
                executeStep();
            } catch (InputStreamClosedException exc) {
                close();
                throw exc;
            } catch (IOException exc) {
                if (catchExceptions) {
                    logger.severe("IO Error executing " + toString());
                    exc.printStackTrace();
                } else {
                    throw exc;
                }
            }
        }
    }

    protected void executeStep() throws IOException {
        if (!started) throw new IOException("Filter not started, cannot execute!");
        Sample sample = this.input.readSample();

        Sample outputSample = algorithm.writeSample(sample);
        if (outputSample != null)
            this.output.writeSample(outputSample);
    }

    @Override
    public void close() throws IOException {
        if (!started) throw new IOException("Filter not started, cannot close underlying algorithm!");
        this.algorithm.close();
    }


    @Override
    public T getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Filter for algorithm: ");
        sb.append(algorithm == null ? "null" : algorithm.toString());
        return sb.toString();
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        output.writeSample(sample);
    }

    private class Runner extends Thread {
        Runner(MetricInputStream input, MetricOutputStream output) {
            FilterImpl.this.input = input;
            FilterImpl.this.output = output;
        }

        public void run() {
            String name = FilterImpl.this.algorithm.toString();
            try {
                FilterImpl.this.execute();
            } catch (InputStreamClosedException exc) {
                logger.info("Input closed for algorithm " + name);
            } catch (Throwable exc) {
                logger.severe("Error in " + getName());
                exc.printStackTrace();
            } finally {
                logger.info(name + " finished");
                try {
                    output.close();
                } catch (IOException e) {
                    logger.severe("Error closing output of algorithm " + name);
                    e.printStackTrace();
                }
            }
        }

    }
}
