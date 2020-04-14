package bitflow4j;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Marshaller for dense binary format of Sample data.
 */
public class BinaryMarshaller {

    private final String HEADER_START = "timB";
    private final String HEADER_TAGS = "tags";
    private final String SAMPLE_START = "X";
    private static final String SEPARATOR = "\n";

    private final byte[] HEADER_START_BYTES = HEADER_START.getBytes();
    private final byte[] HEADER_TAGS_BYTES = HEADER_TAGS.getBytes();
    private final byte[] SAMPLE_START_BYTES = SAMPLE_START.getBytes();
    private static final byte[] SEPARATOR_BYTES = SEPARATOR.getBytes();

    private static final byte SEPARATOR_BYTE = SEPARATOR_BYTES[0];

    private static final int READ_LINE_BUFFER = 2048;

    // ===============================
    // Marshalling samples and headers
    // ===============================

    public void marshallHeader(OutputStream output, Header header) throws IOException {
        output.write(HEADER_START_BYTES);
        output.write(SEPARATOR_BYTES);
        output.write(HEADER_TAGS_BYTES);
        output.write(SEPARATOR_BYTES);
        for (String field : header.header) {
            output.write(field.getBytes());
            output.write(SEPARATOR_BYTES);
        }
        output.write(SEPARATOR_BYTES);
    }

    public void marshallSample(OutputStream output, Sample sample) throws IOException {
        DataOutputStream data = new DataOutputStream(output);

        Date timestamp = sample.getTimestamp();
        data.write(SAMPLE_START_BYTES);
        data.writeLong(timestamp == null ? 0 : timestamp.getTime() * 1000000);

        // Write tags
        String tags = sample.tagString();
        if (tags != null)
            data.write(tags.getBytes());
        data.write(SEPARATOR_BYTES);

        double[] values = sample.getMetrics();
        for (double value : values) {
            data.writeDouble(value);
        }
    }

    // =================================
    // Unmarshalling samples and headers
    // =================================

    public boolean peekIsHeader(BufferedInputStream input) throws IOException {
        byte[] peeked;
        try {
            peeked = peek(input, HEADER_START.length());
        } catch (EOFException e) {
            throw new ExpectedEOF(); // Valid EOF at this point
        }

        boolean isSampleStart = Arrays.equals(
                peeked, 0, SAMPLE_START_BYTES.length,
                SAMPLE_START_BYTES, 0, SAMPLE_START_BYTES.length);
        if (isSampleStart) {
            return false;
        } else if (Arrays.equals(peeked, HEADER_START.getBytes())) {
            return true;
        } else {
            throw new BitflowProtocolError(String.format("Bitflow binary protocol error: Expected '%s' or '%s', received: %s" +
                    new String(SAMPLE_START_BYTES), HEADER_START, new String(peeked)));
        }
    }

    public Header unmarshallHeader(BufferedInputStream input) throws IOException {
        byte[] headerField = readLine(input, true);
        if (!Arrays.equals(headerField, HEADER_START_BYTES)) {
            throw new BitflowProtocolError(String.format(
                    "Bitflow binary header must start with '%s'. Received: %s", HEADER_START, new String(headerField)));
        }
        byte[] tagsField = readLine(input, false);
        if (!Arrays.equals(tagsField, HEADER_TAGS_BYTES)) {
            throw new BitflowProtocolError(String.format(
                    "Bitflow binary header must have a second field of '%s'. Received: %s", HEADER_START, new String(tagsField)));
        }

        List<String> headerList = new ArrayList<>();
        while ((headerField = readLine(input, false)).length > 0) {
            headerList.add(new String(headerField));
        }

        return new Header(headerList.toArray(new String[0]));
    }

    public Sample unmarshallSample(BufferedInputStream input, Header header) throws IOException {
        DataInputStream data = new DataInputStream(input);

        byte[] sampleStart;
        try {
            sampleStart = input.readNBytes(SAMPLE_START_BYTES.length);
        } catch (EOFException e) {
            throw new ExpectedEOF();
        }
        if (!Arrays.equals(sampleStart, SAMPLE_START_BYTES)) {
            throw new BitflowProtocolError(String.format(
                    "Bitflow binary protocol error: Expected sample start ('%s'), but received: %s",
                    SAMPLE_START, new String(sampleStart)));
        }

        Date timestamp = new Date(data.readLong() / 1000000);
        byte[] tags = readLine(input, false);

        double[] metrics = new double[header.numFields()];
        for (int i = 0; i < metrics.length; i++) {
            metrics[i] = data.readDouble();
        }
        return new Sample(header, metrics, timestamp, Sample.parseTags(new String(tags)));
    }

    private static byte[] readLine(BufferedInputStream input, boolean expectingEOF) throws IOException {
        input.mark(READ_LINE_BUFFER);

        int num = 0;
        int chr;
        while ((chr = input.read()) != SEPARATOR_BYTE) {
            if (chr < 0) {
                if (num == 0 && expectingEOF) {
                    throw new ExpectedEOF();
                } else {
                    throw new BitflowProtocolError("Unexpected end of input stream");
                }
            }
            num++;
        }

        input.reset();
        return input.readNBytes(num);
    }

    private static byte[] peek(BufferedInputStream input, int numBytes) throws IOException {
        input.mark(numBytes);
        byte[] result = input.readNBytes(numBytes);
        input.reset();
        return result;
    }

}
