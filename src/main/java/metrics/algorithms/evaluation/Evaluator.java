//package metrics.algorithms.evaluation;
//
//import metrics.Sample;
//import metrics.algorithms.AbstractAlgorithm;
//import metrics.algorithms.clustering.ClusterConstants;
//
//import java.io.IOException;
//
///**
// * Created by malcolmx on 15.08.16.
// */
//public class Evaluator extends AbstractAlgorithm {
//
//    private int correct;
//    private int wrong;
//    private int unknownLabel;
//    private int noPrediction;
//
//    public Evaluator(){
//        this.correct = 0;
//        this.wrong = 0;
//
//    }
//
//    @Override
//    public String toString() {
//        return "evaluator";
//    }
//
//    @Override
//    protected Sample executeSample(Sample sample) throws IOException {
//        String sampleLabel = sample.getLabel();
//        String originalLabel = sample.getTag(ClusterConstants.ORIGINAL_LABEL_TAG);
//
//        if(originalLabel == null) unknownLabel++;
//        if(sampleLabel == null) noPrediction++;
//        if(originalLabel != null && sampleLabel != null){
//            if(originalLabel == sampleLabel) correct++;
//            else wrong++;
//        }
//        if(wrong +)
//        return sample;
//    }
//}
