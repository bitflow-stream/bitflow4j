package metrics.io;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by anton on 4/8/16.
 *
 * In this implementation readSample() will block until ALL inputs deliver new data.
 * Likewise, after delivering data each input is delayed until the data is fetched through readSample().
 */
public class LockstepMetricAggregator extends AbstractMetricAggregator {

    private Set<AggregatingThread> dataDelivered = Collections.synchronizedSet(new HashSet<>());

    private boolean allDataDelivered() {
        synchronized (activeInputs) {
            return dataDelivered.size() >= activeInputs.size();
        }
    }

    @Override
    protected void waitForNewInput() {
        synchronized (dataDelivered) {
            while (!allDataDelivered()) {
                try {
                    dataDelivered.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    protected void inputReceived() {
        synchronized (dataDelivered) {
            dataDelivered.forEach(AggregatingThread::notifyAll);
            dataDelivered.clear();
        }
    }

    @Override
    protected void inputReady(AggregatingThread input) {
        synchronized (input) {
            while (dataDelivered.contains(input)) {
                try {
                    input.wait();
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

}
