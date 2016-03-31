package MetricIO;

/**
 *
 * @author fschmidt
 */
public interface MetricOutputStream {

    public void writeSample(MetricsSample data);
  
}
