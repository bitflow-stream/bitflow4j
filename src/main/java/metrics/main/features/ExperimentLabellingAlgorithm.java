package metrics.main.features;

import metrics.Sample;
import metrics.algorithms.LabellingAlgorithm;
import metrics.io.MetricOutputStream;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by anton on 4/14/16.
 */
public class ExperimentLabellingAlgorithm extends LabellingAlgorithm {

    private final int cutoffMinutes;
    private final String defaultLabel;

    private Date minStart = null;
    private Date maxEnd = null;

    public ExperimentLabellingAlgorithm(int cutoffMinutes, String defaultLabel) {
        this.cutoffMinutes = cutoffMinutes;
        this.defaultLabel = defaultLabel;
    }

    @Override
    protected void writeResults(MetricOutputStream output) throws IOException {
        if (sampleLog.size() < 2) {
            System.err.println("Skipping " + toString() + ", not enough samples");
            minStart = maxEnd = null;
            return;
        }
        Date start = sampleLog.get(0).getTimestamp();
        Date end = sampleLog.get(sampleLog.size() - 1).getTimestamp();
        if (sampleLog.size() < 2) {
            System.err.println("Skipping " + toString() + ", missing start/end timestamps");
            minStart = maxEnd = null;
            return;
        }

        // Reduce the time range of "valid" samples
        Calendar minStart = Calendar.getInstance();
        minStart.setTime(start);
        minStart.add(Calendar.MINUTE, cutoffMinutes);
        this.minStart = minStart.getTime();
        Calendar maxEnd = Calendar.getInstance();
        maxEnd.setTime(end);
        maxEnd.add(Calendar.MINUTE, -cutoffMinutes);
        this.maxEnd = maxEnd.getTime();

        super.writeResults(output);
    }

    @Override
    protected String newLabel(Sample sample) {
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

}
