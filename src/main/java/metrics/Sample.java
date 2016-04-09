package metrics;

import java.io.IOException;
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

    public void checkConsistency() throws IOException {
        if (timestamp == null)
            throw new IOException("Sample.timestamp is null");
        if (header == null)
            throw new IOException("Sample.header is null");
        if (metrics == null)
            throw new IOException("Sample.metrics is null");
        if (header.length != metrics.length)
            throw new IOException("Sample.header is size " + header.length +
                    ", but Sample.metrics is size " + metrics.length);
    }

}
