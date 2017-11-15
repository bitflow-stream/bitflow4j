package bitflow4j;

import bitflow4j.io.SampleReader;
import bitflow4j.io.SampleWriter;
import bitflow4j.io.marshall.*;
import bitflow4j.sample.Header;
import bitflow4j.sample.Sample;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anton on 27.12.16.
 */
public class TestMarshaller extends TestWithSamples {

    private void testAllHeadersDirect(Marshaller marshaller) throws IOException {
        List<Pair<Header, List<Sample>>> headers = createSamples();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        for (Pair<Header, List<Sample>> header : headers) {
            marshaller.marshallHeader(buf, header.getKey());
            for (Sample sample : header.getValue()) {
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
                    Assert.assertNotNull(header);
                    receivedSamples.add(marshaller.unmarshallSample(inBuffer, header));
                }
            } catch (InputStreamClosedException exc) {
                break;
            }
        }

        List<Sample> expected = flatten(headers);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, receivedSamples));
    }

    private void testAllHeaders(Marshaller marshaller) throws IOException {
        List<Pair<Header, List<Sample>>> headers = createSamples();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        SampleWriter printer = new SampleWriter(buf, marshaller);

        for (Pair<Header, List<Sample>> header : headers) {
            for (Sample sample : header.getValue()) {
                printer.writeSample(sample);
            }
        }

        ByteArrayInputStream inBuffer = new ByteArrayInputStream(buf.toByteArray());
        SampleReader reader = SampleReader.singleInput(null, marshaller, "test", inBuffer);

        List<Sample> receivedSamples = new ArrayList<>();
        while (true) {
            Sample sample = reader.nextSample();
            if (sample == null)
                break;
            receivedSamples.add(sample);
        }

        List<Sample> expected = flatten(headers);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, receivedSamples));
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
