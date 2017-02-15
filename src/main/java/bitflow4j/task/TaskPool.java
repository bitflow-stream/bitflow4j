package bitflow4j.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 27.12.16.
 */
public class TaskPool {

    private static final Logger logger = Logger.getLogger(TaskPool.class.getName());

    private final List<Runner> runners = new ArrayList<>();
    private final List<StoppableTask> stoppable = new ArrayList<>();
    private boolean running = true;

    public synchronized void start(Task task) throws IOException {
        assertRunning();
        task.start(this);
        if (task instanceof ParallelTask) {
            Runner runner = new Runner((ParallelTask) task);
            runners.add(runner);
            runner.start();
        }
        if (task instanceof StoppableTask) {
            stoppable.add((StoppableTask) task);
        }
    }

    public synchronized void stop(String reason) {
        if (!running) return;
        logger.info("Shutting down: " + reason);
        running = false;
        for (StoppableTask task : stoppable) {
            try {
                task.stop();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Error stopping task " + task, e);
            }
        }
        notifyAll();
    }

    public boolean isRunning() {
        return running;
    }

    public void assertRunning() {
        if (!running) {
            throw new IllegalStateException("This TaskPool has already been stopped");
        }
    }

    /**
     * Sleep the number of milliseconds and return true, if the task should continue running
     * after the sleep call returns.
     * If false is returned, the task should shut down gracefully as soon as possible.
     * In this case, the sleep might be interrupted.
     */
    public synchronized boolean sleep(long millis) {
        if (running) {
            try {
                wait(millis);
            } catch (InterruptedException ignored) {
            }
        }
        return running;
    }

    public synchronized void waitForShutdown() {
        while (running) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void waitForTasks() {
        logger.fine("Waiting for Task threads to finish...");
        for (Runner runner : runners) {
            while (true) {
                try {
                    runner.join();
                    break;
                } catch (InterruptedException ignored) {
                }
            }
        }
        logger.info("All tasks have finished");
    }

    private class Runner extends Thread {

        private final ParallelTask task;

        Runner(ParallelTask task) {
            this.task = task;
            setName(task.toString());
        }

        public void run() {
            try {
                task.run();
                TaskPool.this.stop("Task finished: " + task);
            } catch (Exception e) {
                String msg = "Exception in Task " + task;
                logger.log(Level.WARNING, msg, e);
                TaskPool.this.stop(msg);
            }
        }

    }

}
