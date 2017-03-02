package bitflow4j.sample;

import java.io.IOException;

/**
 * Created by anton on 14.02.17.
 */
public interface StoppableSampleSource extends SampleSource {

    void stop() throws IOException;

}
