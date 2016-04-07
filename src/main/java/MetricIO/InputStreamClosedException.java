package MetricIO;

import java.io.IOException;

/**
 * Created by anton on 4/6/16.
 *
 * Exception used by Marshaller unmarshall*() implementations to
 * notify that the DataInputStream was unexpectedly closed from the outside.
 */
public class InputStreamClosedException extends IOException {
    public InputStreamClosedException() {
        super("Input stream closed");
    }
}