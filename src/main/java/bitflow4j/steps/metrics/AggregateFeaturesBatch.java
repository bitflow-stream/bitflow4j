package bitflow4j.steps.metrics;

import bitflow4j.Sample;
import bitflow4j.script.registry.BitflowConstructor;
import bitflow4j.script.registry.Description;
import bitflow4j.steps.BatchHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author kevinstyp
 */
@Description("Calculates the requested features on a per batch base. Calculations for new batches do not yield information" +
        "from previous batches. singleSample=true produces a single sample containing the batch information, while" +
        "singleSample=false attaches the batch information to each input sample.")
public class AggregateFeaturesBatch implements BatchHandler {

    private final AggregateFeaturesUtil.ValueGetter[] getters;
    private final String[] suffixes;
    private final boolean singleSample;

    @BitflowConstructor
    AggregateFeaturesBatch(boolean singleSample, List<String> features) {
        this(singleSample, features.toArray(String[]::new));
    }

    public AggregateFeaturesBatch(boolean singleSample, String... features) {
        this(singleSample, AggregateFeaturesUtil.makeGetters(features), AggregateFeaturesUtil.makeSuffixes(features));
    }

    public AggregateFeaturesBatch(boolean singleSample, AggregateFeaturesUtil.ValueGetter[] getters, String[] suffixes) {
        this.getters = getters;
        this.suffixes = suffixes;
        this.singleSample = singleSample;
    }

    @Override
    public List<Sample> handleBatch(List<Sample> batch) throws IOException {
        //Generate a new OnlineStatistics for every batch
        AggregateFeaturesUtil featuresUtil = new AggregateFeaturesUtil(batch.size(), getters, suffixes);

        Sample lastSampleResult = null;
        for (int i = 0; i < batch.size(); i++) {
            if (i == batch.size() - 1) {
                // Only save and return the result of the last sample as
                // this represents the whole batch wihtin the OnlineStatistics
                lastSampleResult = featuresUtil.compute(batch.get(i));
            } else {
                featuresUtil.compute(batch.get(i));
            }
        }
        if (singleSample) {
            return Collections.singletonList(lastSampleResult);
        } else {
            int lastSampleResultSize = 0;
            if (lastSampleResult != null) {
                lastSampleResultSize = lastSampleResult.getMetrics().length;
            }
            String[] headerExtension = new String[lastSampleResultSize];
            double[] metricExtension = new double[lastSampleResultSize];
            for (int i = 0; i < lastSampleResultSize; i++) {
                headerExtension[i] = lastSampleResult.getHeader().header[i];
                metricExtension[i] = lastSampleResult.getMetrics()[i];
            }

            List<Sample> newBatch = new ArrayList<>(batch.size());
            for (int i = 0; i < batch.size(); i++) {
                Sample newSample = batch.get(i).extend(headerExtension, metricExtension);
                newBatch.add(newSample);
            }
            return newBatch;
        }
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
