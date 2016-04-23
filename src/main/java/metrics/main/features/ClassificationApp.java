package metrics.main.features;

import metrics.algorithms.classification.Model;
import metrics.algorithms.classification.WekaClassifier;
import metrics.algorithms.classification.WekaEvaluator;
import metrics.algorithms.classification.WekaLearner;
import metrics.main.AppBuilder;
import metrics.main.Config;
import metrics.main.ExperimentData;
import weka.classifiers.trees.J48;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by anton on 4/22/16.
 */
public class ClassificationApp extends DimensionReductionApp {

    public ClassificationApp(Config config, ExperimentData data) throws IOException {
        super(config, data);
    }

    @Override
    public String toString() {
        return "Classification";
    }

    public class DecisionTree extends AnalysisStep {

        Model<J48> model = new Model<>();
        J48 trainedModel = new J48();
        WekaLearner<J48> learner = new WekaLearner<>(model, trainedModel);
        WekaClassifier<J48> classifier = new WekaClassifier<>(model);
        WekaEvaluator<J48> evaluator = new WekaEvaluator<>(model);

        public DecisionTree(AnalysisStep inputStep) {
            super("7.decision-tree.dot", inputStep);
        }

        @Override
        public String toString() {
            return "decision tree";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            AppBuilder fork = builder.fork(0.8f);
            fork.setEmptyOutput();
            fork.addAlgorithm(learner);
            builder.addAlgorithm(evaluator);
        }

        @Override
        protected void postExecute(File output) throws IOException {
            System.out.println(evaluator.resultsString());
            if (output == null) {
                System.err.println("Not producing dot graph files.");
                return;
            }
            try {
                String graph = trainedModel.graph();
                FileOutputStream file = new FileOutputStream(output);
                file.write(graph.getBytes());
                file.close();
            } catch (Exception e) {
                throw new IOException("Failed to produce decision tree graph", e);
            }

            String target = output.toString() + ".png";
            String cmd = "dot -Tpng " + output.toString() + " -o " + target;
            message("Executing dot command: " + cmd);
            try {
                Runtime.getRuntime().exec(cmd).waitFor();
            } catch (InterruptedException e) {
                throw new IOException("Interrupted", e);
            }
        }

        @Override
        protected void setInMemoryOutput(AppBuilder builder) {
            builder.setEmptyOutput();
        }

        @Override
        protected void setFileOutput(AppBuilder builder, File output) throws IOException {
            System.err.println("Warning: ignoring output file " + output.toString());
            builder.setEmptyOutput();
        }

    }

}
