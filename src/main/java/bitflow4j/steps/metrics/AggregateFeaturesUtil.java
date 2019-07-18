package bitflow4j.steps.metrics;

import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.misc.OnlineWindowStatistics;
import bitflow4j.script.registry.BitflowConstructor;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by anton on 09.02.17.
 */
public class AggregateFeaturesUtil {
    Logger logger = Logger.getLogger(AggregateFeaturesUtil.class.getName());

    private final Map<String, OnlineWindowStatistics> stats = new HashMap<>();
    private final int window;
    private final ValueGetter[] getters;

    private final String[] suffixes;

    /**
     * Constructor for usage by batch or sample-wise step
     **/
    public AggregateFeaturesUtil(int window, ValueGetter[] getters, String[] suffixes) {
        if (getters.length != suffixes.length) {
            throw new IllegalArgumentException("The length of getters and suffixes does not match: " + getters.length + " != "
                    + suffixes.length);
        }
        this.window = window;
        this.getters = getters;
        this.suffixes = suffixes;
    }

    public interface ValueGetter {
        double compute(OnlineWindowStatistics stats);
    }

    private static final Map<String, ValueGetter> ALL_GETTERS = new HashMap<>();

    static {
        ALL_GETTERS.put("input", null);
        ALL_GETTERS.put("mean", OnlineWindowStatistics::mean);
        ALL_GETTERS.put("stddev", OnlineWindowStatistics::standardDeviation);
        ALL_GETTERS.put("var", OnlineWindowStatistics::variance);
        ALL_GETTERS.put("slope", OnlineWindowStatistics::slope);
        ALL_GETTERS.put("relSlope", OnlineWindowStatistics::relative_slope);
        ALL_GETTERS.put("meanSlope", OnlineWindowStatistics::mean_slope);
    }

    protected static ValueGetter[] makeGetters(String... names) {
        if (names.length < 1) {
            throw new IllegalArgumentException("Need at least one feature name to compute");
        }
        ValueGetter[] result = new ValueGetter[names.length];
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            if (!ALL_GETTERS.containsKey(name)) {
                throw new IllegalArgumentException("Unknown ValueGetter name: " + name);
            }
            result[i] = ALL_GETTERS.get(name);
        }
        return result;
    }

    public static String[] makeSuffixes(String... names) {
        names = Arrays.copyOf(names, names.length);
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals("input"))
                names[i] = "";
            else
                names[i] = "_" + names[i];
        }
        return names;
    }

    private final Set<String> warnedNan = new HashSet<>();

    public Sample compute(Sample sample) {
        int num = sample.getMetrics().length;
        double[] values = new double[num * getters.length];
        String[] header = new String[values.length];

        for (int i = 0; i < num; i++) {
            // For every metric: Set name and value
            String field = sample.getHeader().header[i];
            double value = sample.getMetrics()[i];

            // Push the value once to the statistics
            OnlineWindowStatistics stat = getStats(field);
            stat.push(value);

            for (int j = 0; j < getters.length; j++) {
                // For every requested Statistic: Get it
                double outVal = getters[j] == null ? value : getters[j].compute(stat);
                String suffix = suffixes[j];

                if (Double.isNaN(outVal) || Double.isInfinite(outVal)) {
                    if (!warnedNan.contains(suffix)) {
                        warnedNan.add(suffix);
                        logger.log(Level.FINE, "Feature aggregation produced an invalid value for {0}: {1}", new Object[]{suffix, outVal});
                    }
                    outVal = 0;
                }

                values[i * getters.length + j] = outVal;
                header[i * getters.length + j] = field + suffix;
            }
        }
        return new Sample(new Header(header), values, sample);
    }

    private OnlineWindowStatistics getStats(String name) {
        if (stats.containsKey(name)) {
            return stats.get(name);
        } else {
            OnlineWindowStatistics res = new OnlineWindowStatistics(window);
            stats.put(name, res);
            return res;
        }
    }

    public String[] getSuffixes() {
        return suffixes;
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
