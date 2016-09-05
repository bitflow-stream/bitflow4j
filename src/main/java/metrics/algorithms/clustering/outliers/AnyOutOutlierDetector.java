package metrics.algorithms.clustering.outliers;

import com.github.javacliparser.FlagOption;
import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.Options;
import metrics.Sample;
import metrics.algorithms.clustering.ClusteringAlgorithm;
import moa.clusterers.outliers.AnyOut.AnyOut;
import moa.clusterers.outliers.MyBaseOutlierDetector;

/**
 * Created by malcolmx on 23.08.16.
 */
public class AnyOutOutlierDetector extends MOAStreamOutlierDetection<AnyOut> {

    private volatile Integer trainingSetSize = null;
    private volatile Integer oScoreAggregationSize = null;
    private volatile Boolean useMeanScore = null;
    private volatile Integer confidenceAggregationSize = null;
    private volatile Integer confidence = null;
    private volatile Float threshold = null;

    public AnyOutOutlierDetector(Integer trainingSetSize, Integer OScoreAggregationSize, Integer confidenceAggregationSize, Integer confidence, Boolean useMeanScore, Float threshold) {
        super((AnyOut) ClusteringAlgorithm.ANY_OUT.newInstance());
        this.trainingSetSize = trainingSetSize;
        this.oScoreAggregationSize = OScoreAggregationSize;
        this.confidenceAggregationSize = confidenceAggregationSize;
        this.confidence = confidence;
        this.useMeanScore = useMeanScore;
        this.threshold = threshold;
    }

    public void setConfidenceAggregationSize(Integer confidenceAggregationSize) {
        this.confidenceAggregationSize = confidenceAggregationSize;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public AnyOutOutlierDetector setTrainingSetSize(Integer trainingSetSize) throws IllegalStateException{
        if(trainingSetSize != null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.trainingSetSize = trainingSetSize;
            return this;
        }
    }

    public AnyOutOutlierDetector set0ScoreAggregationSize(Integer oScoreAggregationSize) throws IllegalStateException{
        if(oScoreAggregationSize != null) throw new IllegalStateException("Final option cannot be set twice.");
        else{
            this.oScoreAggregationSize = oScoreAggregationSize;
            return this;
        }
    }

    public AnyOutOutlierDetector useMeanScore() throws IllegalStateException{
        this.useMeanScore = true;
        return this;
    }

    public AnyOutOutlierDetector setThreshold(Float threshold) {
        if (threshold == null) {
            this.threshold = threshold;
            return this;
        } else {
            throw new IllegalStateException("Final option cannot be set twice");
        }
    }

    @Override
    protected void onOutlier(MyBaseOutlierDetector.Outlier outlier) {

    }

    @Override
    protected void onInlier(MyBaseOutlierDetector.Outlier outlier) {

    }

    @Override
    protected void setupClustererParameter(Sample firstSample) {
        Options options = clusterer.getOptions();
        if(trainingSetSize != null)((IntOption) options.getOption("TrainingSetSize")).setValue(trainingSetSize);
        if(oScoreAggregationSize != null)((IntOption) options.getOption("OScorek")).setValue(oScoreAggregationSize);
        if(confidenceAggregationSize != null)((IntOption) options.getOption("Confidencek")).setValue(confidenceAggregationSize);
        if(confidence != null)((IntOption) options.getOption("confidence")).setValue(confidence);
        if(useMeanScore != null)((FlagOption) options.getOption("UseMeanScore")).setValue(useMeanScore);
        if(threshold != null)((FloatOption) options.getOption("Threshold")).setValue(threshold);
    }
}
