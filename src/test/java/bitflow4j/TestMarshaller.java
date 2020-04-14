package bitflow4j;

import bitflow4j.steps.misc.Pair;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
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

    @Test
    private void testMarshaller() throws IOException {
        BinaryMarshaller marshaller = new BinaryMarshaller();
        List<Pair<Header, List<Sample>>> headers = createSamples();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        for (bitflow4j.steps.misc.Pair<Header, List<Sample>> header : headers) {
            marshaller.marshallHeader(buf, header.getLeft());
            for (Sample sample : header.getRight()) {
                marshaller.marshallSample(buf, sample);
            }
        }

        BufferedInputStream inBuffer = new BufferedInputStream(new ByteArrayInputStream(buf.toByteArray()));

        List<Sample> receivedSamples = new ArrayList<>();
        Header header = null;
        while (true) {
            try {
                if (marshaller.peekIsHeader(inBuffer)) {
                    header = marshaller.unmarshallHeader(inBuffer);
                } else {
                    assertNotNull(header);
                    receivedSamples.add(marshaller.unmarshallSample(inBuffer, header));
                }
            } catch (BitflowProtocolError exc) {
                break;
            }
        }

        List<Sample> expected = flatten(headers);
        assertTrue(EqualsBuilder.reflectionEquals(expected, receivedSamples));
    }

    @Test
    private void testMarshallerWithChannel() throws IOException {
        List<Pair<Header, List<Sample>>> headers = createSamples();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        SampleChannel printer = new SampleChannel(null, buf);

        int x = 0;
        for (Pair<Header, List<Sample>> header : headers) {
            for (Sample sample : header.getRight()) {
                printer.writeSample(sample);
            }
        }

        ByteArrayInputStream inBuffer = new ByteArrayInputStream(buf.toByteArray());
        SampleChannel reader = new SampleChannel(inBuffer, null);

        Iterator<Sample> expectedSamples = flatten(headers).iterator();

        List<Sample> receivedSamples = new ArrayList<>();
        for (int i = 0; ; i++) {
            Sample sample = reader.readSample();
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

}
