package metrics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by mwall on 30.03.16.
 * <p>
 * Converts between instances of {@link Sample} and streams of bytes.
 * {@link #marshallHeader(OutputStream, Header) marshallHeader()} and
 * {@link #unmarshallHeader(InputStream) unmarshallHeader()}
 * operate on complete headers, including special fields like {@link Header#HEADER_TIME}.
 */
public interface Marshaller {

    Header unmarshallHeader(InputStream input) throws IOException;

    Sample unmarshallSample(InputStream input, Header unmarshallingHeader,
                            Header sampleHeader) throws IOException;

    void marshallHeader(OutputStream output, Header header) throws IOException;

    void marshallSample(OutputStream output, Sample sample) throws IOException;

}
