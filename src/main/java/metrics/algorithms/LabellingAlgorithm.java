package metrics.algorithms;

import metrics.Sample;
import metrics.algorithms.logback.MetricLog;
import metrics.algorithms.logback.PostAnalysisAlgorithm;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anton on 4/14/16.
 */
public abstract class LabellingAlgorithm extends PostAnalysisAlgorithm<MetricLog> {

    protected List<Sample> sampleLog = new ArrayList<>();

    public LabellingAlgorithm() {
        super(false);
    }

    @Override
    public void registerMetricData(Sample sample) throws IOException {
        registerSample(sample);
        sampleLog.add(sample);
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        Sample.Header header = null;
        for (Sample sample : sampleLog) {
            String label = newLabel(sample);
            if (sample.getHeader().hasChanged(header)) {
                header = new Sample.Header(sample.getHeader().header, Sample.Header.HEADER_LABEL_IDX + 1);
            }
            Sample clone = new Sample(header, sample.getMetrics(),
                    sample.getTimestamp(), sample.getSource(), label);
            output.writeSample(clone);
        }
        sampleLog.clear();
    }

    protected abstract String newLabel(Sample sample);

    @Override
    protected MetricLog createMetricStats(String name) {
        return new MetricLog(name);
    }

    @Override
    public String toString() {
        return "labelling algorithm";
    }

}
