package MetricIO;

import Metrics.Sample;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by anton on 4/5/16.
 */
public class MetricPipe implements MetricInputStream, MetricOutputStream {

    private final BlockingQueue<Sample> values = new LinkedBlockingQueue<>();

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
                values.put(data); // Should never block or throw
                return;
            } catch (InterruptedException exc) {
                continue;
            }
        }
    }

}
