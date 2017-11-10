package bitflow4j.window;

import bitflow4j.sample.Sample;
import gnu.trove.list.linked.TDoubleLinkedList;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author fschmidt, Alex (15.05.2017)
 */
public class SlidingMetricWindow extends AbstractMetricWindow {

    private static final Logger logger = Logger.getLogger(SlidingMetricWindow.class.getName());

    //TODO: Add header changed case
    //TODO: add timeSpan as parameter
    private final int windowSize;
    private final TDoubleLinkedList window;

    //Runtime optimization
    private int metricIndex = -1;

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

    public void add(Sample sample, String metricName){
        this.checkMetricNameIndex(metricName, sample.getHeader().header);
        if(metricIndex >= 0 && metricIndex < sample.getMetrics().length)
            this.add(sample.getMetrics()[metricIndex]);
    }

    private void checkMetricNameIndex(String metricName, String[] header) {
        if(metricIndex < 0 || !header[metricIndex].equals(metricName))
            this.scanForMetricIndex(metricName, header);
    }

    private void scanForMetricIndex(String metricName, String[] header) {
        for(int i = 0; i < header.length; i++){
            if(header[i].equals(metricName)) {
                metricIndex = i;
                break;
            }
        }
        if(metricIndex < 0)
            logger.log(Level.INFO, "Metric " + metricName + " not found.");
    }

    @Override
    public void clear() {
        window.clear();
    }

}
