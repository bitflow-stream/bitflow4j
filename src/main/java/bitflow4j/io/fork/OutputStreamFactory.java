package bitflow4j.io.fork;

import bitflow4j.io.MetricOutputStream;

import java.io.IOException;

/**
 * Created by anton on 5/2/16.
 */
public interface OutputStreamFactory<T> {

    MetricOutputStream getOutputStream(T key) throws IOException;

}
