package metrics.algorithms;

import metrics.Sample;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by anton on 4/11/16.
 */
public class CorrelationAlgorithm extends PostAnalysisAlgorithm<PostAnalysisAlgorithm.Metric> {

    private SortedMap<String, Metric> metrics = new TreeMap<>();

    public CorrelationAlgorithm() {
        super("correlation algorithm");
    }

    @Override
    protected void analyseSample(Sample sample) throws IOException {
        registerSample(sample);
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        System.err.println(getName() + " processing " + samples.size() + " samples...");
    }

    @Override
    protected PostAnalysisAlgorithm.Metric createMetricStats(String name) {
        return new PostAnalysisAlgorithm.Metric(name);
    }

}
