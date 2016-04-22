package metrics.algorithms.logback;

import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Created by anton on 4/21/16.
 */
public class MetricLog {

    public final String name;
    public final TDoubleList values = new TDoubleArrayList();

    private double[] vector = null; // Cache for getVector()

    public MetricLog(String name) {
        this.name = name;
    }

    void flushSamples(int numSamples) {
        if (numSamples >= values.size())
            values.clear();
        else
            values.remove(0, numSamples);
    }

    double defaultValue() {
        // This is used when accessing individual values to remove occurrences if NaN.
        // Only relevant, if fillValue() returns NaN.
        // TODO maybe use average?
        return Double.NaN;
    }

    double fillValue() {
        // This will be used whenever filling up the log with unknown values
        // and whenever a known NaN-values is to be added.
        return Double.NaN;
    }

    public double getValue(int sampleNr) {
        double val = values.get(sampleNr);
        if (Double.isNaN(val)) {
            val = defaultValue();
        }
        return val;
    }

    public double getLatestValue() {
        return getValue(values.size() - 1);
    }

    public TDoubleList getLatestValues(int numSamples) {
        // TODO avoid copy
        return values.subList(values.size() - 1 - numSamples, values.size());
    }

    public double[] getVector() {
        if (vector == null) {
            vector = values.toArray();
        }
        return vector;
    }

    public void fill(int num) {
        // TODO this can be more efficient
        for (int i = 0; i < num; i++) {
            values.add(fillValue());
        }
        vector = null;
    }

    public void add(double val) {
        if (Double.isNaN(val)) {
            val = fillValue();
        }
        values.add(val);
        vector = null;
    }

}
