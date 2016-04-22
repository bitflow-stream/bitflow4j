package metrics.algorithms.logback;

import java.util.Date;

/**
 * Created by anton on 4/21/16.
 */
public class SampleMetadata {
    public final Date timestamp;
    public final String source;
    public final String label;

    public SampleMetadata(Date timestamp, String source, String label) {
        this.source = source;
        this.label = label;
        this.timestamp = timestamp;
    }
}
