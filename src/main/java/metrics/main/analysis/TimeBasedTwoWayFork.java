package metrics.main.analysis;

import metrics.Sample;
import metrics.io.fork.OutputStreamFactory;
import metrics.io.fork.TwoWayFork;
import metrics.io.window.SampleMetadata;
import metrics.io.window.SortedWindow;
import metrics.main.misc.ParameterHash;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 5/4/16.
 * <p>
 * Read all samples into memory, sort them by timestamp, then split them into two OutputStreams
 * based on their timestamps.
 */
public class TimeBasedTwoWayFork extends TwoWayFork {

    private final float timeframePercentage;
    private final SortedWindow window = new SortedWindow();

    public TimeBasedTwoWayFork(float timeframePercentage) {
        this(timeframePercentage, null);
    }

    public TimeBasedTwoWayFork(float timeframePercentage, OutputStreamFactory<ForkType> outputs) {
        super(outputs);
        if (timeframePercentage > 0.5 || timeframePercentage <= 0) {
            throw new IllegalArgumentException("timeframePercentage must be between 0 and 0.5");
        }
        this.timeframePercentage = timeframePercentage;
    }

    public void writeSample(Sample sample) throws IOException {
        if (sample.getTimestamp() == null) {
            System.err.println("Warning: Received Sample without timestamp, dropping...");
        }
        window.add(sample);
    }

    @Override
    public void close() throws IOException {
        flushResults();
        super.close();
    }

    private void flushResults() throws IOException {
        Date start = window.getFirst().timestamp;
        Date end = window.getLast().timestamp;
        long totalDuration = end.getTime() - start.getTime();
        if (totalDuration <= 0) {
            throw new IOException("Last Sample has timestamp prior or equal to first sample: " + totalDuration);
        }
        long duration = (long) (totalDuration * timeframePercentage);
        Date extStart = addDate(start, duration);
        Date extEnd = addDate(end, -duration);

        HashMap<String, Integer> classesInPrimary = new HashMap<>();
        HashMap<String, Integer> classesInSecondary = new HashMap<>();
        HashMap<String, Integer> droppedClasses = new HashMap<>();

        int primary = 0;
        int secondary = 0;
        int dropped = 0;
        int forceDropped = 0;
        for (int i = 0; i < window.numSamples(); i++) {
            SampleMetadata meta = window.getSampleMetadata(i);
            String label = meta.label;
            Date timestamp = meta.timestamp;
            if (timestamp.before(extStart)) {
                getOutputStream(ForkType.Primary).writeSample(window.getSample(i));
                increment(classesInPrimary, label);
                primary++;
            } else if (timestamp.after(extEnd)) {
                if (!classesInPrimary.containsKey(label)) {
                    increment(droppedClasses, label);
                    forceDropped++;
                } else {
                    increment(classesInSecondary, label);
                    getOutputStream(ForkType.Secondary).writeSample(window.getSample(i));
                    secondary++;
                }
            } else {
                dropped++;
            }
        }

        SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String dur = fmtDuration(duration);
        System.err.println("Split samples from [" + fmt.format(start) + "] to [" + fmt.format(end)
                + "] (" + dur + ", " + fmtDuration(totalDuration - 2 * duration) +
                ", " + dur + " = " + fmtDuration(totalDuration) + "): "
                + primary + ", " + dropped + ", " + secondary);
        if (forceDropped > 0) {
            System.err.println("Dropped " + forceDropped + " samples because following classes were" +
                    " not available in first time segment: " + droppedClasses);
            System.err.println("Following classes were available in the first time segment: " + classesInPrimary);
            System.err.println("Following classes were available in the second time segment: " + classesInSecondary);
        }
    }

    private void increment(Map<String, Integer> map, String key) {
        Integer val = map.get(key);
        if (val == null) {
            val = 0;
        }
        map.put(key, val + 1);
    }

    @Override
    public String toString() {
        return "time-based two-way split (" + timeframePercentage + ")";
    }

    private static String fmtDuration(long duration) {
        Duration dur = Duration.ofMillis(duration);
        return dur.toHours() + ":" + (dur.toMinutes() % 60) + ":" + ((dur.toMillis() / 1000) % 60);
    }

    private static Date addDate(Date date, long millis) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(date);
        startCal.add(Calendar.MILLISECOND, (int) millis);
        return startCal.getTime();
    }

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeFloat(timeframePercentage);
    }

}
