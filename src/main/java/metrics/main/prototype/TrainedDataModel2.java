package metrics.main.prototype;

import metrics.main.TrainedDataModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by anton on 6/23/16.
 */
public class TrainedDataModel2 extends TrainedDataModel {
    public Map<String, Double> mins;
    public Map<String, Double> maxs;

    // Not filled: model, headerFields, allClasses
    public void fillFromStatistics(FeatureStatistics stats) {
        mins = new HashMap<>();
        maxs = new HashMap<>();
        averages = new HashMap<>();
        stddevs = new HashMap<>();
        for (FeatureStatistics.Feature feature : stats.allFeatures()) {
            mins.put(feature.name, feature.min);
            maxs.put(feature.name, feature.max);
            averages.put(feature.name, feature.avg);
            stddevs.put(feature.name, feature.stddev);
        }
    }

}
