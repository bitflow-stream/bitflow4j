package bitflow4j.window;

/**
 * Created by anton on 5/6/16.
 */
public interface MetricWindowFactory<T extends MetricWindow> {

    T newMetricWindow(String name);

}