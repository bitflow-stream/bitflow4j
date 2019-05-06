package bitflow4j.task;

import java.io.IOException;

/**
 * Created by anton on 13.02.17.
 */
public abstract class LoopTask implements ParallelTask {

    private boolean stop = false;
    private boolean exited = false;
    protected TaskPool pool;

    protected abstract boolean executeIteration() throws IOException;

    @Override
    public void start(TaskPool pool) throws IOException {
        this.pool = pool;
    }

    @Override
    public void run() throws IOException {
        // This is really ugly, but there is no other way to perform
        // the notifyExited() cleanup, while not dropping the Exception.

        IOException excIO = null;
        RuntimeException excRT = null;
        try {
            runLoop();
        } catch (RuntimeException e) {
            excRT = e;
        } catch (IOException e) {
            excIO = e;
        } finally {
            notifyExited();
        }
        if (excIO != null) {
            throw excIO;
        }
        if (excRT != null) {
            throw excRT;
        }
    }

    private void notifyExited() {
        synchronized (this) {
            exited = true;
            notifyAll();
        }
    }

    private void runLoop() throws IOException {
        while (!stop && executeIteration()) ;
    }

    public void stop() {
        stop = true;
    }

    public boolean isStopped() {
        return stop;
    }

    public boolean isExited() {
        return exited;
    }

    public synchronized void waitForExit() {
        while (!exited)
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
    }

}
