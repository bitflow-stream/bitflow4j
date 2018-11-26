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
        while (!stop && executeIteration()) ;
        synchronized (this) {
            exited = true;
            notifyAll();
        }
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
