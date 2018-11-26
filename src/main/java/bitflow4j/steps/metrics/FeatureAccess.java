package bitflow4j.steps.metrics;

import bitflow4j.Header;
import bitflow4j.Sample;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by alex on 10.11.17.
 */
public class FeatureAccess {
    //Attributes for simple metric access by name
    private final Map<String, Integer> indices = new HashMap<>();
    private String[] fields;
    private Header lastHeader = null;
    private double values[];

    //Attributes for accessing metrics by regex expression
    private Map<String, List<Pair<Integer, Integer>>> regexFieldRanges = new HashMap<>();

    public void updateHeader(Header header) {
        if (header.hasChanged(lastHeader)) {
            fields = header.header;
            indices.clear();
            for (int i = 0; i < header.header.length; i++) {
                indices.put(header.header[i], i);
            }
            lastHeader = header;

            //Reset field ranges used for fast filter access
            regexFieldRanges.clear();
        }
    }

    public String[] getFieldNames() {
        return fields;
    }

    public void setSample(Sample sample) {
        values = sample.getMetrics();
    }

    public double getFeature(String name) {
        Integer result = indices.get(name);
        if (result == null) {
            throw new IllegalArgumentException("No such metric: " + name);
        }
        return values[result];
    }

    public int getFeatureIndex(String name){
        Integer result = indices.get(name);
        if (result == null) {
            throw new IllegalArgumentException("No such metric: " + name);
        }
        return result;
    }

    /**
     * Return all metrics of the current sample.
     *
     * @return All metric values of the current sample.
     */
    public double[] getFeatures() {
        return values;
    }

    /**
     * Method returns all metric values which identifiers matches at least one of the regex filters.
     *
     * @param regex Filter regex expression which is used to filter the relevant metric which should be included.
     * @return All metrics which identifier matches at least one of the regex filter.
     */
    public double[] getFilteredFeatures(String... regex) {
        //Check for null
        if (regex == null) {
            throw new IllegalArgumentException("Regex parameters must not be null");
        }

        //Metrics which will be returned
        double[] metrics = new double[0];

        //Init regex indices for fast access
        for (String aRegex1 : regex) {
            if (aRegex1 == null) {
                throw new IllegalArgumentException("Regex parameters must not be null");
            }
            if (!regexFieldRanges.containsKey(aRegex1)) {
                initializeFieldRangeEntry(aRegex1);
            }
        }

        //Auxiliary variables
        int newMetricNumber;
        //Acquire the indices of all matches for the respective regex
        for (String aRegex : regex) {
            List<Pair<Integer, Integer>> ranges = regexFieldRanges.get(aRegex);
            //All ranges of the actual regex entry
            for (Pair<Integer, Integer> range : ranges) {
                //Amount of metrics which will be extracted
                newMetricNumber = range.getSecond() - range.getFirst();
                //Extend metric array which will be returned
                metrics = Arrays.copyOf(metrics, metrics.length + newMetricNumber);
                //Combine the current array range with the set of all metrics which were
                //extracted until this point
                System.arraycopy(
                        Arrays.copyOfRange(values, range.getFirst(), range.getSecond()),
                        0, metrics, metrics.length - newMetricNumber, newMetricNumber);
            }
        }

        return metrics;
    }

    private void initializeFieldRangeEntry(String regex) {
        //Compiled regex pattern
        Pattern pattern = Pattern.compile(regex);
        //Container for index range delimiter for the current regex
        //Usually do not expect more than one (still exceptions possible)
        List<Pair<Integer, Integer>> lstRegexIndices = new ArrayList<>(1);

        //Some auxiliary variables
        int start = -1, end = -1, i = 0;
        //Iterate over all header field and metric values
        for (; i < fields.length; i++) {
            if (pattern.matcher(fields[i]).matches() && start == -1) {
                start = i;
            } else if (!pattern.matcher(fields[i]).matches() && start != -1 && end == -1) {
                end = i;
            }
            if (start != -1 && end != -1) {
                lstRegexIndices.add(new Pair<>(start, end));
                start = end = -1;
            }
        }

        //Regex matches until the last field element --> previous loop wont set end index
        //--> this handles these cases.
        if (start != -1 && end == -1) {
            lstRegexIndices.add(new Pair<>(start, i));
        }

        //Init regex indices for efficient access
        regexFieldRanges.put(regex, lstRegexIndices);
    }
}
