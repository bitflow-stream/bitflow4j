package bitflow4j.steps.normalization;

import bitflow4j.Sample;
import bitflow4j.misc.MetricScaler;
import bitflow4j.steps.BatchHandler;

import java.io.IOException;
import java.util.List;

/**
 * @author fschmidt
 */
public abstract class BatchFeatureScaler implements BatchHandler {

    @Override
    public List<Sample> handleBatch(List<Sample> window) throws IOException {
        int numFields = window.get(0).getHeader().numFields();
        MetricScaler[] scalers = new MetricScaler[numFields];

        for (int i = 0; i < numFields; i++) {
            scalers[i] = createScaler(window, i);
        }

        for (Sample sample : window) {
            for (int i = 0; i < numFields; i++) {
                sample.getMetrics()[i] = scalers[i].scale(sample.getValue(i));
            }
        }
        return window;
    }

    protected abstract MetricScaler createScaler(List<Sample> sample, int metricIndex);

    public static class MinMax extends BatchFeatureScaler {
        @Override
        protected MetricScaler createScaler(List<Sample> sample, int metricIndex) {
            return new MetricScaler.MinMaxScaler(sample, metricIndex);
        }
    }

    public static class Standardize extends BatchFeatureScaler {
        @Override
        protected MetricScaler createScaler(List<Sample> sample, int metricIndex) {
            return new MetricScaler.MetricStandardizer(sample, metricIndex);
        }
    }

}
