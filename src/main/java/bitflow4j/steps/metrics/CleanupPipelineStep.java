package bitflow4j.steps.metrics;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;

import java.io.IOException;

/**
 * This pipeline step can be used to clear additional Tags or Fields added by a previous pipeline step.
 */
public class CleanupPipelineStep extends AbstractPipelineStep {

    private final String[] metricPrefixesToRemove;
    private final String[] metricsToRemove;
    private final String[] tags;

    public CleanupPipelineStep(String[] tags, String[] metrics, String[] metricPrefixesToRemove) {
        this.tags = tags == null ? new String[0] : tags; // prevent NPE
        this.metricsToRemove = metrics == null ? new String[0] : metrics;
        this.metricPrefixesToRemove = metricPrefixesToRemove == null ? new String[0] : metricPrefixesToRemove;
    }

    @Override
    public synchronized void writeSample(Sample sample) throws IOException {
        for (String tagToRemove : this.tags) {
            sample.deleteTag(tagToRemove);
        }
        sample = sample.removeMetrics(metricsToRemove);
        sample = sample.removeMetricsWithPrefix(metricPrefixesToRemove);
        output.writeSample(sample);
    }

}
