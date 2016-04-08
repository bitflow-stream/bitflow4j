package metrics.io;

import java.util.*;

/**
 * Created by anton on 4/8/16.
 *
 * In this implementation readSample() will block until ALL inputs deliver new data.
 * Likewise, after delivering data each input is delayed until the data is fetched through readSample().
 */
public class LockstepMetricAggregator extends AbstractMetricAggregator {

    // Recursive waits occur without wait-timeouts :(
    private static final long LOCK_TIMEOUT = 100;

    private final Set<AggregatingThread> dataDelivered = Collections.synchronizedSet(new HashSet<>());
    private final Map<AggregatingThread, Object> locks = new HashMap<>();

    private Object getInputLock(AggregatingThread thread) {
        synchronized (locks) {
            if (locks.containsKey(thread)) {
                return locks.get(thread);
            }
            Object lock = new Object();
            locks.put(thread, lock);
            return lock;
        }
    }

    private boolean allDataDelivered() {
        int numRunningInputs = 0;
        for (AggregatingThread thread : activeInputs) {
            if (thread.running)
                numRunningInputs++;
        }
        return numRunningInputs == 0 ||
                (numRunningInputs > 0 && dataDelivered.size() >= numRunningInputs);
    }

    @Override
    protected void waitForNewInput() {
        synchronized (dataDelivered) {
            while (!allDataDelivered()) {
                try {
                    dataDelivered.wait(LOCK_TIMEOUT);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    protected void inputReceived() {
        synchronized (dataDelivered) {
            List<AggregatingThread> copy = new ArrayList<>(dataDelivered);
            dataDelivered.clear();
            for (AggregatingThread input : copy) {
                Object lock = getInputLock(input);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        }
    }

    @Override
    protected void inputReady(AggregatingThread input) {
        Object lock = getInputLock(input);
        while (dataDelivered.contains(input)) {
            synchronized (lock) {
                try {
                    lock.wait(LOCK_TIMEOUT);
                } catch (InterruptedException exc) {
                }
            }
        }
    }

    @Override
    protected void notifyNewInput(AggregatingThread input) {
        synchronized (dataDelivered) {
            dataDelivered.add(input);
            if (allDataDelivered())
                dataDelivered.notifyAll();
        }
    }

    @Override
    protected void removeInput(AggregatingThread thread) {
        synchronized (dataDelivered) {
            dataDelivered.remove(thread);
        }
        // Do not remove inputs: the last state will persist until all inputs are finished.
        // This prevents header changes.
    }

}
