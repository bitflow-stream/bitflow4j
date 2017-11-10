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
    private static final String lineSepString = "\n";
    static final byte[] lineSepBytes = lineSepString.getBytes();
    private static final int lineSep = lineSepString.charAt(0);

    protected boolean discardTime;
    protected boolean discardTags;

    public AbstractMarshaller(){
        this.discardTime = false;
        this.discardTags = false;
    }

    public AbstractMarshaller(boolean discardTime, boolean discardTags){
        this.discardTime = discardTime;
        this.discardTags = discardTags;
    }

    static String readLine(InputStream input) throws IOException {
        int chr;
        StringBuilder buffer = new StringBuilder(512);
        while ((chr = input.read()) != lineSep) {
            if (chr < 0) {
                throw new InputStreamClosedException();
            }
            buffer.append((char) chr);
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

}
