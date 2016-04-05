package MetricIO;

import java.io.IOException;

/**
 *
 * @author fschmidt
 */
public class TcpMetricOutputStream implements MetricOutputStream{

    @Override
    public void writeSample(MetricsSample data) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
