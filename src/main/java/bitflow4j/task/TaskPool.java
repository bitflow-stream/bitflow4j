package bitflow4j.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A TaskPool manages the lifecycle of an application, which consists of a number of Tasks. Task instances can be added
 * to a TaskPool, which causes them to be initialized via their own start method. Instances of ParallelTask are then
 * executed in their own thread. Instances of StoppableTask are notified via their stop method once the entire TaskPool
 * shuts down. Shutdown can be invoked by any task at any time, for example when an error occurs or when some finite input
 * data is entirely processed.
 * <p>
 * Created by anton on 27.12.16.
 */
public class TaskPool {

    private static final Logger logger = Logger.getLogger(TaskPool.class.getName());

    private final IdentityHashMap<Task, Object> startedTasks = new IdentityHashMap<>(); // Used as set
    private final List<Runner> runners = new ArrayList<>();
    private final List<StoppableTask> stoppable = new ArrayList<>();
    private boolean running = true;

    /**
     * Starts and initializes the given task. Instances of ParallelTask are executed in their own thread.
     */
    public synchronized void start(Task task) throws IOException {
        start(task, false);
    }

    /**
     * If backgroundTask is true, and the task is an instance of ParallelTask, then the TaskPool is NOT shut down,
     * when the task finishes or throws an exception. In that case the finished task is logged, but the TaskPool and all
     * other tasks continue running. If backgroundTask is false, the finished ParallelTask will automatically call this
     * TaskPools stop method.
     */
    public synchronized void start(Task task, boolean backgroundTask) throws IOException {
        assertRunning();
        if (startedTasks.containsKey(task)) {
            throw new TaskException("Task already started in this TaskPool, possible recursive invocation of TaskPool.start(): " + task);
        }
        startedTasks.put(task, null);
        boolean isParallel = task instanceof ParallelTask;
        boolean isStoppable = task instanceof StoppableTask;
        logger.fine(String.format("Starting task (parallel: %s, stoppable: %s, background: %s): %s", isParallel, isStoppable, backgroundTask, task));

        task.start(this);
        if (isParallel) {
            Runner runner = new Runner((ParallelTask) task, backgroundTask);
            runners.add(runner);
            runner.start();
        }
        if (isStoppable) {
            stoppable.add((StoppableTask) task);
        }
    }

    public synchronized void stop(String reason) {
        if (!running) return;
        logger.info("Shutting down: " + reason);
        running = false;
        for (StoppableTask task : stoppable) {
            logger.fine("Stopping task: " + task);
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

    public void assertRunning() throws TaskException {
        if (!running) {
            throw new TaskException("This TaskPool has already been stopped");
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
        boolean haveRunningThread;
        do {
            haveRunningThread = false;
            for (Runner runner : runners) {
                if (runner.isAlive()) {
                    haveRunningThread = true;
                    logger.fine("Waiting for task to finish: " + runner);
                    while (true) {
                        try {
                            runner.join();
                            break;
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }
        } while (haveRunningThread);
        logger.info("All tasks have finished");
    }

    private class Runner extends Thread {

        private final ParallelTask task;
        private final boolean backgroundTask;

        Runner(ParallelTask task, boolean backgroundTask) {
            this.task = task;
            this.backgroundTask = backgroundTask;
            setName(task.toString());
        }

        public void run() {
            try {
                task.run();
                if (!backgroundTask)
                    TaskPool.this.stop("Finished " + taskString());
            } catch (Throwable e) {
                String msg = "Exception in " + taskString();
                logger.log(Level.SEVERE, msg, e);
                if (!backgroundTask)
                    TaskPool.this.stop(msg);
            }
        }

        private String taskString() {
            return (backgroundTask ? "Background " : "") + "Task " + task;
        }

    }

}
