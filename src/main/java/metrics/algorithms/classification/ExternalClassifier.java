package metrics.algorithms.classification;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.JRip;
import weka.classifiers.rules.OneR;
import weka.classifiers.trees.*;

/**
 * @author fschmidt
 */
public enum ExternalClassifier {

    J48(weka.classifiers.trees.J48::new),
    LMT(weka.classifiers.trees.LMT::new),
    HOEFFDING_TREE(HoeffdingTree::new),
    REP_TREE(REPTree::new),
    RANDOM_TREE(RandomTree::new),
    DECISION_STUMP(DecisionStump::new),
    DECISION_TABLE(DecisionTable::new),
    RANDOM_FOREST(RandomForest::new),
    JRIP(JRip::new),
    ONER(OneR::new),
    PART(weka.classifiers.rules.PART::new),
    NAIVE_BAYES(NaiveBayes::new),
    SMO(weka.classifiers.functions.SMO::new),
    BAYES_NET(BayesNet::new),
    MULTILAYER_PERCEPTRON(MultilayerPerceptron::new);

    private final ClassifierFactory classifier;

    ExternalClassifier(ClassifierFactory classifier) {
        this.classifier = classifier;
    }

    public AbstractClassifier newInstance() {
        return classifier.make();
    }

    private interface ClassifierFactory {
        AbstractClassifier make();
    }

}
