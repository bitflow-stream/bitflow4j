package bitflow4j.misc;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

/**
 * Created by anton on 09.02.17.
 */
public class OnlineWindowStatistics extends OnlineStatistics {

    private final CircularFifoBuffer window;
    private final CircularFifoBuffer averages;
    private double latest = 0;
    private double latestAvg = 0;

    public OnlineWindowStatistics(int window) {
        this.window = new CircularFifoBuffer(window);
        this.averages = new CircularFifoBuffer(window);
    }

    @Override
    public void push(double x) {
        if (window.isFull()) {
            double oldest = (double) window.get();
            //remove(oldest);
            window.remove();
        }
        super.push(x);
        window.add(x);
        latest = x;

        double mean = mean();
        averages.add(mean);
        latestAvg = mean;
    }

    public double slope() {
        if (window.isEmpty()) return 0;
        double oldest = (double) window.get();
        return latest - oldest;
    }

    public double relative_slope() {
        if (latest == 0) return 0;
        return slope() / latest;
    }

    public double mean_slope() {
        if (averages.isEmpty()) return 0;
        double oldest = (double) averages.get();
        return latestAvg - oldest;
    }

}
