package metrics.main.analysis;

import metrics.Sample;
import metrics.io.fork.OutputStreamFactory;
import metrics.io.fork.TwoWayFork;
import metrics.main.misc.ParameterHash;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by anton on 5/12/16.
 * <p>
 * This fork splits samples into two subsets based on a given percentage (like TimeBasedTwoWayFork).
 * The incoming Samples are expected to be sorted by their timestamp (IllegalStateException is thrown otherwise).
 * This way there is no need to store all Samples in memory.
 */
public class SortedTimeBasedFork extends TwoWayFork {

    private static final Logger logger = Logger.getLogger(SortedTimeBasedFork.class.getName());

    private final float timeframePercentage;
    private final Date lastSample;

    private final HashMap<String, Integer> classesInPrimary = new HashMap<>();
    private final HashMap<String, Integer> classesInSecondary = new HashMap<>();
    private final HashMap<String, Integer> droppedClasses = new HashMap<>();

    private Date extStart = null;
    private Date extEnd = null;
    private Date lastReceivedTimestamp = null;

    // Statistics for printing
    private int total = 0;
    private long duration = 0;
    private long totalDuration = 0;
    private Date start;
    private int primary = 0;
    private int secondary = 0;
    private int dropped = 0;
    private int forceDropped = 0;

    public SortedTimeBasedFork(float timeframePercentage, Date lastSample) {
        this(timeframePercentage, lastSample, null);
    }

    public SortedTimeBasedFork(float timeframePercentage, Date lastSample, OutputStreamFactory<TwoWayFork.ForkType> outputs) {
        super(outputs);
        if (timeframePercentage > 0.5 || timeframePercentage <= 0) {
            throw new IllegalArgumentException("timeframePercentage must be between 0 and 0.5");
        }
        this.timeframePercentage = timeframePercentage;
        this.lastSample = lastSample;
    }

    public void writeSample(Sample sample) throws IOException {
        total++;
        Date timestamp = sample.getTimestamp();
        if (timestamp == null) {
            logger.warning("Received Sample without timestamp, dropping...");
            return;
        }
        if (extStart == null) {
            start = timestamp;
            totalDuration = lastSample.getTime() - start.getTime();
            if (totalDuration <= 0) {
                throw new IOException("Last Sample has timestamp prior or equal to first sample: " + totalDuration);
            }
            duration = (long) (totalDuration * timeframePercentage);
            extStart = addDate(start, duration);
            extEnd = addDate(lastSample, -duration);
            printTimerange();
        }

        if (lastReceivedTimestamp != null) {
            if (lastReceivedTimestamp.after(timestamp))
                throw new IllegalStateException("Received sample nr. " + total +
                        " with timestamp later then last received sample. Samples must be sorted! " +
                        lastReceivedTimestamp + " -> " + timestamp);
        }
        lastReceivedTimestamp = timestamp;
        String label = sample.getLabel();
        if (timestamp.before(extStart)) {
            getOutputStream(TwoWayFork.ForkType.Primary).writeSample(sample);
            increment(classesInPrimary, label);
            primary++;
        } else if (timestamp.after(extEnd)) {
            if (!classesInPrimary.containsKey(label)) {
                increment(droppedClasses, label);
                forceDropped++;
            } else {
                increment(classesInSecondary, label);
                getOutputStream(TwoWayFork.ForkType.Secondary).writeSample(sample);
                secondary++;
            }
        } else {
            dropped++;
        }
    }

    @Override
    public void close() throws IOException {
        printStatistics();
        super.close();
    }

    private void printTimerange() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String dur = fmtDuration(duration);
        logger.info("Splitting samples from [" + fmt.format(start) + "] to [" +
                fmt.format(lastSample) + "] (" + dur + ", " + fmtDuration(totalDuration - 2 * duration) +
                ", " + dur + " = " + fmtDuration(totalDuration) + ")");
    }

    private void printStatistics() {
        logger.info("Samples in each time range: " + primary + ", " + dropped + ", " + secondary + " (" + total + " total)");
        if (forceDropped > 0) {
            logger.info("Dropped " + forceDropped + " samples because following classes were" +
                    " not available in first time segment: " + droppedClasses);
            logger.info("Following classes were available in the first time segment: " + classesInPrimary);
            logger.info("Following classes were available in the second time segment: " + classesInSecondary);
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
