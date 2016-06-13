package metrics.main.analysis;

import metrics.algorithms.classification.Model;
import metrics.algorithms.classification.WekaClassifier;
import metrics.algorithms.classification.WekaEvaluator;
import metrics.algorithms.classification.WekaLearner;
import metrics.io.fork.TwoWayFork;
import metrics.main.AlgorithmPipeline;
import weka.classifiers.Classifier;

import java.io.File;
import java.io.Serializable;

import static metrics.io.fork.TwoWayFork.ForkType;

/**
 * Created by anton on 4/22/16.
 */
public class ClassifierFork<T extends Serializable & Classifier> implements AlgorithmPipeline.ForkHandler<TwoWayFork.ForkType> {

    private final File graphFile;

    private final Model<T> model = new Model<>();
    private final WekaLearner<T> learner;
    private final WekaClassifier<T> classifier = new WekaClassifier<>(model);
    private final WekaEvaluator<T> evaluator = new WekaEvaluator<>(model);

    public ClassifierFork(T trainedModel, String graphFilename) {
        learner = new WekaLearner<>(model, trainedModel);
        this.graphFile = new File(graphFilename);
    }

    @Override
    public void buildForkedPipeline(ForkType key, AlgorithmPipeline pipeline) {
        pipeline.emptyOutput();
        switch (key) {
            case Primary:
                pipeline.step(learner);
                pipeline.postExecute(() -> learner.printResults(graphFile));
                break;
            case Secondary:
                pipeline.step(evaluator);
                pipeline.postExecute(() -> evaluator.printResults(null));
                break;
        }
    }

}
