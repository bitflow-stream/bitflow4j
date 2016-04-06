package MetricIO;

import Metrics.Sample;

import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by anton on 4/6/16.
 */
public class MetricInputAggregator implements MetricInputStream {

    // TODO multiple inputs
    private MetricInputStream input;

    private Lock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();

    public void addInput(String name, MetricInputStream input) {
        IllegalStateException exc = null;
        lock.lock();
        try {
            if (this.input != null) {
                exc = new IllegalStateException("Only one input is supported now");
            } else {
                this.input = input;
                connected.signalAll();
            }
        } finally {
            lock.unlock();
        }
        if (exc != null) throw exc;
    }

    public Sample readSample() throws IOException {
        lock.lock();
        try {
            while (input == null) {
                connected.awaitUninterruptibly();
            }
        } finally {
            lock.unlock();
        }
        try {
            return input.readSample();
        } catch (InputStreamClosedException exc) {
            lock.lock();
            try {
                input = null;
            } finally {
                lock.unlock();
            }
            return readSample();
        }
    }

    public int size() {
        return input == null ? 0 : 1;
    }

}
