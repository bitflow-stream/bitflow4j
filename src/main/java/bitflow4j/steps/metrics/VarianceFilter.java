package bitflow4j.steps.metrics;

import bitflow4j.Header;
import bitflow4j.steps.batch.WindowBatchPipelineStep;
import bitflow4j.window.AbstractSampleWindow;
import bitflow4j.window.MetricStatisticsWindow;
import bitflow4j.window.MultiHeaderWindow;
import bitflow4j.window.SampleMetadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Filter out metrics with a very low normalized standard deviation.
 * This only keeps metrics that exceed a minimum inside each given window.
 * <p>
 * Created by anton on 4/7/16.
 */
public class VarianceFilter extends WindowBatchPipelineStep {

    private static final Logger logger = Logger.getLogger(VarianceFilter.class.getName());

    private final double minNormalizedDeviation;

    private final MultiHeaderWindow<MetricStatisticsWindow> window =
            new MultiHeaderWindow<>(MetricStatisticsWindow.FACTORY);

    public VarianceFilter(double minNormalizedDeviation) {
        this.minNormalizedDeviation = minNormalizedDeviation;
    }

    @Override
    protected AbstractSampleWindow getWindow() {
        return window;
    }

    @Override
    protected void flushResults() throws IOException {
        double avgDeviation = 0;

        // Filter out low-deviation bitflow4j
        List<MetricStatisticsWindow> validStats = new ArrayList<>();

        for (MetricStatisticsWindow stats : window.allMetricWindows()) {
            double stdDeviation = stats.normalizedStdDeviation();
            if (stdDeviation > minNormalizedDeviation) {
                validStats.add(stats);
                avgDeviation += stdDeviation;
            }
        }
        if (validStats.isEmpty()) {
            logger.warning(toString() + " produced no output");
            return;
        }
        avgDeviation /= validStats.size();

        // Construct combined header
        String headerFields[] = new String[validStats.size()];
        for (int i = 0; i < headerFields.length; i++) {
            headerFields[i] = validStats.get(i).name;
        }
        Header header = new Header(headerFields);

        // Construct samples from remaining bitflow4j
        for (int sampleNr = 0; sampleNr < window.numSamples(); sampleNr++) {
            double metrics[] = new double[headerFields.length];
            for (int metricNr = 0; metricNr < headerFields.length; metricNr++) {
                metrics[metricNr] = validStats.get(metricNr).getValue(sampleNr);
            }
            SampleMetadata meta = window.getSampleMetadata(sampleNr);
            output.writeSample(meta.newSample(header, metrics));
        }

        logger.info(validStats.size() + " of " + window.numMetrics() +
                " bitflow4j passed stdDeviation filter (" + (window.numMetrics() - validStats.size()) +
                " filtered out). Avg normalized variance: " + avgDeviation);
    }

}