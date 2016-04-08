package metrics;

import java.util.Date;

/**
 * Created by mwall on 30.03.16.
 */
public class Sample {

    private final Date timestamp;
    private final String[] header;
    private final double[] metrics;

    public Sample(String[] header, Date timestamp, double[] metrics) {
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

    public double[] getMetrics() {
        return metrics;
    }

    public boolean headerChanged(String[] oldHeader) {
        if (oldHeader == null || header.length != oldHeader.length) {
            return true;
        } else if (header != oldHeader) {
            // New instance with same length: must compare all header fields. Rare case.
            for (int i = 0; i < header.length; i++) {
                if (!header[i].equals(oldHeader[i])) {
                    return true;
                }
            }
        }
        return false;
    }

}
