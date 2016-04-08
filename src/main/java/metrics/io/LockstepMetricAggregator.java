package metrics.io;

/**
 * Created by anton on 4/8/16.
 *
 * In this implementation readSample() will block until ALL inputs deliver new data.
 * Likewise, after delivering data each input is delayed until the data is fetched through readSample().
 */
public class LockstepMetricAggregator extends AbstractMetricAggregator {

    private int collectedInputs = 0;
    private final Object collectedLock = new Object();

    @Override
    protected void waitForNewInput() {
        synchronized (collectedLock) {
            while (collectedInputs < activeInputs.size()) {
                try {
                    collectedLock.wait();
                } catch (InterruptedException e) {
                }
            }
            collectedInputs = 0;
        }
    }

    @Override
    protected void inputReady(AggregatingThread input) {
        // TODO: block
    }

    @Override
    protected void notifyNewInput(AggregatingThread input) {
        synchronized (collectedLock) {
            collectedInputs++;
            collectedLock.notifyAll();
        }
    }

}
