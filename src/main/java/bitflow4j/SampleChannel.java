package bitflow4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SampleChannel {

    private final BinaryMarshaller marshaller = new BinaryMarshaller();
    private final BufferedInputStream input;
    private final OutputStream output;

    private Header inputHeader = null;
    private final Header.ChangeChecker outputHeader = new Header.ChangeChecker();

    public SampleChannel(InputStream input, OutputStream output) {
        this.input = new BufferedInputStream(input);
        this.output = output;
    }

    public void close() {
        // TODO necessary/possible to close System.in and System.out?
    }

    public void writeSample(Sample sample) throws IOException {
        if (outputHeader.changed(sample.getHeader())) {
            marshaller.marshallHeader(output, sample.getHeader());
        }
        marshaller.marshallSample(output, sample);
        output.flush();
    }

    public Sample readSample() throws IOException {
        while (true) {
            if (inputHeader == null || marshaller.peekIsHeader(input)) {
                inputHeader = marshaller.unmarshallHeader(input);
            } else {
                return marshaller.unmarshallSample(input, inputHeader);
            }
        }
    }

}
