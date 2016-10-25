package bitflow4j;

import bitflow4j.io.InputStreamClosedException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by anton on 4/6/16.
 * <p>
 * Base class for Marshaller implementations, offers readLine() method missing in InputStream.
 */
public abstract class AbstractMarshaller implements Marshaller {

    // This is not necessarily System.getProperty("line.separator")
    private static String lineSepString = "\n";
    static byte[] lineSepBytes = lineSepString.getBytes();
    private static int lineSep = lineSepString.charAt(0);

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

}
