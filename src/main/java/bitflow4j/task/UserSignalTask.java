package bitflow4j.task;

import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by anton on 14.02.17.
 */
public class UserSignalTask implements ParallelTask, SignalHandler, StoppableTask {

    private static final Logger logger = Logger.getLogger(UserSignalTask.class.getName());

    public static String HANDLED_SIGNALS[] = new String[]{
            "HUP", "INT", "TERM" // "QUIT",
    };

    private final String handled_signals[];
    boolean signalTriggered = false;
    private final Map<String, SignalHandler> oldHandlers = new HashMap<>();

    public UserSignalTask() {
        this(HANDLED_SIGNALS);
    }

    public UserSignalTask(String... handled_signals) {
        this.handled_signals = handled_signals;
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        for (String signal : handled_signals) {
            registerSignal(signal);
        }
    }

    private void registerSignal(String signal) {
        Signal diagSignal = new Signal(signal);
        SignalHandler oldHandler = Signal.handle(diagSignal, this);
        oldHandlers.put(signal, oldHandler);
    }

    @Override
    public synchronized void run() throws IOException {
        while (!signalTriggered) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        dumpStacktracesAfter(2000);
    }

    @Override
    public synchronized void handle(Signal signal) {
        logger.info("Received signal " + signal + ", shutting down");
        stop();

        // Forward the signal to the original handler
        /*
        SignalHandler oldHandler = oldHandlers.get(signal.getName());
        if (oldHandler != null && oldHandler != SIG_DFL && oldHandler != SIG_IGN)
            oldHandler.handle(signal);
        */
    }

    @Override
    public synchronized void stop() {
        // Restore the old signal handlers
        for (Map.Entry<String, SignalHandler> oldHandler : oldHandlers.entrySet()) {
            Signal signal = new Signal(oldHandler.getKey());
            Signal.handle(signal, oldHandler.getValue());
        }

        signalTriggered = true;
        notifyAll();
    }

    // Dump stack traces in a daemon thread, after waiting a number of milli seconds.
    // Intention: debugging shutdown sequence, when all Thread should die within
    public static void dumpStacktracesAfter(long millis) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException ignored) {
                }
                dumpStacktraces();
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public static void dumpStacktraces() {
        ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
        System.err.println("\n\n ================== Dumping all Thread stack traces ==================");
        for (ThreadInfo thread : threads) {
            System.err.println("\n\n" + thread.toString());
        }
    }

}
