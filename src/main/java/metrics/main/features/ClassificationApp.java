package metrics.main.features;

import metrics.algorithms.classification.DecisionTreeClassificationAlgorithm;
import metrics.io.EmptyOutputStream;
import metrics.main.AppBuilder;
import metrics.main.Config;
import metrics.main.ExperimentData;

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

        DecisionTreeClassificationAlgorithm algo;

        public DecisionTree(AnalysisStep inputStep) {
            super("7.decision-tree.dot", inputStep);
            algo = new DecisionTreeClassificationAlgorithm();
        }

        @Override
        public String toString() {
            return "Decision tree algorithm";
        }

        @Override
        protected void addAlgorithms(AppBuilder builder) {
            builder.addAlgorithm(algo);
        }

        protected void doExecute(ExperimentData.Host host, File outputDir) throws IOException {
            AppBuilder builder = makeBuilder(host, outputDir);
            addAlgorithms(builder);
            File output = getOutputFile(outputDir);
            builder.setOutput(new EmptyOutputStream());
            builder.runAndWait();

            message("Writing " + toString() + " for " + host + " to " + output.toString());
            String graph = algo.getGraphString();
            FileOutputStream file = new FileOutputStream(output);
            file.write(graph.getBytes());
            file.close();

            String target = output.toString() + ".png";
            String cmd = "dot -Tpng " + output.toString() + " -o " + target;
            message("Executing dot command: " + cmd);
            try {
                Runtime.getRuntime().exec(cmd).waitFor();
            } catch (InterruptedException e) {
                throw new IOException("Interrupted", e);
            }
        }
    }

}
