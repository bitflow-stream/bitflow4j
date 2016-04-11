package metrics.algorithms;

import metrics.Sample;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anton on 4/7/16.
 */
public class StdDeviationFilterAlgorithm extends PostAnalysisAlgorithm<PostAnalysisAlgorithm.MetricStatistics> {

    private final double minNormalizedDeviation;

    public StdDeviationFilterAlgorithm(double minNormalizedDeviation) {
        super("stdDeviation-filter algorithm");
        this.minNormalizedDeviation = minNormalizedDeviation;
    }

    @Override
    protected MetricStatistics createMetricStats(String name) {
        return new MetricStatistics(name);
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        double avgDeviation = 0;

        // Filter out low-deviation metrics
        List<MetricStatistics> validStats = new ArrayList<>();

        for (Map.Entry<String, MetricStatistics> metric : metrics.entrySet()) {
            MetricStatistics stats = metric.getValue();
            double stdDeviation = stats.normalizedStdDeviation();
            if (stdDeviation > minNormalizedDeviation) {
                validStats.add(stats);
                avgDeviation += stdDeviation;
            }
        }
        if (validStats.isEmpty()) {
            System.err.println(getName() + " produced no output");
            return;
        }
        avgDeviation /= validStats.size();

        // Construct combined header
        String headerFields[] = new String[validStats.size()];
        for (int i = 0; i < headerFields.length; i++) {
            headerFields[i] = validStats.get(i).name;
        }
        Sample.Header header = new Sample.Header(headerFields, Sample.Header.TOTAL_SPECIAL_FIELDS);

        // Construct samples from remaining metrics
        for (int sampleNr = 0; sampleNr < samples.size(); sampleNr++) {
            double metrics[] = new double[headerFields.length];
            for (int metricNr = 0; metricNr < headerFields.length; metricNr++) {
                metrics[metricNr] = validStats.get(metricNr).getValue(sampleNr);
            }
            SampleMetadata meta = samples.get(sampleNr);
            Sample sample = new Sample(header, metrics, meta.timestamp, meta.source, meta.label);
            output.writeSample(sample);
        }

        System.err.printf("%d of %d metrics passed stdDeviation filter (%d filtered out). Avg normalized deviation: %f.\n",
                validStats.size(), metrics.size(), metrics.size() - validStats.size(), avgDeviation);
    }

}
