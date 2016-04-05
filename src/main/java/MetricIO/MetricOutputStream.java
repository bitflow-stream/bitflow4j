package MetricIO;

import java.io.IOException;

/**
 *
 * @author fschmidt
 */
public interface MetricOutputStream {

    void writeSample(MetricsSample data) throws IOException;
  
}
