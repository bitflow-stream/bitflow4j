package bitflow4j.steps.reorder;

import bitflow4j.Sample;
import bitflow4j.steps.BatchHandler;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * Created by anton on 5/12/16.
 * <p>
 * Read all samples from the input into memory and output them, sorted by their timestamp.
 */
public class Sort implements BatchHandler, Comparator<Sample> {

    private final String[] metricNames;

    public Sort(String... metricNames) {
        this.metricNames = metricNames;
    }

    @Override
    public List<Sample> handleBatch(List<Sample> window) throws IOException {
        window.sort(this);
        return window;
    }

    @Override
    public int compare(Sample o1, Sample o2) {
        for (String name : metricNames) {
            double v1 = o1.getValueOf(name);
            double v2 = o1.getValueOf(name);
            if (v1 < v2) return -1;
            if (v1 > v2) return 1;
        }
        return o1.getTimestamp().compareTo(o2.getTimestamp());
    }

}