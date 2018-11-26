package bitflow4j.steps.misc;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;

import java.io.IOException;

/**
 * Created by anton on 4/8/16.
 * <p>
 * Create and output certain statistics over incoming samples and bitflow4j.
 */
public class MetricCounter extends AbstractPipelineStep {

    private static final String[] headerFields = new String[]{
            "bitflow4j", "samples this header", "bitflow4j this header", "total samples", "total bitflow4j", "headers"};

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
    public void writeSample(Sample sample) throws IOException {
        if (sample.headerChanged(lastHeader)) {
            lastHeader = sample.getHeader();
            outputHeader = new Header(headerFields);
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
        output.writeSample(new Sample(outputHeader, values, sample));
    }

}
