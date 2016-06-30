package metrics.main.prototype;

import metrics.algorithms.*;
import metrics.algorithms.classification.Model;
import metrics.algorithms.classification.WekaLearner;
import metrics.io.file.FileMetricReader;
import metrics.main.AlgorithmPipeline;
import metrics.main.TrainedDataModel;
import metrics.main.analysis.OpenStackSampleSplitter;
import weka.classifiers.trees.J48;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 6/9/16.
 */
public class Train {

    static final String TRAINING_INPUT_FORMAT = "BIN";

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Parameters: <input " + TRAINING_INPUT_FORMAT + " file> <output file> <filter>");
            return;
        }
        TrainedDataModel model = createDataModel(args[0], args[2]);
        storeDataModel(args[1], model);
    }

    static J48 createClassifier() {
        J48 j48 = new J48();

        j48.setConfidenceFactor(0.55f);
        j48.setMinNumObj(750);
//        j48.setReducedErrorPruning(true);

        return j48;
    }

    static TrainedDataModel createDataModel(String inputFile, String filter) throws IOException {
        J48 classifier = createClassifier();
        Algorithm filterAlgo = getFilter(filter);

        AbstractFeatureScaler standardizer;
        if (Analyse.USE_MIN_MAX_SCALING) {
            standardizer = new FeatureMinMaxScaler();
        } else {
            standardizer = new FeatureStandardizer();
        }
        WekaLearner<J48> learner = new WekaLearner<>(new Model<>(), classifier);

        new AlgorithmPipeline(AlgorithmPipeline.fileReader(inputFile, TRAINING_INPUT_FORMAT, FileMetricReader.FILE_NAME))
                .fork(new OpenStackSampleSplitter(),
                        (name, p) -> {
                            if (!name.isEmpty()) {
                                System.err.println("Error: received hostname from OpenstackSampleSplitter: " + name);
                                return;
                            }

                            p
                                    .step(filterAlgo)
                                    .step(standardizer)
                                    .step(new FeatureAggregator(10000L).addAvg().addSlope())
                                    .step(learner);
                        })
                .runAndWait();

        TrainedDataModel2 dataModel = new TrainedDataModel2();
        dataModel.model = classifier;
        dataModel.averages = new HashMap<>();
        dataModel.stddevs = new HashMap<>();
        dataModel.mins = new HashMap<>();
        dataModel.maxs = new HashMap<>();
        dataModel.allClasses = learner.allFlushedClasses;
        dataModel.headerFields = learner.flushedHeader.header;
        for (Map.Entry<String, AbstractFeatureScaler.MetricScaler> entry : standardizer.getScalers().entrySet()) {
            String name = entry.getKey();
            AbstractFeatureScaler.MetricScaler scaler = entry.getValue();
            if (!(scaler instanceof AbstractFeatureScaler.AbstractMetricScaler)) {
                throw new IllegalStateException("MetricScaler was not FeatureStandardizer.AbstractMetricScaler, but " +
                        scaler.getClass().toString());
            }
            AbstractFeatureScaler.AbstractMetricScaler  metric = (AbstractFeatureScaler.AbstractMetricScaler) scaler;
            dataModel.averages.put(name, metric.average);
            dataModel.stddevs.put(name, metric.stdDeviation);
            dataModel.mins.put(name, metric.min);
            dataModel.maxs.put(name, metric.max);
        }
        return dataModel;
    }

    static void storeDataModel(String cacheFile, TrainedDataModel model) throws IOException {
        System.err.println("Storing model to " + cacheFile);
        FileOutputStream file_out = new FileOutputStream(cacheFile);
        ObjectOutputStream obj_out = new ObjectOutputStream(file_out);
        obj_out.writeObject(model);
        obj_out.close();
    }

    static MetricFilterAlgorithm getFilter(String name) {
        if (name.equals("small")) {
            return new MetricFilterAlgorithm(new MetricFilterAlgorithm.MetricNameIncludeFilter(SMALL_FILTER));
        } else if (name.equals("medium")) {
            return new MetricFilterAlgorithm(new MetricFilterAlgorithm.MetricNameIncludeFilter(MEDIUM_FILTER));
        } else if (name.equals("none")) {
            return new MetricFilterAlgorithm(BAD_METRICS); // Just exclude a few bad metrics.
        } else {
            throw new IllegalArgumentException("Illegal filter name: " + name + ", available: 'small', 'medium', 'none'");
        }
    }

    // These metrics will STAY when using "small"
    static final String[] SMALL_FILTER = new String[] {
            "cpu", "mem/percent", "net-io/bytes",
            "disk-io/vda/ioTime", "disk-io/sda/ioTime",
            "proc/vnf/cpu", "proc/vnf/net-io/bytes"
    };

    // These metrics will STAY when using "medium"
    static final String[] MEDIUM_FILTER = new String[] {
            "time","cpu","disk-io/vda/ioTime","disk-io/vda/read","disk-io/vda/readBytes","disk-io/vda/readTime","disk-io/vda/write","disk-io/vda/writeBytes","disk-io/vda/writeTime","disk-usage///used","mem/percent","net-io/bytes","net-io/dropped","net-io/errors","net-io/packets","net-io/rx","bytes","net-io/rx","packets","net-io/tx","bytes","net-io/tx","packets","num","procs","proc/vnf/cpu","proc/vnf/ctxSwitch/involuntary","proc/vnf/ctxSwitch/voluntary","proc/vnf/disk/read","proc/vnf/disk/readBytes","proc/vnf/disk/write","proc/vnf/disk/writeBytes","proc/vnf/fds","proc/vnf/mem/rss","proc/vnf/mem/swap","proc/vnf/mem/vms","proc/vnf/net-io/bytes","proc/vnf/net-io/dropped","proc/vnf/net-io/errors","proc/vnf/net-io/packets","proc/vnf/net-io/rx","bytes","proc/vnf/net-io/rx","packets","proc/vnf/net-io/tx","bytes","proc/vnf/net-io/tx","packets","proc/vnf/num","proc/vnf/threads"
    };

    static final String[] BAD_METRICS = new String[] {
            "disk-usage///free", "disk-usage///used", "num_procs", "proc/vnf/num"
    };

}
