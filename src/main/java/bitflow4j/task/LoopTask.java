package bitflow4j.task;

import java.io.IOException;

/**
 * Created by anton on 13.02.17.
 */
public abstract class LoopTask implements ParallelTask {

    protected boolean stop = false;
    private boolean exited = false;

    protected abstract boolean execute(TaskPool pool) throws IOException;

    @Override
    public void start(TaskPool pool) throws IOException {
        while (!stop && execute(pool)) ;
        synchronized (this) {
            exited = true;
            notifyAll();
        }
    }

    public void stop() {
        stop = true;
    }

    public synchronized void waitForExit() {
        while (!exited)
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
    }

}
