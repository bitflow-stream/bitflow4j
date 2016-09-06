package metrics.algorithms.filter;

import metrics.Header;
import metrics.algorithms.WindowBatchAlgorithm;
import metrics.io.MetricOutputStream;
import metrics.io.window.AbstractSampleWindow;
import metrics.io.window.MetricStatisticsWindow;
import metrics.io.window.MultiHeaderWindow;
import metrics.io.window.SampleMetadata;
import metrics.main.misc.ParameterHash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter out metrics with a very low normalized standard deviation.
 * This only keeps metrics that exceed a minimum inside each given window.
 *
 * Created by anton on 4/7/16.
 */
public class VarianceFilterAlgorithm extends WindowBatchAlgorithm {

    private final double minNormalizedDeviation;

    private final MultiHeaderWindow<MetricStatisticsWindow> window =
            new MultiHeaderWindow<>(MetricStatisticsWindow.FACTORY);

    public VarianceFilterAlgorithm(double minNormalizedDeviation) {
        this.minNormalizedDeviation = minNormalizedDeviation;
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeDouble(minNormalizedDeviation);
    }

    @Override
    protected AbstractSampleWindow getWindow() {
        return window;
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        double avgDeviation = 0;

        // Filter out low-deviation metrics
        List<MetricStatisticsWindow> validStats = new ArrayList<>();

        for (MetricStatisticsWindow stats : window.allMetricWindows()) {
            double stdDeviation = stats.normalizedStdDeviation();
            if (stdDeviation > minNormalizedDeviation) {
                validStats.add(stats);
                avgDeviation += stdDeviation;
            }
        }
        if (validStats.isEmpty()) {
            System.err.println(toString() + " produced no output");
            return;
        }
        avgDeviation /= validStats.size();

        // Construct combined header
        String headerFields[] = new String[validStats.size()];
        for (int i = 0; i < headerFields.length; i++) {
            headerFields[i] = validStats.get(i).name;
        }
        Header header = new Header(headerFields);

        // Construct samples from remaining metrics
        for (int sampleNr = 0; sampleNr < window.numSamples(); sampleNr++) {
            double metrics[] = new double[headerFields.length];
            for (int metricNr = 0; metricNr < headerFields.length; metricNr++) {
                metrics[metricNr] = validStats.get(metricNr).getValue(sampleNr);
            }
            SampleMetadata meta = window.getSampleMetadata(sampleNr);
            output.writeSample(meta.newSample(header, metrics));
        }

        System.err.printf("%d of %d metrics passed stdDeviation filter (%d filtered out). Avg normalized variance: %f.\n",
                validStats.size(), window.numMetrics(), window.numMetrics() - validStats.size(), avgDeviation);
    }

}
