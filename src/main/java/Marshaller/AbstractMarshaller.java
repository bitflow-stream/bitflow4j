package Marshaller;

import MetricIO.InputStreamClosedException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by anton on 4/6/16.
 */
public abstract class AbstractMarshaller implements Marshaller {

    // This is not necessarily System.getProperty("line.separator")
    protected static String lineSepString = "\n";
    protected static byte[] lineSepBytes = lineSepString.getBytes();
    protected static int lineSep = lineSepString.charAt(0);

    protected String readLine(InputStream input) throws IOException {
        int chr;
        StringBuffer buffer = new StringBuffer(512);

        while ((chr = input.read()) != lineSep) {
            if (chr < 0) {
                throw new InputStreamClosedException();
            }
            buffer.append((char) chr);
        }
        return buffer.toString();
    }

}
