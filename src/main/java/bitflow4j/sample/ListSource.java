package bitflow4j.sample;

import bitflow4j.io.SampleReader;
import bitflow4j.io.ThreadedReaderSource;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

/**
 * Created by anton on 15.06.17.
 */
public class ListSource extends ThreadedSource implements ThreadedSource.SampleGenerator {

    private TaskPool pool;
    private final List<Sample> samples;
    private Iterator<Sample> iterator = null;

    // Configuration variables
    public long pauseBetweenSamples = 0;
    public boolean endlessLoop = false;

    public Runnable inputClosedHook;
    private boolean closed = false;
    private ThreadedReaderSource.FileInputFinishedHook fileFinishedHook = null;
    private int readSamples = 0;

    public ListSource(List<Sample> samples) {
        this.samples = samples;
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        this.pool = pool;
        readSamples(this.pool, this);
    }

    @Override
    public void readSamples(TaskPool pool, SampleGenerator generator, boolean keepAlive) throws IOException {
        this.inputClosedHook = this::handleClosedInput;
        super.readSamples(pool, generator, keepAlive);
    }

    @Override
    public synchronized Sample nextSample() throws IOException {
        if (pauseBetweenSamples > 0) {
            pool.sleep(pauseBetweenSamples);
        }
        if (iterator == null || (!iterator.hasNext() && endlessLoop)) {
            iterator = samples.iterator();
        }
        if (!iterator.hasNext()) {
            close();
        }
        readSamples++;
        return iterator.hasNext() ? iterator.next() : null;
    }

    private synchronized void closeCurrentInput() throws IOException {
        Runnable hook = inputClosedHook;
        if (hook != null) {
            hook.run();
        }
    }

    public synchronized void close() throws IOException {
        closed = true;
        closeCurrentInput();
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
