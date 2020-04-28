package bitflow4j;

import java.io.IOException;

/**
 * Created by anton on 4/6/16.
 * <p>
 * Exception used by Marshaller unmarshall*() implementations to
 * notify that the DataInputStream was unexpectedly closed from the outside.
 */
public class BitflowProtocolError extends IOException {

    public BitflowProtocolError(String message) {
        super(message);
    }

    public BitflowProtocolError(String message, Exception cause) {
        super(message, cause);
    }

}
