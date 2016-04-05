package MetricIO;

import Metrics.Sample;

import java.io.IOException;

/**
 *
 * @author fschmidt
 */
public interface MetricOutputStream {

    void writeSample(Sample data) throws IOException;
  
}
