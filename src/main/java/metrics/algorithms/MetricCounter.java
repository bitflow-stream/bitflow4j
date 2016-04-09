package metrics.algorithms;

import metrics.Sample;

import java.io.IOException;

/**
 * Created by anton on 4/8/16.
 */
public class MetricCounter extends GenericAlgorithm {

    private static final String[] header = new String[] {
                "metrics", "samples this header", "metrics this header", "total samples", "total metrics", "headers" };

    private String[] lastHeader = null;
    private long samplesThisHeader = 0;
    private long metricsThisHeader = 0;
    private long totalSamples = 0;
    private long totalMetrics = 0;
    private long headers = 0;

    public MetricCounter() {
        super("metric counter");
    }

    @Override
    public Sample executeSample(Sample sample) throws IOException {
        if (sample.headerChanged(lastHeader)) {
            lastHeader = sample.getHeader();
            samplesThisHeader = 0;
            metricsThisHeader = 0;
            headers++;
        }

        int num = sample.getMetrics().length;
        samplesThisHeader++;
        totalSamples++;
        totalMetrics += num;
        metricsThisHeader += num;
        double[] values = new double[] { num, samplesThisHeader, metricsThisHeader, totalSamples, totalMetrics, headers };
        return new Sample(header, sample.getTimestamp(), values);
    }

}
