package metrics.algorithms;

import metrics.Header;
import metrics.Sample;
import metrics.algorithms.classification.OnlineNormalEstimator;

import java.util.HashMap;
import java.util.Map;

/**
 * Map incoming values to a spcified header. If values are missing, fill them up with defaults.
 *
 * Created by anton on 6/21/16.
 */
public class SampleConverger {

    public static final int REPORT_FILLED_UP_VALUES = 20;

    private Header expectedHeader = null;
    private OnlineNormalEstimator filledUpValues = new OnlineNormalEstimator();

    public SampleConverger() {
        this(null);
    }

    public SampleConverger(Header expectedHeader) {
        this.expectedHeader = expectedHeader;
    }

    public Header getExpectedHeader() {
        return expectedHeader;
    }

    public double[] getValues(Sample sample) {
        Header incomingHeader = sample.getHeader();
        double[] values = sample.getMetrics();

        if (expectedHeader == null) {
            // The first encountered header will be used as reference for the future.
            expectedHeader = incomingHeader;
            return values;
        }

        if (expectedHeader.hasTags != incomingHeader.hasTags) {
            expectedHeader = new Header(expectedHeader.header, incomingHeader);
        }
        if (incomingHeader.hasChanged(expectedHeader)) {

            // === Different strategies can be chosen to converge the incoming
            // === feature vector to the expected one.
            // values = optimisticConvergeValues(incomingHeader, values);
            values = mappedConvergeValues(incomingHeader, values);

            if (filledUpValues.numSamples() % REPORT_FILLED_UP_VALUES == 0) {
                System.err.println("Number of unavailable metrics: " + filledUpValues);
            }
        } else if (expectedHeader != incomingHeader) {
            // Make next hasChanged faster.
            expectedHeader = incomingHeader;
        }
        return values;
    }

    private double[] optimisticConvergeValues(Header incomingHeader, double[] incomingValues) {
        double result[] = new double[expectedHeader.header.length];
        int incoming = 0;
        int numFakeValues = 0;
        for (int i = 0; i < result.length; i++) {
            // Expect same order of header fields, but incoming can have some additional fields.
            while (incoming < incomingHeader.header.length && !incomingHeader.header[incoming].equals(expectedHeader.header[i])) {
                incoming++;
            }
            if (incoming < incomingHeader.header.length) {
                result[i] = incomingValues[incoming];
                incoming++;
            } else {
                result[i] = 0;
                numFakeValues++;
            }
        }
        filledUpValues.handle((double) numFakeValues / (double) incomingValues.length);
        return result;
    }

    private double[] mappedConvergeValues(Header incomingHeader, double[] incomingValues) {
        Map<String, Double> incomingMap = new HashMap<>();
        for (int i = 0; i < incomingValues.length; i++) {
            incomingMap.put(incomingHeader.header[i], incomingValues[i]);
        }

        double result[] = new double[expectedHeader.header.length];
        int numFakeValues = 0;
        for (int i = 0; i < result.length; i++) {
            Double incoming = incomingMap.get(expectedHeader.header[i]);
            if (incoming == null) {
                result[i] = 0;
                numFakeValues++;
            } else {
                result[i] = incoming;
            }
        }
        filledUpValues.handle((double) numFakeValues / (double) incomingValues.length);
        return result;
    }

}
