package bitflow4j.io.marshall;

import bitflow4j.Header;
import bitflow4j.Sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mwall on 30.03.16.
 * <p>
 * Converts between instances of {@link Sample} and streams of bytes.
 */
public interface Marshaller {

    static Marshaller get(String format) {
        switch (format.toUpperCase()) {
            case CsvMarshaller.FORMAT:
                return new CsvMarshaller();
            case BinaryMarshaller.FORMAT:
                return new BinaryMarshaller();
            case OldBinaryMarshaller.FORMAT:
                return new OldBinaryMarshaller();
            case TextMarshaller.FORMAT:
                return new TextMarshaller();
            default:
                throw new IllegalArgumentException("Unknown marshaller format: " + format);
        }
    }

    /**
     * Peek into the given BufferedInputStream and indicate whether the stream contains
     * a header or a sample. This implies that headers and samples must be clearly distinguishable.
     * The stream is guaranteed to be at a position where either a header or a sample should start.
     * An exception should be thrown, if neither is the case.
     *
     * @param input The buffered byte stream to peek into.
     * @return whether the next
     */
    boolean peekIsHeader(InputStream input) throws IOException;

    /**
     * Receive and parse a Header. This should be the first method to read from the given input stream.
     *
     * @param input The byte stream to read the header from.
     * @return The received header.
     */
    UnmarshalledHeader unmarshallHeader(InputStream input) throws IOException;

    /**
     * Receive and parse a Sample. This assumes, that a header has been
     * previously received on this input stream using unmarshallHeader().
     *
     * @param input  The byte stream to read the sample from.
     * @param header The header that has been previously received on the same input stream.
     * @return The received sample.
     */
    Sample unmarshallSample(InputStream input, UnmarshalledHeader header) throws IOException;

    /**
     * Write the given header to the given output stream. This should be the first method
     * to write to this output stream.
     *
     * @param output The output stream to write the marshalled header to.
     * @param header The header to convert to a byte stream.
     */
    void marshallHeader(OutputStream output, Header header) throws IOException;

    /**
     * Write the given sample to the output stream. This should be called after a header has been written
     * to the same output stream using marshallHeader().
     *
     * @param output The output stream to write the marshalled sample to.
     * @param sample The sample that will be converted to a byte stream.
     */
    void marshallSample(OutputStream output, Sample sample) throws IOException;

}
