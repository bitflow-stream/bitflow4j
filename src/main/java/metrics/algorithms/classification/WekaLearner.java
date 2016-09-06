package metrics.algorithms.classification;

import metrics.Header;
import metrics.io.MetricOutputStream;
import metrics.main.Config;
import metrics.main.misc.ParameterHash;
import weka.classifiers.Classifier;
import weka.core.Drawable;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by anton on 4/23/16.
 */
public class WekaLearner<T extends Classifier & Serializable> extends AbstractWekaAlgorithm {

    private final Model<T> model;
    private final T classifier;

    // hack: keep info for after flushResults()
    public ArrayList<String> allFlushedClasses;
    public Header flushedHeader;

    public WekaLearner(Model<T> model, T classifier) {
        this.model = model;
        this.classifier = classifier;
    }

    @Override
    protected void flushResults(MetricOutputStream output) throws IOException {
        allFlushedClasses = allClasses();
        flushedHeader = window.getHeader();

        Instances instances = createDataset();
        fillDataset(instances);
        try {
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            IOException io = new IOException(toString() + ": Learning failed", e);
            model.modelProducerFailed(io);
            throw io;
        }
        model.setModel(classifier);
    }

    public void printResults(File file) {
        if (file == null) {
            System.err.println("Not producing dot graph files.");
            return;
        }
        createPng(file, classifier);
    }

    public static void createPng(File file, Classifier classifier) {
        if (classifier instanceof Drawable) {
            try {
                String graph = ((Drawable) classifier).graph();
                createPng(graph, file);
            } catch (Exception e) {
                System.err.println("Failed to produce classification graph");
                e.printStackTrace();
            }
        } else {
            System.err.println("Not producing dot graph file: Instance of " + classifier.getClass().toString() + " is not Drawable");
        }
    }

    public static void createPng(String dotString, File outputFile) throws
            IOException {
        String cmd[] = new String[]{Config.getInstance().getDotPath(), "-Tpng", "-o", outputFile.getAbsolutePath()};
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

    @Override
    public void hashParameters(ParameterHash hash) {
        super.hashParameters(hash);
        hash.writeClassName(classifier);
    }
}
