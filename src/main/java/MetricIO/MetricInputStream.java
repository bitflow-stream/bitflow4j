package MetricIO;

import java.io.IOException;

/**
 * Created by mwall on 30.03.16.
 */
public interface MetricInputStream {

    MetricsSample readSample() throws IOException;

}
