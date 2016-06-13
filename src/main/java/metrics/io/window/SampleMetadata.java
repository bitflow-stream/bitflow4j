package metrics.io.window;

import metrics.Header;
import metrics.Sample;

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

    public SampleMetadata(Sample sample) {
        this(sample.getTimestamp(), sample.getSource(), sample.getLabel());
    }

    public Sample newSample(Header header, double values[]) {
        return new Sample(header, values, timestamp, source, label);
    }

}
