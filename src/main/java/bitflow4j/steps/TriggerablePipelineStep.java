package bitflow4j.steps;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.steps.metrics.FeatureAccess;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by alex on 10.11.17.
 */
public abstract class TriggerablePipelineStep extends AbstractPipelineStep {

    public interface Trigger{
        boolean isTrigger(Sample sample, FeatureAccess access);
    }

    protected FeatureAccess access;
    protected Trigger trigger;

    int counterIn = 0;
    int counterOut = 0;

    public TriggerablePipelineStep(Trigger trigger){
        this.trigger = trigger;
        this.access = new FeatureAccess();
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        this.updateAccess(sample);

        counterIn++;

        List<Sample> samples;
        if(trigger.isTrigger(sample, access) || isInTriggeredMode()) {
            samples = this.handleTrigger(sample);
        }else {
            samples = Arrays.asList(sample);
        }

        if(samples != null) {
            for (Sample s : samples)
                if (s != null) {
                    super.writeSample(s);
                    counterOut++;
                }
        }
    }

    protected void updateAccess(Sample sample){
        access.updateHeader(sample.getHeader());
        access.setSample(sample);
    }

    abstract protected List<Sample> handleTrigger(Sample sample);
    abstract  protected boolean isInTriggeredMode();

    public static class MetricValueTrigger implements Trigger{

        private String metricName;
        private double triggerValue;

        public MetricValueTrigger(String metricName, double triggerValue) {
            this.metricName = metricName;
            this.triggerValue = triggerValue;
        }

        @Override
        public boolean isTrigger(Sample sample, FeatureAccess access){
            return access.getFeature(metricName) == triggerValue;
        }
    }

    public static class TagValueTrigger implements Trigger{

        private String tagName;
        private String tagValue;

        public TagValueTrigger(String tagName, String tagValue) {
            this.tagName = tagName;
            this.tagValue = tagValue;
        }

        @Override
        public boolean isTrigger(Sample sample, FeatureAccess access){
            return !(sample.getTag(tagName) == null) && sample.getTag(tagName).equals(tagValue);
        }
    }
}
