package bitflow4j.steps.batch;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.steps.metrics.MetricFilter;
import bitflow4j.window.SlidingMetricWindow;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fschmidt, Alex (15.05.2017)
 */
public abstract class SlidingWindowMetricPipelineStep extends AbstractPipelineStep {

    private Map<String, SlidingMetricWindow> windows = null;
    private final MetricFilter.MetricSelection filter;
    private final int windowSize;

    public SlidingWindowMetricPipelineStep(MetricFilter.MetricSelection filter, int windowSize) {
        this.filter = filter;
        if (windowSize <= 0) {
            throw new IllegalArgumentException("Window size must be > 0.");
        }
        this.windowSize = windowSize;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (windows == null) {
            initializeWindows(sample.getHeader());
        }
        addToWindow(sample);
        Map<String, Double> results = calculateNewMetrics(sample);
        double[] values = new double[results.size()];
        String[] fields = new String[results.size()];
        int i = 0;
        for (Map.Entry<String, Double> entry : results.entrySet()) {
            values[i] = entry.getValue();
            fields[i++] = entry.getKey();
        }
        sample = sample.extend(fields, values);
        super.writeSample(sample);
    }

    protected abstract Map<String, Double> calculateNewMetrics(Sample sample);

    private void initializeWindows(Header header) {
        windows = new HashMap<>();
        for (int i = 0; i < header.numFields(); i++) {
            String metricName = header.header[i];
            if (filter == null || filter.shouldInclude(metricName)) {
                windows.put(metricName, new SlidingMetricWindow(metricName, windowSize));
            }
        }
    }

    private void addToWindow(Sample sample) {
        for (int i = 0; i < sample.getHeader().numFields(); i++) {
            String metricName = sample.getHeader().header[i];
            windows.get(metricName).add(sample.getMetrics()[i]);
        }
    }

    protected Map<String, SlidingMetricWindow> getWindows() {
        return windows;
    }
}
