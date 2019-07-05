package bitflow4j.steps.metrics;

import bitflow4j.Sample;
import bitflow4j.script.registry.Description;
import bitflow4j.steps.BatchHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author kevinstyp
 */
@Description("Calculates the requested features on a per batch base. Calculations for new batches do not yield information" +
        "from previous batches")
public class AggregateFeaturesBatch implements BatchHandler {

    private final AggregateFeaturesUtil.ValueGetter[] getters;
    private final String[] suffixes;

    AggregateFeaturesBatch(List<String> features){
        this(features.toArray(String[]::new));
    }

    public AggregateFeaturesBatch(String... features) {
        this(AggregateFeaturesUtil.makeGetters(features), AggregateFeaturesUtil.makeSuffixes(features));
    }

    public AggregateFeaturesBatch(AggregateFeaturesUtil.ValueGetter[] getters, String[] suffixes) {
        this.getters = getters;
        this.suffixes = suffixes;
    }

    @Override
    public List<Sample> handleBatch(List<Sample> batch) throws IOException {
        //Generate a new OnlineStatistics for every batch
        AggregateFeaturesUtil featuresUtil = new AggregateFeaturesUtil(batch.size(), getters, suffixes);

        Sample lastSampleResult = null;
        for (int i = 0; i < batch.size(); i++){
            if(i == batch.size() - 1) {
                // Only save and return the result of the last sample as
                // this represents the whole batch wihtin the OnlineStatistics
                lastSampleResult = featuresUtil.compute(batch.get(i));
            }
            else {
                featuresUtil.compute(batch.get(i));
            }
        }
        return Collections.singletonList(lastSampleResult);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("aggregator [");
        boolean added = false;
        for (String name : suffixes) {
            if (added) res.append(", ");
            added = true;
            res.append(name);
        }
        return res + "]";
    }
}
