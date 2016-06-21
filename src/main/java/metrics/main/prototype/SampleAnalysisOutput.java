package metrics.main.prototype;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.AbstractAlgorithm;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Remove the sample data, only keep timestamp and tags.
 * Add host tag.
 * Turn the 'cls' into multiple features indicating through values 0 or 1 which class was selected.
 *
 * Created by anton on 6/9/16.
 */
public class SampleAnalysisOutput extends AbstractAlgorithm {

    public static final String TAG_HOSTNAME = "host";

    private final Set<String> allClasses;
    private final String hostname;
    private final Header header;
    Map<String, Integer> fieldIndices;

    public SampleAnalysisOutput(Set<String> allClasses, String hostname) {
        this.allClasses = allClasses;
        this.hostname = hostname;

        String fields[] = allClasses.toArray(new String[allClasses.size()]);
        header = new Header(fields, true);
        fieldIndices = new HashMap<>();
        for (int i = 0; i < fields.length; i++) {
            fieldIndices.put(fields[i], i);
        }
    }

    protected Sample executeSample(Sample sample) throws IOException {
        Map<String, String> tags = sample.getTags();
        String cls = tags.get(Sample.TAG_LABEL);
        Integer tagFieldObj = fieldIndices.get(cls);
        int tagField;
        if (tagFieldObj == null) {
            System.err.println("Warning: Failed to find field index for cls=" + cls + " in " + Arrays.toString(header.header));
            tagField = -1;
        } else {
            tagField = tagFieldObj;
        }

        double[] values = new double[header.header.length];
        for (int i = 0; i < values.length; i++) {
            if (i == tagField) {
                values[i] = 1;
            } else {
                values[i] = 0;
            }
        }
        tags.put(TAG_HOSTNAME, hostname);
        // TODO maybe delete TAG_LABEL, since already encoded in values?
        return new Sample(header, values, sample.getTimestamp(), tags);
    }

    @Override
    public String toString() {
        return "sample clearer";
    }
}
