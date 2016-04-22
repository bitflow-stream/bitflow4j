package metrics.algorithms;

import metrics.Sample;
import metrics.algorithms.logback.ExtendedMetricsStats;
import metrics.algorithms.logback.MetricStatistics;
import metrics.algorithms.logback.PostAnalysisAlgorithm;
import metrics.algorithms.logback.SampleMetadata;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by anton on 4/18/16.
 */
public abstract class AbstractFeatureScaler extends PostAnalysisAlgorithm<ExtendedMetricsStats> {

    public AbstractFeatureScaler() {
        super(true);
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        Collection<ExtendedMetricsStats> stats = metrics.values();
        MetricScaler scalers[] = new MetricScaler[stats.size()];

        int i = 0;
        for (ExtendedMetricsStats stat : stats) {
            scalers[i++] = createScaler(stat);
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
                double scaled = scalers[i].scale(val);
                metrics[i++] = scaled;
            }
            SampleMetadata meta = samples.get(sampleNr);
            Sample sample = new Sample(header, metrics, meta.timestamp, meta.source, meta.label);
            output.writeSample(sample);
        }
    }

    protected abstract MetricScaler createScaler(ExtendedMetricsStats stats);

    interface MetricScaler {
        double scale(double val);
    }

    @Override
    protected ExtendedMetricsStats createMetricStats(String name) {
        return new ExtendedMetricsStats(name);
    }

}
