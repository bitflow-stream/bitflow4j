package bitflow4j.steps.batch;

import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.steps.metrics.MetricFilter;
import bitflow4j.window.AbstractSampleWindow;
import bitflow4j.window.MetricWindow;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fschmidt
 */
public abstract class FilteringWindowBatchPipelineStep extends WindowBatchPipelineStep {

    private Map<String, MetricWindow> windows = null;
    private final MetricFilter.MetricSelection filter;

    public FilteringWindowBatchPipelineStep(MetricFilter.MetricSelection filter) {
        this.filter = filter;
    }

    @Override
    protected void addSample(Sample sample) {
        if (windows == null) {
            initializeWindows(sample.getHeader());
        }
        addToWindow(sample);
    }

    private void initializeWindows(Header header) {
        windows = new HashMap<>();
        for (int i = 0; i < header.numFields(); i++) {
            String metricName = header.header[i];
            if (filter == null || filter.shouldInclude(metricName)) {
                windows.put(metricName, new MetricWindow(metricName));
            }
        }
    }

    private void addToWindow(Sample sample) {
        for (int i = 0; i < sample.getHeader().numFields(); i++) {
            String metricName = sample.getHeader().header[i];
            windows.get(metricName).add(sample.getMetrics()[i]);
        }
    }

    public Map<String, MetricWindow> getWindows() {
        return windows;
    }

    @Override
    protected AbstractSampleWindow getWindow() {
        throw new UnsupportedOperationException("Use getWindows() instead.");
    }

}
