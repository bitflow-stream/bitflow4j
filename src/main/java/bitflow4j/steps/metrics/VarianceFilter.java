package bitflow4j.steps.metrics;

import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.misc.OnlineStatistics;
import bitflow4j.steps.BatchPipelineStep;

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
public class VarianceFilter extends BatchPipelineStep {

    private static final Logger logger = Logger.getLogger(VarianceFilter.class.getName());

    private final double minNormalizedDeviation;

    public VarianceFilter(double minNormalizedDeviation) {
        this.minNormalizedDeviation = minNormalizedDeviation;
    }

    @Override
    protected void flush(List<Sample> window) throws IOException {
        double avgDeviation = 0;

        Header inHeader = window.get(0).getHeader();
        int numFields = inHeader.numFields();
        List<OnlineStatistics> fieldStats = new ArrayList<>(numFields);
        for (int i = 0; i < numFields; i++) {
            fieldStats.set(i, OnlineStatistics.buildFrom(window, i));
        }

        List<OnlineStatistics> validStats = new ArrayList<>(numFields);
        List<Integer> validMetricIndices = new ArrayList<>(numFields);
        List<String> validHeaderFields = new ArrayList<>(numFields);
        for (int i = 0; i < fieldStats.size(); i++) {
            OnlineStatistics stats = fieldStats.get(i);
            double stdDeviation = stats.standardDeviation();
            double mean = stats.mean();
            double normalizedStddev = Math.abs(mean == 0 ? stdDeviation : stdDeviation / mean);
            if (normalizedStddev > minNormalizedDeviation) {
                validStats.add(stats);
                avgDeviation += normalizedStddev;
                validMetricIndices.add(i);
                validHeaderFields.add(inHeader.header[i]);
            }
        }
        if (validStats.isEmpty()) {
            logger.warning(toString() + " produced no output");
            return;
        }
        avgDeviation /= validStats.size();

        // Construct combined header
        Header outHeader = new Header(validHeaderFields.toArray(new String[0]));

        // Construct samples from remaining metrics
        for (Sample sample : window) {
            double[] metrics = new double[outHeader.header.length];
            for (int i = 0; i < metrics.length; i++) {
                metrics[i] = sample.getValue(validMetricIndices.get(i));
            }
            output.writeSample(new Sample(outHeader, metrics, sample));
        }

        logger.info(validStats.size() + " of " + numFields +
                " metrics passed stdDeviation filter (" + (numFields - validStats.size()) +
                " filtered out). Avg normalized variance: " + avgDeviation);
    }

}