package metrics.algorithms.clustering.outliers;

import com.github.javacliparser.FlagOption;
import com.github.javacliparser.FloatOption;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.Options;
import metrics.Sample;
import metrics.algorithms.clustering.ExternalClusterer;
import moa.clusterers.outliers.AnyOut.AnyOut;
import moa.clusterers.outliers.MyBaseOutlierDetector;
import moa.options.OptionsHandler;
import weka.core.Option;

import java.util.Set;

/**
 * Created by malcolmx on 23.08.16.
 */
public class AnyOutOutlierDetector extends MOAStreamOutlierDetection<AnyOut> {
    private final Integer trainingSetSize;
    private final Integer oScoreAggregationSize;
    private final Integer confidenceAggregationSize;
    private final Integer confidence;
    private final Boolean useMeanScore;
    private final Float threshold;

    public AnyOutOutlierDetector(boolean alwaysTrain, Integer trainingSetSize, Integer OScoreAggregationSize, Integer confidenceAggregationSize, Integer confidence, Boolean useMeanScore, Float threshold) {
        super((AnyOut) ExternalClusterer.ANY_OUT.newInstance(), alwaysTrain);
        this.trainingSetSize = trainingSetSize;
        oScoreAggregationSize = OScoreAggregationSize;
        this.confidenceAggregationSize = confidenceAggregationSize;
        this.confidence = confidence;
        this.useMeanScore = useMeanScore;
        this.threshold = threshold;
    }

    public AnyOutOutlierDetector(Set<String> trainedLabels, Integer trainingSetSize, Integer OScoreAggregationSize, Integer confidenceAggregationSize, Integer confidence, Boolean useMeanScore, Float threshold) {
        super((AnyOut) ExternalClusterer.ANY_OUT.newInstance(), trainedLabels);
        this.trainingSetSize = trainingSetSize;
        oScoreAggregationSize = OScoreAggregationSize;
        this.confidenceAggregationSize = confidenceAggregationSize;
        this.confidence = confidence;
        this.useMeanScore = useMeanScore;
        this.threshold = threshold;
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
