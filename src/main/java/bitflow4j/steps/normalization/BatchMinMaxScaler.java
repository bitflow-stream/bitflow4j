package bitflow4j.steps.normalization;

import bitflow4j.Sample;
import bitflow4j.steps.batch.BatchPipelineStep;
import bitflow4j.window.SampleWindow;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fschmidt
 */
public class BatchMinMaxScaler extends BatchPipelineStep {

    private final SampleWindow samples;
    private final Map<String, Double> min;
    private final Map<String, Double> max;

    public BatchMinMaxScaler() {
        samples = new SampleWindow();
        min = new HashMap<>();
        max = new HashMap<>();
    }

    @Override
    protected void flushAndClearResults() throws IOException {
        for (Sample sample : samples.samples) {
            double[] metrics = normalize(sample);
            Sample outSample = new Sample(sample.getHeader(), metrics, sample);
            output.writeSample(outSample);
        }
    }

    @Override
    protected void addSample(Sample sample) {
        samples.addSample(sample);
        updateMinMax(sample);
    }

    private void updateMinMax(Sample sample) {
        for (int i = 0; i < sample.getHeader().numFields(); i++) {
            if (!min.containsKey(sample.getHeader().header[i]) || !max.containsKey(sample.getHeader().header[i])) {
                min.put(sample.getHeader().header[i], sample.getMetrics()[i]);
                max.put(sample.getHeader().header[i], sample.getMetrics()[i]);
            } else {
                min.put(sample.getHeader().header[i], Math.min(min.get(sample.getHeader().header[i]), sample.getMetrics()[i]));
                max.put(sample.getHeader().header[i], Math.max(max.get(sample.getHeader().header[i]), sample.getMetrics()[i]));
            }
        }
    }

    private double[] normalize(Sample sample) {
        double[] normalizedMetrics = new double[sample.getMetrics().length];
        for (int i = 0; i < normalizedMetrics.length; i++) {
            double maxValue = max.get(sample.getHeader().header[i]);
            double minValue = min.get(sample.getHeader().header[i]);
            double range = maxValue - minValue;
            if (range == 0) {
                normalizedMetrics[i] = 0.5;
            } else {
                normalizedMetrics[i] = (sample.getMetrics()[i] - minValue) / range;
            }
        }
        return normalizedMetrics;
    }
}
