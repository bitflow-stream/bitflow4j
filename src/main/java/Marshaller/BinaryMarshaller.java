package Marshaller;

import MetricIO.InputStreamClosedException;
import Metrics.Sample;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mwall on 30.03.16.
 */
public class BinaryMarshaller extends AbstractMarshaller {

    public Sample unmarshallSample(InputStream input, String[] header) throws IOException {
        try {
            DataInputStream data = new DataInputStream(input);
            Date timestamp = new Date(data.readLong() / 1000000);

            Double[] metrics = new Double[header.length];
            for (int i = 0; i < header.length; i++) {
                metrics[i] = data.readDouble();
            }
            return new Sample(header, timestamp, metrics);
        } catch (EOFException exc) {
            throw new InputStreamClosedException();
        }
    }

    public String[] unmarshallHeader(InputStream input) throws IOException {
        List<String> headerList = new ArrayList<>();

        String headerField;
        while (!(headerField = readLine(input)).isEmpty()) {
            headerList.add(headerField);
        }

        return headerList.toArray(new String[0]);
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    public void marshallHeader(OutputStream output, String[] header) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

}
