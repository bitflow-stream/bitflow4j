package metrics.io;

import metrics.Sample;

import java.io.IOException;

/**
 * Created by mwall on 30.03.16.
 */
public interface MetricInputStream {

    Sample readSample() throws IOException;

}
