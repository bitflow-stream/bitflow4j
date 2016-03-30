package Marshaller;

import MetricIO.MetricsSample;

/**
 * Created by mwall on 30.03.16.
 */
public class CsvMarshaller implements Marshaller_Interface{


    public MetricsSample marshallSample(String header, String metrics) {
        MetricsSample sample = new MetricsSample();

        sample.setMetricsHeader(header.split(",",-1));
        float[] metricsFloat = new float[sample.getMetricsHeader().length];
        String[] metricsStr = metrics.split(",",-1);

        for ( int i = 0; i < sample.getMetricsHeader().length; i++){
            metricsFloat[i] = Float.parseFloat(metricsStr[i]);
        }

        sample.setMetrics(metricsFloat);
        return sample;
    }

    public void unmarshallSample(MetricsSample metricsSample){


    };
}
