package bitflow4j.io.marshall;

import bitflow4j.sample.Header;
import bitflow4j.sample.Sample;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mwall on 30.03.16.
 * <p>
 * Marshaller for dense binary format of Sample data.
 */
public class BinaryMarshaller extends AbstractMarshaller {

    private final String BIN_HEADER_TIME = "timB";
    private final String BIN_HEADER_TAGS = "tags";
    private final byte[] BIN_SAMPLE_START = "X".getBytes(); // Must not collide with BIN_HEADER_TIME, and should be shorter

    public boolean peekIsHeader(InputStream input) throws IOException {
        byte peeked[] = peek(input, BIN_HEADER_TIME.length());
        byte shorterCopy[] = Arrays.copyOf(peeked, BIN_SAMPLE_START.length);
        if (Arrays.equals(shorterCopy, BIN_SAMPLE_START)) {
            return false;
        } else if (Arrays.equals(peeked, BIN_HEADER_TIME.getBytes())) {
            return true;
        } else {
            throw new IOException("Bitflow binary protocol error: Expected '" + new String(BIN_SAMPLE_START) + "' or '" +
                    BIN_HEADER_TIME + "', but got '" + new String(peeked) + "'");
        }
    }

    public UnmarshalledHeader unmarshallHeader(InputStream input) throws IOException {
        List<String> headerList = new ArrayList<>();

        String headerField = readLine(input);
        if (!headerField.equals(BIN_HEADER_TIME)) {
            throw new IllegalArgumentException("First field in binary header must be " + BIN_HEADER_TIME + ". Received: " + headerField);
        }

        while (!(headerField = readLine(input)).isEmpty()) {
            headerList.add(headerField);
        }

        boolean hasTags = headerList.size() >= 1 && headerList.get(0).equals(BIN_HEADER_TAGS);
        if (hasTags)
            headerList = headerList.subList(1, headerList.size());
        String[] header = headerList.toArray(new String[headerList.size()]);
        return new UnmarshalledHeader(new Header(header), hasTags);
    }

    public Sample unmarshallSample(InputStream input, UnmarshalledHeader header) throws IOException {
        try {
            DataInputStream data = new DataInputStream(input);
            readSampleStart(data);

            Date timestamp = new Date(data.readLong() / 1000000);
            String tags = null;
            if (header.hasTags) {
                tags = readLine(input);
            }

            double[] metrics = new double[header.header.numFields()];
            for (int i = 0; i < metrics.length; i++) {
                metrics[i] = data.readDouble();
            }
            return Sample.unmarshallSample(header.header, metrics, timestamp, tags);
        } catch (EOFException exc) {
            throw new InputStreamClosedException(exc);
        }
    }

    protected void readSampleStart(DataInputStream data) throws IOException {
        byte[] sampleStart = new byte[BIN_SAMPLE_START.length];
        data.readFully(sampleStart);
        if (!Arrays.equals(sampleStart, BIN_SAMPLE_START)) {
            throw new IOException("Bitflow binary protocol error: Expected sample start ('" + new String(BIN_SAMPLE_START) +
                    "'), but received '" + new String(sampleStart) + "')");
        }
    }

    public void marshallHeader(OutputStream output, Header header) throws IOException {
        output.write(BIN_HEADER_TIME.getBytes());
        output.write(lineSepBytes_1);

        output.write(BIN_HEADER_TAGS.getBytes());
        output.write(lineSepBytes_1);

        for (String field : header.header) {
            output.write(field.getBytes());
            output.write(lineSepBytes_1);
        }
        output.write(lineSepBytes_1);
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        DataOutputStream data = new DataOutputStream(output);

        Date timestamp = sample.getTimestamp();
        data.write(BIN_SAMPLE_START);
        data.writeLong(timestamp == null ? 0 : timestamp.getTime() * 1000000);

        // Write tags
        String tags = sample.tagString();
        if (tags == null) tags = "";
        data.write(tags.getBytes());
        data.write(lineSepBytes_1);

        double[] values = sample.getMetrics();
        for (double value : values) {
            data.writeDouble(value);
        }
    }

}
