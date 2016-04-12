package metrics.algorithms;

import metrics.io.MetricOutputStream;

import java.io.IOException;

/**
 * Created by anton on 4/12/16.
 *
 * This analyses the output of CorrelationAlgorithm and produces a summary.
 */
public class CorrelationSignificanceAlgorithm extends PostAnalysisAlgorithm<CorrelationAlgorithm.MetricLog> {

    private final double minCorrelationSignificance;

    public CorrelationSignificanceAlgorithm(double minCorrelationSignificance) {
        super(true);
        this.minCorrelationSignificance = minCorrelationSignificance;
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        // TODO
    }

    @Override
    public String toString() {
        return "correlation post-analysis [" + minCorrelationSignificance + "]";
    }

    @Override
    protected CorrelationAlgorithm.MetricLog createMetricStats(String name) {
        return new CorrelationAlgorithm.MetricLog(name);
    }

}
