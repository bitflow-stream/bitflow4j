package MetricIO;

import java.util.Date;

/**
 * Created by mwall on 30.03.16.
 */
public class MetricsSample {

    private String[] metricsHeader;
    private Double[] metrics;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    private Date timestamp;

    public String[] getMetricsHeader() {
        return metricsHeader;
    }

    public void setMetricsHeader(String[] metricsHeader) {

        this.metricsHeader = metricsHeader;
    }

    public Double[] getMetrics() {
        return metrics;
    }

    public void setMetrics(Double[] metrics) {

        this.metrics = metrics;
    }
    
    public MetricsSample aggregate(MetricsSample otherSample){
        MetricsSample aggregatedSample = new MetricsSample();
        String[] aggregatedMetricsHeader = new String[this.getMetricsHeader().length + otherSample.getMetricsHeader().length];
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}
