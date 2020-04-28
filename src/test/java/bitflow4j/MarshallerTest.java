package bitflow4j;

import bitflow4j.steps.misc.Pair;
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
public class MarshallerTest {

    @Test
    public void testMarshaller() throws IOException {
        BinaryMarshaller marshaller = new BinaryMarshaller();
        List<Pair<Header, List<Sample>>> headers = Helpers.createSamples();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        for (Pair<Header, List<Sample>> header : headers) {
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
            } catch (ExpectedEOF exc) {
                break;
            }
        }

        List<Sample> expected = Helpers.flatten(headers);
        assertArrayEquals(expected.toArray(), receivedSamples.toArray());
    }

    @Test
    public void testChannel() throws IOException {
        List<Pair<Header, List<Sample>>> headers = Helpers.createSamples();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        SampleChannel printer = new SampleChannel(null, buf);

        for (Pair<Header, List<Sample>> header : headers) {
            for (Sample sample : header.getRight()) {
                printer.writeSample(sample);
            }
        }

        ByteArrayInputStream inBuffer = new ByteArrayInputStream(buf.toByteArray());
        SampleChannel reader = new SampleChannel(inBuffer, null);

        Iterator<Sample> expectedSamples = Helpers.flatten(headers).iterator();

        List<Sample> receivedSamples = new ArrayList<>();
        for (int i = 0; ; i++) {
            Sample sample = reader.readSample();
            if (sample == null)
                break;
            assertTrue(expectedSamples.hasNext(), "Received more samples than expected");
            Sample expected = expectedSamples.next();

            assertEquals(expected, sample, "Unexpected sample nr " + i);
        }
        assertFalse(expectedSamples.hasNext(), "Received less samples than expected");
    }

}
