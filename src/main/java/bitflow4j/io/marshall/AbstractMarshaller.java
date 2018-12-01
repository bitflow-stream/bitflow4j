package bitflow4j.io.marshall;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by anton on 4/6/16.
 * <p>
 * Base class for Marshaller implementations, offers readLine() method missing in InputStream.
 */
public abstract class AbstractMarshaller implements Marshaller {

    // This is not necessarily System.getProperty("line.separator")
    private static final String lineSepString_1 = "\n";
    private static final String lineSepString_2 = "\r";

    private static final int lineSep_1 = lineSepString_1.charAt(0);
    private static final int lineSep_2 = lineSepString_2.charAt(0);

    static final byte[] lineSepBytes_1 = lineSepString_1.getBytes();
    static final byte[] lineSepBytes_2 = lineSepString_2.getBytes();

    static String readLine(InputStream input) throws IOException {
        int chr = input.read();
        StringBuilder buffer = new StringBuilder(512);
        while (chr != lineSep_1 && chr != lineSep_2) {
            if (chr < 0) {
                throw new InputStreamClosedException();
            }
            buffer.append((char) chr);
            chr = input.read();
        }
        return buffer.toString();
    }

    static byte[] peek(InputStream input, int numBytes) throws IOException {
        if (!input.markSupported()) {
            throw new IllegalArgumentException("Cannot peek from a " + input.getClass().getName() + ", since it does not support mark/reset");
        }

        input.mark(numBytes);
        byte[] readBytes = new byte[numBytes];

        try {
            int n = 0;
            while (n < numBytes) {
                int count = input.read(readBytes, n, numBytes - n);
                if (count < 0) {
                    if (n == 0) {
                        throw new InputStreamClosedException();
                    } else {
                        throw new InputStreamClosedException(new IOException("While trying to peek " + numBytes + " bytes, got EOF after "
                                + n));
                    }
                }
                n += count;
            }
            return readBytes;
        } finally {
            input.reset();
        }
    }

    static void cleanLineSeparation(InputStream input) throws IOException {
        //Handling \r\n... line breaks
        input.mark(1);
        int chr = input.read();
        while(chr == lineSep_1 || chr == lineSep_2) {
            input.mark(1);
            chr = input.read();
        }
        input.reset();
    }

}
