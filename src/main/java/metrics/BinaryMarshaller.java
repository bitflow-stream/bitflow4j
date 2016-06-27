package metrics;

import metrics.io.InputStreamClosedException;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mwall on 30.03.16.
 * <p>
 * Marshaller for dense binary format of Sample data.
 */
public class BinaryMarshaller extends AbstractMarshaller {

    public Header unmarshallHeader(InputStream input) throws IOException {
        List<String> headerList = new ArrayList<>();

        String headerField;
        while (!(headerField = readLine(input)).isEmpty()) {
            headerList.add(headerField);
        }

        String[] header = headerList.toArray(new String[headerList.size()]);
        Header h = Header.unmarshallHeader(header);
        return h;
    }

    public Sample unmarshallSample(InputStream input, Header header) throws IOException {
        try {
            DataInputStream data = new DataInputStream(input);
            Date timestamp = new Date(data.readLong() / 1000000);
            String tags = null;
            if (header.hasTags) {
                tags = readLine(input);
            }

            double[] metrics = new double[header.header.length];
            for (int i = 0; i < metrics.length; i++) {
                metrics[i] = data.readDouble();
            }
            return Sample.unmarshallSample(header, metrics, timestamp, tags);
        } catch (EOFException exc) {
            throw new InputStreamClosedException(exc);
        }
    }

    public void marshallHeader(OutputStream output, Header header) throws IOException {
        for (String field : header.getSpecialFields()) {
            output.write(field.getBytes());
            output.write(lineSepBytes);
        }
        for (String field : header.header) {
            output.write(field.getBytes());
            output.write(lineSepBytes);
        }
        output.write(lineSepBytes);
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        DataOutputStream data = new DataOutputStream(output);
        Header header = sample.getHeader();
        Date timestamp = sample.getTimestamp();
        data.writeLong(timestamp == null ? 0 : timestamp.getTime() * 1000000);
        if (header.hasTags) {
            String tags = sample.tagString();
            if (tags == null) tags = "";
            data.write(tags.getBytes());
            data.write(lineSepBytes);
        }
        double[] values = sample.getMetrics();
        for (double value : values) {
            data.writeDouble(value);
        }
    }

}
