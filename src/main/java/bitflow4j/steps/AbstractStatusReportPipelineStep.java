package bitflow4j.steps;

import bitflow4j.sample.Sample;

import java.io.IOException;
import java.util.List;

/**
 * Created by alex on 19.03.18.
 */
public abstract class AbstractStatusReportPipelineStep extends AbstractPipelineStep {

    public static final String STATUS_SAMPLE_LABEL = "status_sample";

    private long sampleCounter;
    private int statusFrequency;

    public AbstractStatusReportPipelineStep(){
        this.statusFrequency = -1;
    }

    public AbstractStatusReportPipelineStep(int statusFrequency){
        this.statusFrequency = statusFrequency;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        sampleCounter++;
        if(statusFrequency <= 0){
            super.writeSample(sample);
            return;
        } else if(sampleCounter % statusFrequency == 0) {
            this.writeStatusSamples(this.createStatusSamples());
        }
        super.writeSample(sample);
    }

    protected void writeStatusSamples(List<Sample> samples) throws IOException {
        if(samples != null) {
            for (Sample s : samples) {
                if(s != null) {
                    s.setLabel(STATUS_SAMPLE_LABEL);
                    super.writeSample(s);
                }
            }
        }
    }

    public abstract List<Sample> createStatusSamples();
}
