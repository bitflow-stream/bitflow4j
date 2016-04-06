package Algorithms;

import MetricIO.MetricInputStream;
import MetricIO.MetricOutputStream;

/**
 *
 * @author fschmidt
 */
public interface Algorithm {
    
    void execute(MetricInputStream input, MetricOutputStream output);
    
}
