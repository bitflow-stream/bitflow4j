package bitflow4j.io;

import bitflow4j.Sample;

import java.io.IOException;

/**
 * Basic interface for reading a stream of Samples.
 * <p>
 * Created by mwall on 30.03.16.
 */
public interface MetricInputStream {

    Sample readSample() throws IOException;

}
