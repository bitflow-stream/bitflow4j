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

    private final int milliSeconds;
    private final String tag;

    private String previousTag;

    public SleepOnTagChange(int milliSeconds, String tag) {
        this.milliSeconds = milliSeconds;
        this.tag = tag;
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        if (!sample.getTag(tag).equals(previousTag)) {
            // Sleep here, because the Tag changed
            try {
                Thread.sleep(milliSeconds);
            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Interrupted", e);
                Thread.currentThread().interrupt();
            }
            previousTag = sample.getTag(tag);
        }
        output.writeSample(sample);
    }
}
