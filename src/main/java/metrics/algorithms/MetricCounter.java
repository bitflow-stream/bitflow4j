package metrics.algorithms;

import metrics.Header;
import metrics.Sample;

import java.io.IOException;

/**
 * Created by anton on 4/8/16.
 * <p>
 * Create and output certain statistics over incoming samples and metrics.
 */
public class MetricCounter extends AbstractAlgorithm {

    private static final String[] headerFields = new String[]{
            "metrics", "samples this header", "metrics this header", "total samples", "total metrics", "headers"};

    private Header lastHeader = null;
    private Header outputHeader = null;

    private long samplesThisHeader = 0;
    private long metricsThisHeader = 0;
    private long totalSamples = 0;
    private long totalMetrics = 0;
    private long headers = 0;

    @Override
    public String toString() {
        return "metric statistics counter";
    }

    @Override
    public Sample executeSample(Sample sample) throws IOException {
        if (sample.headerChanged(lastHeader)) {
            lastHeader = sample.getHeader();
            outputHeader = new Header(headerFields, lastHeader);
            samplesThisHeader = 0;
            metricsThisHeader = 0;
            headers++;
        }

        int num = sample.getMetrics().length;
        samplesThisHeader++;
        totalSamples++;
        totalMetrics += num;
        metricsThisHeader += num;
        double[] values = new double[]{num, samplesThisHeader, metricsThisHeader, totalSamples, totalMetrics, headers};
        return new Sample(outputHeader, values, sample.getTimestamp());
    }

}
