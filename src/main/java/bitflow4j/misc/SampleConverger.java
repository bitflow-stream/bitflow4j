package bitflow4j.misc;

import bitflow4j.Header;
import bitflow4j.Sample;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Map incoming values to a specified header.
 * Metrics not existing in the desired header are stripped, missing values are filled up with defaults.
 * If an instance is initialized without a given header, then the header of the FIRST incoming Sample
 * is stored and all subsequent Samples are changed to fit into it.
 * Use with care: this pipeline step hides changes in headers and can cause data corruption to go unnoticed.
 * The purpose of this is to avoid errors at any cost, if the system should keep running even when changing
 * headers are expected.
 * <p>
 * Created by anton on 6/21/16.
 */
public class SampleConverger {

    private static final Logger logger = Logger.getLogger(SampleConverger.class.getName());

    public static final int REPORT_FILLED_UP_VALUES = 20;

    private Header expectedHeader;
    private OnlineStatistics filledUpValues = new OnlineStatistics();

    public SampleConverger() {
        this(null);
    }

    public SampleConverger(Header expectedHeader) {
        this.expectedHeader = expectedHeader;
    }

    public Header getExpectedHeader() {
        return expectedHeader;
    }

    public Sample getExpectedHeaderSample(Sample sample) {
        return new Sample(expectedHeader, this.getValues(sample), sample.getTimestamp(), sample.getTags());
    }

    public double[] getValues(Sample sample) {
        Header incomingHeader = sample.getHeader();
        double[] values = sample.getMetrics();

        if (expectedHeader == null) {
            // The first encountered header will be used as reference for the future.
            expectedHeader = incomingHeader;
            return values;
        }

        if (incomingHeader.hasChanged(expectedHeader)) {

            // === Different strategies can be chosen to converge the incoming
            // === feature vector to the expected one.
            // values = optimisticConvergeValues(incomingHeader, values);
            values = mappedConvergeValues(incomingHeader, values);

            if (filledUpValues.numSamples() % REPORT_FILLED_UP_VALUES == 0) {
                logger.warning("Number of unavailable bitflow4j: " + filledUpValues);
            }
        } else if (expectedHeader != incomingHeader) {
            // Make next hasChanged faster.
            expectedHeader = incomingHeader;
        }
        return values;
    }

    private double[] optimisticConvergeValues(Header incomingHeader, double[] incomingValues) {
        double[] result = new double[expectedHeader.header.length];
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
        filledUpValues.push((double) numFakeValues / (double) incomingValues.length);
        return result;
    }

    private double[] mappedConvergeValues(Header incomingHeader, double[] incomingValues) {
        Map<String, Double> incomingMap = new HashMap<>();
        for (int i = 0; i < incomingValues.length; i++) {
            incomingMap.put(incomingHeader.header[i], incomingValues[i]);
        }

        double[] result = new double[expectedHeader.header.length];

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
        filledUpValues.push((double) numFakeValues / (double) incomingValues.length);
        return result;
    }

}
