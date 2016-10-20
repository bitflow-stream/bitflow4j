package bitflow4j.algorithms;

import bitflow4j.Sample;
import bitflow4j.io.InputStreamClosedException;
import bitflow4j.io.MetricInputStream;
import bitflow4j.io.MetricOutputStream;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Generic implementation of the Algorithm interface.<br/>
 * Gives subclasses different levels of control to implement the actual algorithm logic by overriding methods.
 * Every invocation of {@link #start(MetricInputStream, MetricOutputStream) start()} spawns a new Thread.
 * If you need control over Thread creation, implement {@link bitflow4j.algorithms.Algorithm Algorithm} directly.
 * One of the following methods should be overridden:
 * <ul>
 * <li>{@link #execute(MetricInputStream, MetricOutputStream) execute()}: Executes the entire algorithm
 * from beginning to end</li>
 * <li>{@link #executeStep(MetricInputStream, MetricOutputStream) executeStep()}: Called within endless
 * loop, can use input/output repeatedly</li>
 * <li>{@link #executeSample(Sample) executeSample()}: Convert a single Sample to an output Sample</li>
 * </ul>
 * {@link InputStreamClosedException} should not be caught (or always rethrown).<br/>
 * When using AbstractAlgorithm directly, all samples from {@link MetricInputStream} are directly forwarded
 * to {@link MetricOutputStream}.
 */
public abstract class AbstractAlgorithm implements Algorithm {

    private static final Logger logger = Logger.getLogger(AbstractAlgorithm.class.getName());

    private static int ID_COUNTER;

    /**
     * Get the next unused free id.
     * @return the next unused id.
     */
    public synchronized static int getNextId(){
        return ID_COUNTER++;
    }

    public boolean catchExceptions = false;
    private Exception startedStacktrace = null;
    private boolean started = false;
    protected int id = getNextId();

    private synchronized static int nextInstanceNumber() {
        return algorithmInstanceCounter++;
    }
    private static int algorithmInstanceCounter = 0;
    private final int algorithmInstanceNumber = nextInstanceNumber();

    public String toString() {
        return getClass().getSimpleName() + " (Instance " + algorithmInstanceNumber + ")";
    }

    public synchronized void start(MetricInputStream input, MetricOutputStream output) throws IOException {
        if (startedStacktrace != null) {
            throw new IllegalStateException("Algorithm was already started: " + toString(), startedStacktrace);
        }
        startedStacktrace = new Exception("This is the stack when first starting this algorithm");
        Runner thread = new Runner(input, output);
        thread.setDaemon(false);
        thread.setName("Algorithm Thread '" + toString() + "'");
        thread.start();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    protected void execute(MetricInputStream input, MetricOutputStream output) throws IOException {
        while (true) {
            try {
                executeStep(input, output);
            } catch (InputStreamClosedException exc) {
                inputClosed(output);
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

    protected void executeStep(MetricInputStream input, MetricOutputStream output) throws IOException {
        Sample sample = input.readSample();
        printStarted();
        Sample outputSample = executeSample(sample);
        if (outputSample != null)
            output.writeSample(outputSample);
    }

    protected Sample executeSample(Sample sample) throws IOException {
        return sample;
    }

    protected void inputClosed(MetricOutputStream output) throws IOException {
        // Hook for subclasses
    }

    protected synchronized void printStarted() {
        if (!started) {
            logger.info("Starting " + toString() + "...");
            started = true;
        }
    }

    public AbstractAlgorithm catchExceptions() {
        catchExceptions = true;
        return this;
    }

    public AbstractAlgorithm reset(){
        this.startedStacktrace = null;
        return this;
    }

    private class Runner extends Thread {

        private final MetricInputStream input;
        private final MetricOutputStream output;

        Runner(MetricInputStream input, MetricOutputStream output) {
            this.input = input;
            this.output = output;
        }

        public void run() {
            String name = AbstractAlgorithm.this.toString();
            try {
                execute(input, output);
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

    @Override
    public Object getModel() {
        throw new UnsupportedOperationException("Not implemented for this class");
    }

    @Override
    public void setModel(Object model) {
        throw new UnsupportedOperationException("Not implemented for this class");
    }

    @Deprecated
    public int getId() {
        return this.id;
    }

    @Override
    @Deprecated
    public boolean equals(Object o){
        return o instanceof AbstractAlgorithm ? ((AbstractAlgorithm) o).getId() == this.getId() : o instanceof Integer ? this.getId() == ((Integer)o).intValue() : false;
    }
}
