package bitflow4j.steps.query;

import bitflow4j.Header;
import bitflow4j.Sample;
import bitflow4j.io.list.ListSink;
import bitflow4j.steps.query.exceptions.IllegalStreamingQueryException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("The query script language is currently not further developed")
public class TestsForV4 {

    /**
     * Test for the in key word in Where part of query
     */
    @Test
    public void test1() throws IOException {
        Date d = new Date();

        // define query
        String query = "SeLect * Where CPU in {1,105,2000} OR RAM in {20,50}";
        StreamingQuery sq;

        ListSink sink = new ListSink();
        sq = new StreamingQuery(query);
        sq.setOutgoingSink(sink);

        // Input 1
        String[] input_1_strings = {"CPU", "RAM"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {1, 100};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"CPU", "RAM"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {1, 100};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"RAM", "CPU"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {50, 2000};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"RAM", "CPU"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {50, 2000};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        // Input 3
        String[] input_3_strings = {"RAM", "CPU", "MEM"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {3, 3, 50};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        sq.writeSample(inputSample_3);
        // Expected Output
        assertEquals(2, sink.samples.size());

        // Input 4
        String[] input_4_strings = {"a", "CPU", "b", "c", "RAM"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {0.5, 7, 123, 20000, 20};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        sq.writeSample(inputSample_4);
        // Expected Output 4
        String[] output_4_strings = {"a", "CPU", "b", "c", "RAM"};
        Header header_output_4 = new Header(output_4_strings);
        double[] values_output_4 = {0.5, 7, 123, 20000, 20};
        Sample expectedOutputSample_4 = new Sample(header_output_4, values_output_4, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4, sink.samples.get(2)));
    }

    /**
     * Test for the in key word in Having part of query
     */
    @Test
    public void test2() throws IOException {
        Date d = new Date();

        // define query
        String query = "SELECT cpu*2 AS CPU_OUTPUT Having CPU_OUTPUT iN {10,100,1002}";
        StreamingQuery sq;

        ListSink sink = new ListSink();
        sq = new StreamingQuery(query);
        sq.setOutgoingSink(sink);

        // Input 1
        String[] input_1_strings = {"cpu"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {4};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        assertEquals(0, sink.samples.size());

        // Input 2
        String[] input_2_strings = {"cpu"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {5};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"CPU_OUTPUT"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {10};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(0)));

        // Input 3
        String[] input_3_strings = {"RAM", "cpu", "MEM"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {3, 50, 580};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        sq.writeSample(inputSample_3);
        // Expected Output 3
        String[] output_3_strings = {"CPU_OUTPUT"};
        Header header_output_3 = new Header(output_3_strings);
        double[] values_output_3 = {100};
        Sample expectedOutputSample_3 = new Sample(header_output_3, values_output_3, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3, sink.samples.get(1)));

        // Input 4
        String[] input_4_strings = {"a", "cpu", "b", "c", "RAM"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {0.5, 501, 123, 20000, 20};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        sq.writeSample(inputSample_4);
        // Expected Output 4
        String[] output_4_strings = {"CPU_OUTPUT"};
        Header header_output_4 = new Header(output_4_strings);
        double[] values_output_4 = {1002};
        Sample expectedOutputSample_4 = new Sample(header_output_4, values_output_4, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4, sink.samples.get(2)));

        // Input 5
        String[] input_5_strings = {"a", "cpu", "b", "c", "RAM"};
        Header header_input_5 = new Header(input_5_strings);
        double[] values_input_5 = {0.5, 77, 123, 20000, 20};
        Sample inputSample_5 = new Sample(header_input_5, values_input_5, d);
        sq.writeSample(inputSample_5);
        // Expected Output 5
        assertEquals(3, sink.samples.size());
    }

    /**
     * Test for the in key word in with tags in the where part
     */
    @Test
    public void test3() throws IOException {

        Date d = new Date();

        // define query
        String query = "SELECT * Where tag(host) in {foo,gulenko}";
        StreamingQuery sq;

        ListSink sink = new ListSink();
        sq = new StreamingQuery(query);
        sq.setOutgoingSink(sink);

        // Input 1
        String[] input_1_strings = {"a"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {15};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        inputSample_1.setTag("host", "foo");
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"a"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {15};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        expectedOutputSample_1.setTag("host", "foo");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"b"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {55};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        inputSample_2.setTag("host", "gulenko");
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"b"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {55};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        expectedOutputSample_2.setTag("host", "gulenko");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        // Input 3
        String[] input_3_strings = {"RAM", "CPU", "MEM"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {3, 50, 580};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        inputSample_3.setTag("IP", "123.456.789.123");
        sq.writeSample(inputSample_3);
        // Expected Output 3
        assertEquals(2, sink.samples.size());

        // Input 4
        String[] input_4_strings = {"a", "CPU", "b", "c", "Memory"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {0.5, 501, 123, 20000, 20};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        sq.writeSample(inputSample_4);
        // Expected Output 4
        assertEquals(2, sink.samples.size());
    }

    /**
     * Test for the countFunction with 2 tags
     */
    @Test
    public void test4() throws IOException {

        Date d = new Date();

        // define query
        String query = "Select Count(host,IP) as Count2Tags Window all";
        StreamingQuery sq;

        ListSink sink = new ListSink();
        sq = new StreamingQuery(query);
        sq.setOutgoingSink(sink);

        // Input 1
        String[] input_1_strings = {"a"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {15};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        inputSample_1.setTag("host", "foo");
        inputSample_1.setTag("IP", "1");
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"Count2Tags"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {1};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        expectedOutputSample_1.setTag("host", "foo");
        expectedOutputSample_1.setTag("IP", "1");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"b"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {55};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        inputSample_2.setTag("host", "foo");
        inputSample_2.setTag("IP", "1");
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"Count2Tags"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {1};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        expectedOutputSample_2.setTag("host", "foo");
        expectedOutputSample_2.setTag("IP", "1");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        // Input 3
        String[] input_3_strings = {"RAM", "CPU", "MEM"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {3, 50, 580};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        inputSample_3.setTag("host", "foo");
        inputSample_3.setTag("IP", "2");
        sq.writeSample(inputSample_3);
        // Expected Output 3
        String[] output_3_strings = {"Count2Tags"};
        Header header_output_3 = new Header(output_3_strings);
        double[] values_output_3 = {2};
        Sample expectedOutputSample_3 = new Sample(header_output_3, values_output_3, d);
        expectedOutputSample_3.setTag("host", "foo");
        expectedOutputSample_3.setTag("IP", "2");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3, sink.samples.get(2)));

        // Input 4
        String[] input_4_strings = {"RAM", "CPU", "MEM"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {4, 50, 580};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        inputSample_4.setTag("host", "foo");
        inputSample_4.setTag("IP", "2");
        sq.writeSample(inputSample_4);
        // Expected Output 4
        String[] output_4_strings = {"Count2Tags"};
        Header header_output_4 = new Header(output_4_strings);
        double[] values_output_4 = {2};
        Sample expectedOutputSample_4 = new Sample(header_output_4, values_output_4, d);
        expectedOutputSample_4.setTag("host", "foo");
        expectedOutputSample_4.setTag("IP", "2");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4, sink.samples.get(3)));

        // Input 5
        String[] input_5_strings = {"RAM", "CPU", "MEM"};
        Header header_input_5 = new Header(input_5_strings);
        double[] values_input_5 = {5, 50, 580};
        Sample inputSample_5 = new Sample(header_input_5, values_input_5, d);
        inputSample_5.setTag("host", "foo");
        sq.writeSample(inputSample_5);
        // Expected Output 5
        String[] output_5_strings = {"Count2Tags"};
        Header header_output_5 = new Header(output_5_strings);
        double[] values_output_5 = {3};
        Sample expectedOutputSample_5 = new Sample(header_output_5, values_output_5, d);
        expectedOutputSample_5.setTag("host", "foo");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_5, sink.samples.get(4)));
    }

    /**
     * Test if we can group by without window
     */
    @Test
    public void test5() throws IllegalStreamingQueryException {
        assertThrows(IllegalStreamingQueryException.class, () ->
                new StreamingQuery("Select * Where CPU>1 Group by host"));
    }

    /**
     * Test of the tag(t) = "value" function for where part
     */
    @Test
    public void test6() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select * Where b>1 AND tag(host) = \"foo\"";
        StreamingQuery sq;

        ListSink sink = new ListSink();
        sq = new StreamingQuery(query);
        sq.setOutgoingSink(sink);

        // Input 1
        String[] input_1_strings = {"a", "b", "c"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {1, 2, 3};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        inputSample_1.setTag("host", "foo");
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"a", "b", "c"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {1, 2, 3};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        expectedOutputSample_1.setTag("host", "foo");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"b"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {55};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        inputSample_2.setTag("host", "gulenko");
        inputSample_2.setTag("IP", "1");
        sq.writeSample(inputSample_2);
        // Expected Output 2
        assertEquals(1, sink.samples.size());

        // Input 3
        String[] input_3_strings = {"b", "CPU", "MEM"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {3, 50, 580};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        sq.writeSample(inputSample_3);
        // Expected Output 3
        assertEquals(1, sink.samples.size());
    }

    /**
     * Test where calculateOutputIfWhereIsFalse of the StreamingQuery is false
     */
    @Test
    public void test7() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select Memory , Sum(Memory) Where Memory>10 Window all";
        StreamingQuery sq;

        ListSink sink = new ListSink();
        sq = new StreamingQuery(query, false);
        sq.setOutgoingSink(sink);

        // Input 1
        String[] input_1_strings = {"Memory"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {50};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"Memory", "Sum(Memory)"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {50, 50};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"Memory"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {100};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"Memory", "Sum(Memory)"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {100, 150};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        // Input 3
        String[] input_3_strings = {"Memory"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {4};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        sq.writeSample(inputSample_3);
        // Expected Output 3
        assertEquals(2, sink.samples.size());

        // change output calculation to true, now the receiver gets a sample
        // for input 3
        sq.setCalculateOutputIfWhereIsFalse(true);
        sq.writeSample(inputSample_3);
        assertEquals(3, sink.samples.size());
    }

}
