package metrics.io;

import metrics.Sample;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by anton on 4/5/16.
 */
public class MetricPipe implements MetricInputStream, MetricOutputStream {

    private final BlockingQueue<Sample> values;

    public MetricPipe() {
        values = new LinkedBlockingQueue<>();
    }

    public MetricPipe(int bufferSize) {
        values = new ArrayBlockingQueue<>(bufferSize);
    }

    public Sample readSample() throws IOException {
        while (true) {
            try {
                return values.take();
            } catch (InterruptedException exc) {
                continue;
            }
        }
    }

    public void writeSample(Sample data) throws IOException {
        while (true) {
            try {
                values.put(data);
                return;
            } catch (InterruptedException exc) {
                continue;
            }
        }
    }

}
