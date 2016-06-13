package metrics.io.fork;

import metrics.io.MetricOutputStream;

import java.io.IOException;

/**
 * Created by anton on 5/2/16.
 */
public interface OutputStreamFactory<T> {

    MetricOutputStream getOutputStream(T key) throws IOException;

}
