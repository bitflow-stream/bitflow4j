package metrics.main.features;

import com.google.common.io.ByteArrayDataOutput;
import metrics.algorithms.classification.Model;
import metrics.algorithms.classification.WekaClassifier;
import metrics.algorithms.classification.WekaEvaluator;
import metrics.algorithms.classification.WekaLearner;
import metrics.main.AppBuilder;
import metrics.main.Config;
import metrics.main.ExperimentData;
import weka.classifiers.trees.J48;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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

    public static void createPng(String dotString, File outputFile) throws IOException {
        String cmd[] = new String[]{"dot", "-Tpng", "-o", outputFile.getAbsolutePath()};
        System.err.println("Executing command: " + Arrays.toString(cmd));
        Process dot = Runtime.getRuntime().exec(cmd);
        dot.getOutputStream().write(dotString.getBytes());
        dot.getOutputStream().close();
        try {
            dot.waitFor();
        } catch (InterruptedException e) {
            throw new IOException("Interrupted", e);
        }
    }

    public class DecisionTree extends AnalysisStep {

        private final float trainingPortion;

        Model<J48> model = new Model<>();
        J48 trainedModel = new J48();
        WekaLearner<J48> learner = new WekaLearner<>(model, trainedModel);
        WekaClassifier<J48> classifier = new WekaClassifier<>(model);
        WekaEvaluator<J48> evaluator = new WekaEvaluator<>(model);

        public DecisionTree(AnalysisStep inputStep, float trainingPortion) {
            super("7.decision-tree.png", inputStep);
            this.trainingPortion = trainingPortion;
        }

        @Override
        public String toString() {
            return "decision tree";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            AppBuilder fork = builder.fork(trainingPortion);
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
            String graph;
            try {
                graph = trainedModel.graph();
            } catch (Exception e) {
                throw new IOException("Failed to produce decision tree graph", e);
            }
            createPng(graph, output);
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

        @Override
        protected void hashParameters(ByteArrayDataOutput bytes) {
            bytes.writeFloat(trainingPortion);
        }
    }

}
