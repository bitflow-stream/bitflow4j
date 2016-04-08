package metrics;

import metrics.io.InputStreamClosedException;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mwall on 30.03.16.
 */
public class BinaryMarshaller extends AbstractMarshaller {

    public String[] unmarshallHeader(InputStream input) throws IOException {
        List<String> headerList = new ArrayList<>();

        String headerField;
        while (!(headerField = readLine(input)).isEmpty()) {
            headerList.add(headerField);
        }

        return headerList.toArray(new String[0]);
    }

    public Sample unmarshallSample(InputStream input, String[] header) throws IOException {
        try {
            DataInputStream data = new DataInputStream(input);
            Date timestamp = new Date(data.readLong() / 1000000);

            double[] metrics = new double[header.length];
            for (int i = 0; i < header.length; i++) {
                metrics[i] = data.readDouble();
            }
            return new Sample(header, timestamp, metrics);
        } catch (EOFException exc) {
            throw new InputStreamClosedException(exc);
        }
    }

    public void marshallHeader(OutputStream output, String[] header) throws IOException {
        for (int i = 0; i < header.length; i++) {
            output.write(header[i].getBytes());
            output.write(lineSepBytes);
        }
        output.write(lineSepBytes);
    }


    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        DataOutputStream data = new DataOutputStream(output);
        double[] values = sample.getMetrics();
        for (int i = 0; i < values.length; i++) {
            data.writeDouble(values[i]);
        }
    }

}
