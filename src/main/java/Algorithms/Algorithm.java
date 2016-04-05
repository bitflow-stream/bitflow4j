package Algorithms;

import MetricIO.FileMetricInputStream;
import MetricIO.FileMetricOutputStream;

/**
 *
 * @author fschmidt
 */
public interface Algorithm {
    
    public void execute(FileMetricInputStream input, FileMetricOutputStream output);
    
}
