package metrics.algorithms.evaluation;

import metrics.CsvMarshaller;
import metrics.Sample;
import metrics.algorithms.clustering.ClusterConstants;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

/**
 * Evaluator for predicting one of two classes for every Sample: is it "normal" or "abnormal".
 * The evaluation is triggered only for samples that entered the pipeline with a specific label.
 * This allows to control the evaluation at runtime, useful for long-running online systems.
 *
 * Created by anton on 9/2/16.
 */
public class OnlineOutlierEvaluator extends StreamEvaluator {

    private final Collection<String> normalLabels;
    private final String expectNormalLabel;
    private final String expectAbnormalLabel;

    private String incorrectnessLogfile = null;
    private String hostname = null;
    private Date incorrectStarted = null;
    private boolean incorrectShouldBeNormal;

    public OnlineOutlierEvaluator(boolean extendSample, Collection<String> normalLabels,
                                  String expectNormalLabel, String expectAbnormalLabel) {
        super(extendSample);
        this.normalLabels = normalLabels;
        this.expectNormalLabel = expectNormalLabel;
        this.expectAbnormalLabel = expectAbnormalLabel;
    }

    /**
     * Log all intervals of incorrect predictions to the given file as a CSV format (omitting the header).
     * The hostname will be added as a first column to simplify concatenating multiple such log files.
     */
    public OnlineOutlierEvaluator logIncorrectPredictions(String filename, String hostname) {
        this.incorrectnessLogfile = filename;
        this.hostname = hostname;
        return this;
    }

    @Override
    protected Boolean isCorrectPrediction(Sample sample) {
        Boolean shouldBeNormal = shouldBeNormal(sample);
        if (shouldBeNormal == null) {
            logPrediction(true, false); // In case an incorrect prediction was going on.
            return null;
        }

        boolean isNormal = normalLabels.contains(sample.getLabel());
        boolean correct = shouldBeNormal == isNormal;
        logPrediction(correct, shouldBeNormal);
        return correct;
    }

    private Boolean shouldBeNormal(Sample sample) {
        if (!sample.hasTag(ClusterConstants.ORIGINAL_LABEL_TAG)) return null;
        String originalLabel = sample.getTag(ClusterConstants.ORIGINAL_LABEL_TAG);
        boolean shouldBeNormal = originalLabel.equals(expectNormalLabel);
        boolean shouldBeAbnormal = originalLabel.equals(expectAbnormalLabel);
        if (!shouldBeNormal && !shouldBeAbnormal) return null;
        if (!sample.hasLabel()) return null;
        return shouldBeNormal;
    }

    private void logPrediction(boolean isCorrect, boolean shouldBeNormal) {
        if (isCorrect && incorrectStarted != null) {
            // Period with incorrect predictions has stopped, output a log line
            Date now = new Date();
            long duration = now.getTime() - incorrectStarted.getTime();
            System.err.println("Incorrect prediction interval ended: Duration " + duration + ", should have been normal: " + incorrectShouldBeNormal);
            incorrectStarted = null;
            if (incorrectnessLogfile != null) {
                try {
                    FileOutputStream out = new FileOutputStream(incorrectnessLogfile, true);
                    StringBuffer buf = new StringBuffer();
                    writeLogMessage(buf, incorrectShouldBeNormal, duration, now);
                    out.write(buf.toString().getBytes());
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    System.err.println("Failed to log incorrect prediction interval of "
                            + duration + "ms to " + incorrectnessLogfile + ": " + e);
                }
            }
        } else if (!isCorrect && incorrectStarted == null) {
            // Starting period with incorrect predictions
            incorrectStarted = new Date();
            incorrectShouldBeNormal = shouldBeNormal;
            System.err.println("Incorrect prediction interval starting, should have been normal: " + incorrectShouldBeNormal);
        }
    }

    private void writeLogMessage(StringBuffer buf, boolean shouldBeNormal, long duration, Date timestamp) {
        String dateStr = CsvMarshaller.date_formatter.format(timestamp);
        buf.append(dateStr).append(",");
        buf.append(hostname).append(",");
        buf.append(shouldBeNormal).append(",");
        buf.append(duration).append("\n");
    }

}
