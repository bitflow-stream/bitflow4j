package bitflow4j.io;

import bitflow4j.AbstractSource;
import bitflow4j.PipelineStep;
import bitflow4j.Sample;
import bitflow4j.task.LoopTask;
import bitflow4j.task.ParallelTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for implementing Source in case multiple threads
 * are needed, for example when reading from TCP connections or files.
 * <p>
 * Created by anton on 23.12.16.
 */
public abstract class ThreadedSource extends AbstractSource implements ParallelTask {

    private static final Logger logger = Logger.getLogger(ThreadedSource.class.getName());

    protected TaskPool pool;
    protected final Object outputLock = new Object();
    private final List<LoopTask> tasks = new ArrayList<>();
    private boolean initializingReaders = true;

    @Override
    public void start(TaskPool pool) throws IOException {
        this.pool = pool;
    }

    public interface SampleGenerator {
        Sample nextSample() throws IOException;
    }

    /**
     * Read samples from the given generator, until an Exception is thrown. Shut down the application after the first
     * Exception. This is suitable when reading samples from a file or the standard input.
     * <p>
     * Exceptions from the writeSample() method of the subsequent processing step will also shut down the application.
     */
    public void readSamples(SampleGenerator generator) throws IOException {
        readSamples(generator, true);
    }

    /**
     * Read samples form the given generator. If an Exception occurs when reading a sample, log the exception, but do
     * not shut down. This is suitable when reading samples from the network, where errors are expected.
     * <p>
     * Exceptions from the writeSample() method of the subsequent processing step will still shut down the application.
     */
    public void readSamplesRobust(SampleGenerator generator) throws IOException {
        readSamples(generator, false);
    }

    // This method is not public to force usage of one of the other 2 readSamples* methods for clarity.
    private void readSamples(SampleGenerator generator, boolean readerExceptionsAreFatal) throws IOException {
        LoopTask task = new LoopSampleReaderTask(generator, readerExceptionsAreFatal);
        tasks.add(task);
        pool.start(task, false);
    }

    @Override
    public void run() throws IOException {
        // Wait for initialization of all readers, then wait for all readers to exit.
        // Then start the close() sequence with our direct output.
        synchronized (this) {
            while (initializingReaders)
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
        }
        tasks.forEach(LoopTask::waitForExit);
        output().close();
    }

    // initFinished() should be called in an overridden run() or start() method, after all sub-tasks have been started.
    protected void initFinished() {
        synchronized (this) {
            initializingReaders = false;
            notifyAll();
        }
    }

    public void close() {
        initFinished();
        tasks.forEach(LoopTask::stop);
        // The output is closed in run(), after all tasks finish.
    }

    private class LoopSampleReaderTask extends LoopTask {

        private final SampleGenerator generator;
        private final PipelineStep sink;
        private final boolean fatalReaderExceptions;

        public LoopSampleReaderTask(SampleGenerator generator, boolean fatalReaderExceptions) {
            this.generator = generator;
            this.fatalReaderExceptions = fatalReaderExceptions;
            this.sink = ThreadedSource.this.output();
        }

        @Override
        public String toString() {
            return String.format("%s reading from %s", getClass().getName(), generator);
        }

        @Override
        public boolean executeIteration() throws IOException {
            if (!pool.isRunning()) return false;
            Sample sample = readSample();
            if (sample == null) return true;
            if (!pool.isRunning()) return false;

            synchronized (outputLock) {
                sink.writeSample(sample);
            }
            return true;
        }

        private Sample readSample() throws IOException {
            try {
                Sample sample = generator.nextSample();
                if (sample == null) {
                    // This is exception is handled below
                    throw new IOException(toString() + " produced a null Sample");
                }
                return sample;
            } catch (IOException e) {
                if (fatalReaderExceptions) {
                    throw e;
                } else {
                    logger.log(Level.SEVERE, "Non-fatal exception in " + toString(), e);
                    return null;
                }
            }
        }

    }

}
