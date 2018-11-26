package bitflow4j.window;

import bitflow4j.Header;
import bitflow4j.Sample;

import java.util.Date;
import java.util.Map;

/**
 * Created by anton on 4/21/16.
 */
public class SampleMetadata {

    public final Date timestamp;
    public final String source;
    public final String label;
    public final Map<String, String> tags;

    public SampleMetadata(Date timestamp, Map<String, String> tags) {
        this.source = tags.get(Sample.TAG_SOURCE);
        this.label = tags.get(Sample.TAG_LABEL);
        this.timestamp = timestamp;
        this.tags = tags;
    }

    public SampleMetadata(Sample sample) {
        this(sample.getTimestamp(), sample.getTags());
    }

    public Sample newSample(Header header, double values[]) {
        return new Sample(header, values, timestamp, tags);
    }

}
