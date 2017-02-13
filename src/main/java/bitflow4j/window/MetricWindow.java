package bitflow4j.window;

import gnu.trove.list.linked.TDoubleLinkedList;

/**
 * Implements a FIFO queue for a single metric, i.e. a queue of double values.
 * <p>
 * Created by anton on 4/21/16.
 */
public class MetricWindow {

    public static final MetricWindowFactory<MetricWindow> FACTORY = MetricWindow::new;

    // TODO this should be an array backed ring, e.g. based on TDoubleArrayList
    public final TDoubleLinkedList values = new TDoubleLinkedList();
    public final String name;

    private double[] vector = null; // Cache for getVector()

    public MetricWindow(String name) {
        this.name = name;
    }

    public void flushSamples(int numSamples) {
        if (numSamples >= values.size())
            values.clear();
        else
            values.remove(0, numSamples);
    }

    public double getValue(int sampleNr) {
        return values.get(sampleNr);
    }

    public double[] getVector() {
        if (vector == null) {
            vector = values.toArray();
        }
        return vector;
    }

    private double fillValue() {
        return values.isEmpty() ? 0 : values.get(values.size() - 1);
    }

    public void fill(int num) {
        double previous = fillValue();
        for (int i = 0; i < num; i++) {
            addImpl(previous);
        }
    }

    public void add(double val) {
        if (Double.isNaN(val)) {
            val = fillValue();
        }
        addImpl(val);
    }

    public void addImpl(double val) {
        values.add(val);
        vector = null;
    }

    public void clear() {
        values.clear();
        vector = null;
    }

}
