package metrics.algorithms.filter;

import metrics.Sample;
import metrics.algorithms.WindowBatchAlgorithm;
import metrics.io.MetricOutputStream;
import metrics.io.window.AbstractSampleWindow;
import metrics.io.window.SampleWindowWithLabelCounter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
//TODO change javadoc ref
/**
 * This Algorithm can be used to create evaluation splits on an underlying sample stream (batch-mode only). The splits are created using a
 * This filter-algorithm can seperate the underlying stream into a training and an evaluation split. The training split will contain 80% of all samples that the  included.
 * The evaluation-split contains the other 20% of the samples and an euqal number of samples the filter originally excluded.
 */
public class BatchSampleFilterAlgorithm extends WindowBatchAlgorithm {

    private static final Logger logger = Logger.getLogger(BatchSampleFilterAlgorithm.class.getName());

    private static final String KEY_FOR_REMAINING_SPLIT_SIZE = "remaining samples to fill validation split";
    /**
     * The sample window, additionally stores the count for each label
     */
    protected final SampleWindowWithLabelCounter window = new SampleWindowWithLabelCounter();
    /**
     * The Sample filter
     */
    protected final SampleFilterAlgorithm.Filter filter;
    /**
     * The default size of the training split
     */
    public static final double DEFAULT_TRAINING_SPLIT_SIZE = 0.8;
    private final double trainingSplitSize;
    private final boolean getEvaluationSplit;
    private int sampleCount = 0;

    /**
     * Constructor with training split size
     * @param filter A filter that seperates the sample stream into training and evaluation samples (the algorithm will still split the filtered samples by a percentual value)
     * @param trainingSplitSize the percentual amount of samples filtered samples that goes to the training split.
     * @param getEvaluationSplit if true, the algorithm will only forward samples that belong to the evaluation split, else only the training split is forwarded
     */
    public BatchSampleFilterAlgorithm(SampleFilterAlgorithm.Filter filter, double trainingSplitSize, boolean getEvaluationSplit) {
        if(filter == null) this.filter = new SampleFilterAlgorithm.Filter() {
            @Override
            public boolean shouldInclude(Sample sample) {
                // TODO use parameterized set instead of these hardcoded labels.
                String label = sample.getLabel();
                return label.equals("idle") || label.equals("load") || label.equals("overload");
            }
        };//throw new IllegalArgumentException("Filter cannot be null");
        else this.filter = filter;
        if(trainingSplitSize >= 1 || trainingSplitSize <= 0) throw new IllegalArgumentException("invalid split size (ust be between 0 and 1)");
        this.trainingSplitSize = trainingSplitSize;
        this.getEvaluationSplit = getEvaluationSplit;
    }

    /**
     * Constructor without training split size (default value = {@link #DEFAULT_TRAINING_SPLIT_SIZE})
     * @param filter A filter that seperates the sample stream into training and evaluation samples (the algorithm will still split the filtered samples by a percentual value)
     * @param getEvaluationSplit if true, the algorithm will only forward samples that belong to the evaluation split, else only the training split is forwarded
     */
    public BatchSampleFilterAlgorithm(SampleFilterAlgorithm.Filter filter, boolean getEvaluationSplit){
        this(filter, DEFAULT_TRAINING_SPLIT_SIZE, getEvaluationSplit);
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        if (window.numSamples() < 1)
            throw new IllegalStateException("Cannot filter empty dataset");
        if(getEvaluationSplit){
            this.getEvaluationSplit(output);
        }else{
            this.getTrainingSplit(output);
        }

        logger.info("filter algo pushed " + sampleCount + " Samples to pipeline");
    }

