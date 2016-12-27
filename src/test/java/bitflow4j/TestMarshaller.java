package bitflow4j;

import bitflow4j.io.InputStreamClosedException;
import bitflow4j.io.MetricPrinter;
import bitflow4j.io.MetricReader;
import javafx.util.Pair;
import org.apache.commons.lang3.builder.EqualsBuilder;
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

        ByteArrayInputStream inbuf = new ByteArrayInputStream(buf.toByteArray());

        List<Sample> receivedSamples = new ArrayList<>();
        Header header = null;
        while (true) {
            try {
                if (marshaller.peekIsHeader(inbuf)) {
                    header = marshaller.unmarshallHeader(inbuf);
                } else {
                    Assert.assertNotNull(header);
                    receivedSamples.add(marshaller.unmarshallSample(inbuf, header));
                }
            } catch (InputStreamClosedException exc) {
                break;
            }
        }

        List<Sample> expected = flatten(headers);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, receivedSamples));
    }

    private void testIndividualHeadersDirect(Marshaller marshaller) throws IOException {

    }

    private void testAllHeaders(Marshaller marshaller) throws IOException {
        List<Pair<Header, List<Sample>>> headers = createSamples();

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        MetricPrinter printer = new MetricPrinter(buf, marshaller);

        for (Pair<Header, List<Sample>> header : headers) {
            for (Sample sample : header.getValue()) {
                printer.writeSample(sample);
            }
        }

        ByteArrayInputStream inbuf = new ByteArrayInputStream(buf.toByteArray());
        MetricReader reader = new MetricReader(inbuf, "test", marshaller);

        List<Sample> receivedSamples = new ArrayList<>();
        while (true) {
            try {
                receivedSamples.add(reader.readSample());
            } catch (InputStreamClosedException exc) {
                break;
            }
        }

        List<Sample> expected = flatten(headers);
        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, receivedSamples));
    }

    private void testIndividualHeaders(Marshaller marshaller) throws IOException {

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
    public void testBinaryIndividualHeaders() throws IOException {
        testIndividualHeaders(new BinaryMarshaller());
    }

    @Test
    public void testCsvIndividualHeaders() throws IOException {
        testIndividualHeaders(new CsvMarshaller());
    }

    @Test
    public void testBinaryAllHeadersDirect() throws IOException {
        testAllHeadersDirect(new BinaryMarshaller());
    }

    @Test
    public void testCsvAllHeadersDirect() throws IOException {
        testAllHeadersDirect(new CsvMarshaller());
    }

    @Test
    public void testBinaryIndividualHeadersDirect() throws IOException {
        testIndividualHeadersDirect(new BinaryMarshaller());
    }

    @Test
    public void testCsvIndividualHeadersDirect() throws IOException {
        testIndividualHeadersDirect(new CsvMarshaller());
    }

}
