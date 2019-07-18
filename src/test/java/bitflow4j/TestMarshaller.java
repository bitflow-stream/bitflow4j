package bitflow4j;

import bitflow4j.io.SampleInputStream;
import bitflow4j.io.console.SampleWriter;
import bitflow4j.io.marshall.*;
import bitflow4j.misc.Pair;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by anton on 27.12.16.
 */
public class TestMarshaller extends TestWithSamples {

    private void testAllHeadersDirect(Marshaller marshaller) throws IOException {
        List<Pair<Header, List<Sample>>> headers = createSamples();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        for (Pair<Header, List<Sample>> header : headers) {
            marshaller.marshallHeader(buf, header.getLeft());
            for (Sample sample : header.getRight()) {
                marshaller.marshallSample(buf, sample);
            }
        }

        ByteArrayInputStream inBuffer = new ByteArrayInputStream(buf.toByteArray());

        List<Sample> receivedSamples = new ArrayList<>();
        UnmarshalledHeader header = null;
        while (true) {
            try {
                if (marshaller.peekIsHeader(inBuffer)) {
                    header = marshaller.unmarshallHeader(inBuffer);
                } else {
                    assertNotNull(header);
                    receivedSamples.add(marshaller.unmarshallSample(inBuffer, header));
                }
            } catch (InputStreamClosedException exc) {
                break;
            }
        }

        List<Sample> expected = flatten(headers);
        assertTrue(EqualsBuilder.reflectionEquals(expected, receivedSamples));
    }

    private void testAllHeaders(Marshaller marshaller) throws IOException {
        List<Pair<Header, List<Sample>>> headers = createSamples();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        SampleWriter printer = new SampleWriter(buf, marshaller, "buffer");
        printer.setOutgoingSink(new DropStep());

        int x = 0;
        for (Pair<Header, List<Sample>> header : headers) {
            for (Sample sample : header.getRight()) {
                printer.writeSample(sample);
            }
        }

        ByteArrayInputStream inBuffer = new ByteArrayInputStream(buf.toByteArray());
        SampleInputStream reader = new SampleInputStream.Single(inBuffer, "test", null, marshaller);

        Iterator<Sample> expectedSamples = flatten(headers).iterator();

        List<Sample> receivedSamples = new ArrayList<>();
        for (int i = 0; ; i++) {
            Sample sample = reader.nextSample();
            if (sample == null)
                break;
            assertTrue(expectedSamples.hasNext(), "Received more samples than expected");
            Sample expected = expectedSamples.next();

            if (!EqualsBuilder.reflectionEquals(expected, sample)) {
                assertEquals(expected, sample, "Unexpected sample nr " + i);
            }
        }
        assertFalse(expectedSamples.hasNext(), "Received less samples than expected");
    }

    @Test
    public void testBinaryAllHeaders() throws IOException {
        testAllHeaders(new BinaryMarshaller());
    }

    @Test
    public void testCsvAllHeaders() throws IOException {
        testAllHeaders(new CsvMarshaller());
    }

    @Test
    public void testBinaryAllHeadersDirect() throws IOException {
        testAllHeadersDirect(new BinaryMarshaller());
    }

    @Test
    public void testCsvAllHeadersDirect() throws IOException {
        testAllHeadersDirect(new CsvMarshaller());
    }

}