    /**
     * Filter samples to get training set
     * @param output the metric output stream
     * @throws IOException if an error occurs
     */
    private void getTrainingSplit(MetricOutputStream output) throws IOException {
        //TODO prepare this to use all provided filtered labels not only idle, current implementation is inconsistent
        int numberOfIdlesToInclude = (int) (trainingSplitSize * window.getCountsForLabel("idle"));
        logger.info("counts for label " + window.getCountsForLabel("idle"));
        logger.info("number of idles to include: " + numberOfIdlesToInclude);
        if (numberOfIdlesToInclude <1) throw new IOException("no idle data in window, cannot filter");
        int counter = 0;
        for(Sample sample : window.samples){
            if(shouldInclude(sample)){
                counter++;
                //stop after split border reached
                if(counter > numberOfIdlesToInclude) break;
                output.writeSample(sample);
                sampleCount++;
            }
        }
    }
    /**
     * Filter samples to get evaluation set
     * @param output the metric output stream
     * @throws IOException if an error occurs
     */
    private void getEvaluationSplit(MetricOutputStream output) throws IOException {
        //TODO prepare this to use all provided filtered labels not only idle, current implementation is inconsistent
        Map<String, Integer> samplesToInclude;
        samplesToInclude = this.calculateSplitsizes();
        int numberOfIdlesToInclude = (int) ((1-trainingSplitSize) * window.getCountsForLabel("normal"));
//        if (numberOfIdlesToInclude <1) throw new IOException("no idle data in window, cannot filter");
        int counter = 0;
        //TODO
//        int remainingFill = 0;
        for(Sample sample : window.samples){
//            if(shouldInclude(sample)){
//         counter++;
            if (sample.getLabel().equals("normal")) {
                    numberOfIdlesToInclude--;
                    //exclude all samples from training split
                    if(numberOfIdlesToInclude >= 0) {

                        output.writeSample(sample);
                        sampleCount++;
                    }
                } else {
                    if(samplesToInclude.get(sample.getLabel()) > 0 || samplesToInclude.get(KEY_FOR_REMAINING_SPLIT_SIZE)> 0){
                        //include all samples untill splitsize reached
                        if (samplesToInclude.get(sample.getLabel()) > 0) {
                            samplesToInclude.put(sample.getLabel(), samplesToInclude.get(sample.getLabel())-1);
                            //TODO currently, missing samples for a label will result in incorrect splitsize
                            output.writeSample(sample);
                            sampleCount++;
                        }
                        else if (samplesToInclude.get(KEY_FOR_REMAINING_SPLIT_SIZE) > 0) samplesToInclude.put(sample.getLabel(), samplesToInclude.get(KEY_FOR_REMAINING_SPLIT_SIZE)-1);
                    }
            }
            }
//        }
    }

    /**
     * Calculates the number of samples to include for each label
     * @return
     */
    private Map<String, Integer> calculateSplitsizes() {
        //TODO this method will crash with small sample count or samall sample count per label only use with big samples counts
        Map<String, Integer> result = new HashMap<>();
        Map<String, Integer> counts = window.getCountsPerLabel();
        logger.info("calculating splitsizes for " + counts.size() + " Labels.");

//        int numberOfIdlesToInclude = (int) (trainingSplitSize * window.getCountsForLabel("idle"));
        int validationSplitSize = (int) ((1-trainingSplitSize) * window.getCountsForLabel("normal"));
        int splitSizePerLabel = validationSplitSize / (counts.size() -1);
        final int[] remainingSplitSize = {0};
        Map<String, Integer> splitSizeForLabel = new HashMap<>();
        ArrayList<String> labelsWithRemainingSamples = new ArrayList<>();
        counts.entrySet().stream().forEachOrdered(stringIntegerEntry -> {
                //use min of percentual splitsize and actual number of samples with label for real split
        int currentSplitSize = Math.min(splitSizePerLabel, stringIntegerEntry.getValue());
            //if there are not enough sample for a label to fill the split, use other labels to fill split
            int diffToDefaultSplitSize = Math.abs(currentSplitSize - splitSizePerLabel);
            if(diffToDefaultSplitSize > 0 ) {
                remainingSplitSize[0] += diffToDefaultSplitSize;
            }else {
                labelsWithRemainingSamples.add(stringIntegerEntry.getKey());
            }
            splitSizeForLabel.put(stringIntegerEntry.getKey(), currentSplitSize);
        });
        splitSizeForLabel.put(KEY_FOR_REMAINING_SPLIT_SIZE, remainingSplitSize[0]);
        logger.info("splits per label:");
        for(Map.Entry<String, Integer> entries : splitSizeForLabel.entrySet()){
            logger.info(entries.getKey() + " : " + entries.getValue());
        }
        return splitSizeForLabel;
    }

    /**
     * Call to udnerlying filter object
     * @param sample the current sample
     * @return wether the current sample should be included (decided by filter)
     */
    private boolean shouldInclude(Sample sample) {
        return filter.shouldInclude(sample);
    }

    @Override
    protected AbstractSampleWindow getWindow() {
        return this.window;
    }

    @Override
    public String toString() {
        return "batch filter algorithm";
    }


//    public BatchSampleFilterAlgorithm(Filter filter) {
//        super(filter);
//    }


}
