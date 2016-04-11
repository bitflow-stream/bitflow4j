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

    public Sample.Header unmarshallHeader(InputStream input) throws IOException {
        List<String> headerList = new ArrayList<>();

        String headerField;
        while (!(headerField = readLine(input)).isEmpty()) {
            headerList.add(headerField);
        }

        String[] header = headerList.toArray(new String[headerList.size()]);
        return new Sample.Header(header);
    }

    public Sample unmarshallSample(InputStream input, Sample.Header header) throws IOException {
        DataInputStream data = new DataInputStream(input);
        Date timestamp = null;
        String source = null;
        String label = null;
        try {
            if (header.hasTimestamp()) {
                timestamp = new Date(data.readLong() / 1000000);
            }
            if (header.hasSource()) {
                source = readLine(input);
            }
            if (header.hasLabel()) {
                label = readLine(input);
            }

            double[] metrics = new double[header.header.length];
            for (int i = 0; i < metrics.length; i++) {
                metrics[i] = data.readDouble();
            }
            return new Sample(header, metrics, timestamp, source, label);
        } catch (EOFException exc) {
            throw new InputStreamClosedException(exc);
        }
    }

    public void marshallHeader(OutputStream output, Sample.Header header) throws IOException {
        for (String field : header.getSpecialFields()) {
            output.write(lineSepBytes);
            output.write(field.getBytes());
        }
        for (String field : header.header) {
            output.write(lineSepBytes);
            output.write(field.getBytes());
        }
        output.write(lineSepBytes);
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        DataOutputStream data = new DataOutputStream(output);
        Sample.Header header = sample.getHeader();
        if (header.hasTimestamp()) {
            Date timestamp = sample.getTimestamp();
            data.writeLong(timestamp == null ? 0 : timestamp.getTime());
        }
        if (header.hasSource()) {
            String source = sample.getSource();
            if (source == null) source = "";
            data.write(source.getBytes());
            data.write(lineSepBytes);
        }
        if (header.hasLabel()) {
            String label = sample.getLabel();
            if (label == null) label = "";
            data.write(label.getBytes());
            data.write(lineSepBytes);
        }

        double[] values = sample.getMetrics();
        for (int i = 0; i < values.length; i++) {
            data.writeDouble(values[i]);
        }
    }

}
