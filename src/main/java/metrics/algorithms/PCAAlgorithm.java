package metrics.algorithms;

import metrics.io.MetricOutputStream;

import java.io.IOException;

/**
 * Created by anton on 4/13/16.
 */
public class PCAAlgorithm extends PostAnalysisAlgorithm<CorrelationAlgorithm.MetricLog> {

    public PCAAlgorithm() {
        super(true);
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        // TODO
    }

    @Override
    protected CorrelationAlgorithm.MetricLog createMetricStats(String name) {
        return new CorrelationAlgorithm.MetricLog(name);
    }

    @Override
    public String toString() {
        return "pca algorithm";
    }

}
