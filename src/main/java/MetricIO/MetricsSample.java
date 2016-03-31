package MetricIO;

import java.util.Date;

/**
 * Created by mwall on 30.03.16.
 */
public class MetricsSample {

    private String[] metricsHeader;
    private float[] metrics;

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

    public float[] getMetrics() {
        return metrics;
    }

    public void setMetrics(float[] metrics) {
        this.metrics = metrics;
    }
}
