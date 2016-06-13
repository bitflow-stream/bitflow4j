package metrics.main.analysis;

import metrics.Sample;
import metrics.algorithms.BatchLabellingAlgorithm;
import metrics.io.MetricOutputStream;
import metrics.main.misc.ParameterHash;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by anton on 4/14/16.
 */
public class ExperimentLabellingAlgorithm extends BatchLabellingAlgorithm {

    private final int cutoffMinutes;
    private final String defaultLabel;

    private Date minStart = null;
    private Date maxEnd = null;

    public ExperimentLabellingAlgorithm(int cutoffMinutes, String defaultLabel) {
        this.cutoffMinutes = cutoffMinutes;
        this.defaultLabel = defaultLabel;
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        if (cutoffMinutes > 0) {
            if (window.numSamples() < 2) {
                System.err.println("Skipping " + toString() + ", not enough samples");
                minStart = maxEnd = null;
                return;
            }
            Date start = window.samples.getFirst().getTimestamp();
            Date end = window.samples.getLast().getTimestamp();

            // Reduce the time range of "valid" samples
            Calendar minStart = Calendar.getInstance();
            minStart.setTime(start);
            minStart.add(Calendar.MINUTE, cutoffMinutes);
            this.minStart = minStart.getTime();
            Calendar maxEnd = Calendar.getInstance();
            maxEnd.setTime(end);
            maxEnd.add(Calendar.MINUTE, -cutoffMinutes);
            this.maxEnd = maxEnd.getTime();
        }
        super.flushResults(output);
    }

    @Override
    protected String newLabel(Sample sample) {
        if (cutoffMinutes <= 0) {
            return sample.getSource();
        }
        if (minStart == null || maxEnd == null) {
            return sample.getLabel();
        }
        Date timestamp = sample.getTimestamp();
        if (timestamp.before(minStart) || timestamp.after(maxEnd)) {
            return defaultLabel;
        } else {
            return sample.getSource();
        }
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeInt(cutoffMinutes);
        hash.writeChars(defaultLabel);
    }
}
