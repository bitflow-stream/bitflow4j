package MetricIO;

import java.util.Date;

/**
 * Created by mwall on 30.03.16.
 */
public class MetricsSample {

    private final Date timestamp;
    private final String[] header;
    private final Double[] metrics;

    public MetricsSample(String[] header, Date timestamp, Double[] metrics) {
        this.header = header;
        this.timestamp = timestamp;
        this.metrics = metrics;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String[] getHeader() {
        return header;
    }

    public Double[] getMetrics() {
        return metrics;
    }

    public MetricsSample aggregate(MetricsSample otherSample){
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
