package bitflow4j.main;

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
    private final Wait wait = new Wait();

    public void startDaemon(String name, Runnable runnable) {
        // TODO also manage daemon threads somehow?
        Thread thread = new Thread(runnable);
        thread.setName(name);
        thread.setDaemon(true);
        thread.start();
    }

    public void start(String name, Runnable runnable) {
        synchronized (wait) {
            if (!wait.running()) {
                throw new IllegalStateException("This TaskPool has already been stopped");
            }
            Runner runner = new Runner(name, runnable);
            runners.add(runner);
            runner.start();
        }
    }

    public void start(String name, InterruptibleRunnable runnable) {
        start(name, () -> runnable.run(wait));
    }

    /**
     * This version of Runnable can be used to get access to a Wait object that
     * is used to query whether the Runnable should shut down.
     */
    public interface InterruptibleRunnable {
        void run(Wait wait);
    }

    public class Wait {
        private boolean running = true;

        /**
         * If this returns false, the InterruptibleRunnable task should shut down gracefully
         * as soon as possible.
         */
        public synchronized boolean running() {
            return running;
        }

        /**
         * Sleep the number of milliseconds and return true, if the task should continue running
         * after the sleep call returns.
         * If false is returned, the task should shut down gracefully as soon as possible.
         * In this case, the sleep might be interrupted.
         */
        public synchronized boolean sleep(long millis) {
            if (!running) {
                return false;
            }
            try {
                wait(millis);
            } catch (InterruptedException ignored) {
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

        public synchronized void shutdown(String reason) {
            logger.info("Shutting down: " + reason);
            running = false;
            notifyAll();
        }
    }

    /**
     * Notify all running tasks to stop.
     */
    public void stop(String reason) {
        wait.shutdown(reason);
    }

    public void waitForTasks() {
        wait.waitForShutdown();
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

        private final Runnable runnable;

        Runner(String name, Runnable runnable) {
            this.runnable = runnable;
            setName(name);
        }

        public void run() {
            try {
                runnable.run();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception in Task " + getName(), e);
            }
        }

    }

}
