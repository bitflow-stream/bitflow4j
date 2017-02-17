package bitflow4j.io;

import bitflow4j.sample.AbstractSampleSource;
import bitflow4j.sample.Sample;
import bitflow4j.sample.SampleSink;
import bitflow4j.task.LoopTask;
import bitflow4j.task.ParallelTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for implementing SampleSource in case multiple threads
 * are needed, for example when reading from TCP connections.
 * <p>
 * This does not implement StoppableSampleSource, because it always stops on its own
 * and should not explicitly be stopped from the outside.
 * <p>
 * Created by anton on 23.12.16.
 */
public abstract class ThreadedSampleSource extends AbstractSampleSource implements ParallelTask {

    private static final Logger logger = Logger.getLogger(ThreadedSampleSource.class.getName());

    private final Object outputLock = new Object();
    private final List<LoopTask> tasks = new ArrayList<>();
    private boolean shuttingDown = false;

    protected void readSamples(TaskPool pool, MetricReader reader) throws IOException {
        LoopTask task = new LoopSampleReader(reader);
        tasks.add(task);
        pool.start(task);
    }

    @Override
    public void run() throws IOException {
        // Wait for the shutdown and start the close() sequence with our direct output
        synchronized (this) {
            while (!shuttingDown)
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
        }
        for (LoopTask task : tasks) {
            task.waitForExit();
        }
        output().close();
    }

    protected void shutDown() {
        synchronized (this) {
            shuttingDown = true;
            notifyAll();
        }
    }

    protected void stopTasks() {
        shutDown();
        for (LoopTask task : tasks) {
            task.stop();
        }
    }

    protected void readerException() {
        // By default, shut down when an Exception occurs
        stopTasks();
    }

    private class LoopSampleReader extends LoopTask {

        private final MetricReader reader;
        private final SampleSink sink;

        public LoopSampleReader(MetricReader reader) {
            this.reader = reader;
            this.sink = ThreadedSampleSource.this.output();
            reader.inputClosedHook = ThreadedSampleSource.this::handleClosedInput;
        }

        @Override
        public String toString() {
            return reader.toString();
        }

        public boolean executeIteration() throws IOException {
            try {
                if (!pool.isRunning())
                    return false;
                Sample sample = reader.readSample();
                if (sample == null || !pool.isRunning())
                    return false;
                handleReadSample(sample);
                synchronized (outputLock) {
                    sink.writeSample(sample);
                }
                return true;
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Exception in " + reader.toString() +
                        ", running as part of: " + ThreadedSampleSource.this, e);
                readerException();
                return false;
            }
        }

    }

    // ================================================================================================
    // TODO the code below is a hack to enable synchronized reading of files.
    // Must be handled differently in the future.
    // This hack also includes the inputClosedHook field of MetricReader.
    // ================================================================================================

    public static final String INPUT_FILE_SAMPLE_ID_TAG = "input-file-sample-id";

    public interface FileInputFinishedHook {
        // Implementation can block here, the next file will be started only after
        // this method returns
        void finishedFileInput(int numSampleIds);
    }

    private FileInputFinishedHook fileFinishedHook = null;
    private int readSamples = 0;

    public void setFileInputNotification(FileInputFinishedHook hook) {
        fileFinishedHook = hook;
    }

    private void handleReadSample(Sample sample) {
        if (fileFinishedHook != null) {
            synchronized (outputLock) {
                sample.setTag(INPUT_FILE_SAMPLE_ID_TAG, String.valueOf(readSamples));
                readSamples++;
            }
        }
    }

    private void handleClosedInput() {
        if (fileFinishedHook != null) {
            synchronized (outputLock) {
                fileFinishedHook.finishedFileInput(readSamples);
                readSamples = 0;
            }
        }
    }

}
