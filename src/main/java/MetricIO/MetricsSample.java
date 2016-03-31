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
}
