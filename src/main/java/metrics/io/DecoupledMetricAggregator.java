package metrics.io;

/**
 * Created by anton on 4/8/16.
 *
 * Inputs are read independently from readSample(), overwriting intermediate data if readSample() is not called
 * frequently enough.
 * readSample() will only block until the data changes in any way:
 * an input stream delivers data, a new input stream is added, or an input stream is closed.
 */
public class DecoupledMetricAggregator extends AbstractMetricAggregator {

    private final Object newInputLock = new Object();
    private boolean newInput = false;

    @Override
    protected void inputReady(AggregatingThread input) {
        // nothing
    }

    @Override
    protected void waitForNewInput() {
        synchronized (newInputLock) {
            while (!newInput) {
                try {
                    newInputLock.wait();
                } catch (InterruptedException e) {
                }
            }
            newInput = false;
        }
    }

    @Override
    protected void notifyNewInput(AggregatingThread input) {
        synchronized (newInputLock) {
            newInput = true;
            newInputLock.notifyAll();
        }
    }

}
