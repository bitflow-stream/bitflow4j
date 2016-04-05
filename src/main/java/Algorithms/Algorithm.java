package Algorithms;

import MetricIO.FileMetricInputStream;
import MetricIO.FileMetricOutputStream;

/**
 *
 * @author fschmidt
 */
public interface Algorithm {
    
    void execute(FileMetricInputStream input, FileMetricOutputStream output);
    
}
