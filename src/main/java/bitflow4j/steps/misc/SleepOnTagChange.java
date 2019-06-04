package bitflow4j.steps.misc;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Sample;
import bitflow4j.script.registry.Description;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author kevinstyp
 */
@Description("Whenever the given onChangedTag-Tag changes, this step sleeps for the given time in ms.")
public class SleepOnTagChange extends AbstractPipelineStep {

    protected static final Logger logger = Logger.getLogger(SleepOnTagChange.class.getName());

    private final int time;
    private final String onChangedTag;

    private String previousTag;

    public SleepOnTagChange(int time, String onChangedTag) {
        this.time = time;
        this.onChangedTag = onChangedTag;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (!sample.getTag(onChangedTag).equals(previousTag)) {
            // Sleep here, because the Tag changed
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Interrupted", e);
                Thread.currentThread().interrupt();
            }
            previousTag = sample.getTag(onChangedTag);
        }
        output.writeSample(sample);
    }
}
