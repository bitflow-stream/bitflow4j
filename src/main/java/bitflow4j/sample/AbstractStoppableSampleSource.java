package bitflow4j.sample;

/**
 * Created by anton on 14.02.17.
 */
public abstract class AbstractStoppableSampleSource extends AbstractSampleSource implements StoppableSampleSource {

    public void stop() {
        // Propagate the stop() call
        output().close();
    }

}
