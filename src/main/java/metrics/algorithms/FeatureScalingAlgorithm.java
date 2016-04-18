package metrics.algorithms;

import metrics.Sample;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by anton on 4/18/16.
 */
public class FeatureScalingAlgorithm extends PostAnalysisAlgorithm<PostAnalysisAlgorithm.MetricStatistics> {

    public FeatureScalingAlgorithm() {
        super(true);
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        Collection<MetricStatistics> stats = metrics.values();
        double[] stdDeviations = new double[stats.size()];
        double[] averages = new double[stats.size()];

        int i = 0;
        for (MetricStatistics stat : stats) {
            averages[i] = stat.average();
            double stdDev = stat.stdDeviation();
            if (stdDev == 0) stdDev = 1;
            stdDeviations[i++] = stdDev;
        }

        // Construct combined header
        String headerFields[] = new String[stats.size()];
        i = 0;
        for (MetricStatistics stat : stats) {
            headerFields[i++] = stat.name;
        }
        Sample.Header header = new Sample.Header(headerFields, Sample.Header.TOTAL_SPECIAL_FIELDS);

        // Construct samples
        for (int sampleNr = 0; sampleNr < samples.size(); sampleNr++) {
            double metrics[] = new double[headerFields.length];
            i = 0;
            for (MetricStatistics stat : stats) {
                double val = stat.getValue(sampleNr);

                // The actual scaling calculation
                double scaled = (val - averages[i]) / stdDeviations[i];

                metrics[i++] = scaled;
            }
            SampleMetadata meta = samples.get(sampleNr);
            Sample sample = new Sample(header, metrics, meta.timestamp, meta.source, meta.label);
            output.writeSample(sample);
        }
    }

    @Override
    protected MetricStatistics createMetricStats(String name) {
        return new MetricStatistics(name);
    }

    @Override
    public String toString() {
        return "feature scaling";
    }

}
