package MetricIO;

import Metrics.Sample;

import java.io.IOException;

/**
 *
 * @author fschmidt
 */
public class TcpMetricOutputStream implements MetricOutputStream{

    @Override
    public void writeSample(Sample data) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
