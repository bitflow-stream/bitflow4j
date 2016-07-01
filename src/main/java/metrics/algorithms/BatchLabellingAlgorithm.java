package metrics.algorithms;

import metrics.Header;
import metrics.Sample;
import metrics.io.MetricOutputStream;
import metrics.io.window.AbstractSampleWindow;
import metrics.io.window.SampleWindow;

import java.io.IOException;

/**
 * Created by anton on 5/7/16.
 */
public abstract class BatchLabellingAlgorithm extends WindowBatchAlgorithm {

    protected final SampleWindow window = new SampleWindow();

    @Override
    protected AbstractSampleWindow getWindow() {
        return window;
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        Header header = null;
        for (Sample sample : window.samples) {
            String label = newLabel(sample);
            if (sample.getHeader().hasChanged(header)) {
                header = new Header(sample.getHeader().header);
            }
            Sample clone = new Sample(header, sample.getMetrics(), sample);
            output.writeSample(clone);
        }
    }

    protected abstract String newLabel(Sample sample);

    @Override
    public String toString() {
        return "batch labelling algorithm";
    }

}
