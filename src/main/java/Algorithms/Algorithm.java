package Algorithms;

import MetricIO.FileMetricInputStream;
import MetricIO.FileMetricOutputStream;

/**
 *
 * @author fschmidt
 */
public interface Algorithm {
    
    public FileMetricOutputStream execute(FileMetricInputStream dataInputStream);
    
}
