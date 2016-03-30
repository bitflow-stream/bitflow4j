package Marshaller;

import MetricIO.MetricsSample;

/**
 * Created by mwall on 30.03.16.
 */
public interface Marshaller_Interface {

    public MetricsSample marshallSample(String header, String metrics);

    public void unmarshallSample(MetricsSample metricsSample);
}

