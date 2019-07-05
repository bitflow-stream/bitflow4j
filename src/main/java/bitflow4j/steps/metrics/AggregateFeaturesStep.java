package bitflow4j.steps.metrics;

import bitflow4j.AbstractPipelineStep;
import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.misc.OnlineWindowStatistics;
import bitflow4j.script.registry.Description;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by anton on 09.02.17.
 */
@Description("Calculates the requested features sample-wise within the specified window of samples.")
public class AggregateFeaturesStep extends AbstractPipelineStep {

    private final AggregateFeaturesUtil featuresUtil;

    /**
     * Constructor for bitflow-script, features should be a comma-separated string.
     **/
    public AggregateFeaturesStep(int window, List<String> features) {
        this(window, features.toArray(String[]::new));
    }

    public AggregateFeaturesStep(int window, String... features) {
        this(window, AggregateFeaturesUtil.makeGetters(features), AggregateFeaturesUtil.makeSuffixes(features));
    }

    public AggregateFeaturesStep(int window, AggregateFeaturesUtil.ValueGetter[] getters, String[] suffixes) {
        this.featuresUtil = new AggregateFeaturesUtil(window, getters, suffixes);
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        super.writeSample(featuresUtil.compute(sample));
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("aggregator [");
        boolean added = false;
        for (String name : featuresUtil.getSuffixes()) {
            if (added) res.append(", ");
            added = true;
            res.append(name);
        }
        return res + "]";
    }
}
