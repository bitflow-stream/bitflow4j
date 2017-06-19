package bitflow4j.window;

import gnu.trove.list.linked.TDoubleLinkedList;

/**
 * @author fschmidt, Alex (15.05.2017)
 */
public class SlidingMetricWindow extends AbstractMetricWindow {

    //TODO: Add header changed case
    //TODO: add timeSpan as parameter
    private final int windowSize;
    private final TDoubleLinkedList window;

    public SlidingMetricWindow(String name, int windowSize) {
        super(name);
        this.windowSize = windowSize;
        this.window = new TDoubleLinkedList();
    }

    @Override
    public double[] getVector() {
        return window.toArray();
    }

    @Override
    public void add(double val) {
        window.add(val);
        if (windowSize > 0 && window.size() > windowSize) {
            window.removeAt(0);
        }
    }

    @Override
    public void clear() {
        window.clear();
    }

}
