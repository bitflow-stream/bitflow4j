package bitflow4j.window;

import java.util.LinkedList;

/**
 *
 * @author fschmidt
 */
public class SlidingMetricWindow extends AbstractMetricWindow {

    //TODO: add timeSpan as parameter
    private final int windowSize;
    private final LinkedList<Double> window;

    public SlidingMetricWindow(String name, int windowSize) {
        super(name);
        this.windowSize = windowSize;
        this.window = new LinkedList<>();
    }

    @Override
    public double[] getVector() {
        double[] returnVector = new double[window.size()];
        for (int i = 0; i < window.size(); i++) {
            returnVector[i] = window.get(i);
        }
        return returnVector;
    }

    @Override
    public void add(double val) {
        window.offer(val);
        if (windowSize > 0 && window.size() > windowSize) {
            window.poll();
        }
    }

    @Override
    public void clear() {
        window.clear();
    }

}
