package bitflow4j.filter;

import bitflow4j.Sample;
import bitflow4j.algorithms.Algorithm;
import bitflow4j.io.InputStreamClosedException;
import bitflow4j.io.MetricInputStream;
import bitflow4j.io.MetricOutputStream;
import bitflow4j.main.TaskPool;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of Filter that starts a new thread to read from the input stream
 * and push the received samples into an underlying algorithm instance.
 */
public class ThreadedFilter implements Filter {

    private static final Logger logger = Logger.getLogger(ThreadedFilter.class.getName());

    private final TaskPool pool;
    MetricInputStream input;
    private Algorithm algorithm;
    private Exception startedStacktrace = null;
    public boolean catchExceptions = false;

    public ThreadedFilter(TaskPool pool, MetricInputStream input) {
        this.input = input;
        this.pool = pool;
    }

    public Filter catchExceptions() {
        catchExceptions = true;
        return this;
    }

    @Override
    public synchronized void start(Algorithm algorithm, MetricOutputStream output) throws IOException {
        if (startedStacktrace != null) {
            throw new IllegalStateException("Algorithm was already started: " + toString(), startedStacktrace);
        }
        if (algorithm == null) {
            throw new NullPointerException("Algorithm instance is null");
        }
        this.algorithm = algorithm;
        algorithm.setOutput(output);
        startedStacktrace = new Exception("This is the stack when first starting this algorithm");
        logger.info("Starting " + this + "...");
        pool.start(toString(), this::safeExecute);
    }

    private void safeExecute() {
        String name = algorithm.toString();
        try {
            ThreadedFilter.this.execute();
        } catch (InputStreamClosedException exc) {
            logger.info("Input closed for algorithm " + name);
        } catch (IOException exc) {
            logger.severe("Error in " + name);
            exc.printStackTrace();
        } finally {
            logger.info(name + " finished");
            close();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void execute() throws IOException {
        while (true) {
            try {
                Sample sample = input.readSample();
                algorithm.writeSample(sample);
            } catch (InputStreamClosedException exc) {
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

    private void close() {
        if (algorithm != null) {
            try {
                algorithm.close();
                algorithm.waitUntilClosed();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing algorithm " + algorithm, e);
            }
            algorithm = null;
        }
        startedStacktrace = null;
    }

    @Override
    public String toString() {
        if (algorithm == null)
            return "Unstarted ThreadedFilter";
        else
            return "ThreadedFilter for algorithm: " + algorithm;
    }

}
