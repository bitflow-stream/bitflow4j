package bitflow4j;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Reads old binary format. Left for backwards-compatibility with
 * files in the old format. The marshall*() operations still write the new binary
 * format.
 *
 * Created by anton on 23.12.16.
 */
public class OldBinaryMarshaller extends BinaryMarshaller {

    static final String OLD_BIN_HEADER_TIME = "time";

    public boolean peekIsHeader(BufferedInputStream input) throws IOException {
        // This format cannot correctly distinguish between a header and a sample
        // if the binary timestamp happens to collide with the bytes for the string "time".
        byte peeked[] = peek(input, OLD_BIN_HEADER_TIME.length());
        return Arrays.equals(peeked, OLD_BIN_HEADER_TIME.getBytes());
    }

    protected void readSampleStart(DataInputStream data) throws IOException {
        // Samples are not preceded by anything in this format
    }

}
