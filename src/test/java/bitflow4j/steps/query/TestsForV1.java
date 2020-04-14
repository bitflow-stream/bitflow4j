package bitflow4j.steps.query;

import bitflow4j.Header;
import bitflow4j.MockContext;
import bitflow4j.Sample;
import bitflow4j.steps.query.exceptions.IllegalStreamingQueryException;
import bitflow4j.steps.query.exceptions.MissingMetricException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that must run without error after version 1 of the program.
 * <p>
 * Output samples are stored in a list in the helper class MockContext. This
 * list is used to compare calculated and expected output samples.
 * <p>
 * Reset list before new tests.
 */
@Disabled("The query script language is currently not further developed")
public class TestsForV1 {

    /**
     * test1 tests the Select * function.
     */
    @Test
    public void test1() throws IOException {

        Date d = new Date();

        // define query
        String query = "SeLect *";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"cpu", "hosts", "memory"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {20, 40, 30};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"cpu", "hosts", "memory"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {20, 40, 30};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"CPU", "server", "ram", "Abc"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {0, 1000, 0.5, 3.334};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"CPU", "server", "ram", "Abc"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {0, 1000, 0.5, 3.334};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        query = "Select ALL Where cpu=2";
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 3
        String[] input_3_strings = {"hosts", "cpu", "memory"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {111, 2, 0.5681};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        sq.writeSample(inputSample_3);
        // Expected Output 3
        String[] output_3_strings = {"hosts", "cpu", "memory"};
        Header header_output_3 = new Header(output_3_strings);
        double[] values_output_3 = {111, 2, 0.5681};
        Sample expectedOutputSample_3 = new Sample(header_output_3, values_output_3, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3, sink.samples.get(2)));
    }

    /**
     * Test for selecting metrics with Select keyword, with and without renaming
     * with As.
     */
    @Test
    public void test2() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select cpu, mem as memory ";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"cpu", "mem", "temp", "ram"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {2, 3, 20, 0.5};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"cpu", "memory"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {2.0, 3.0};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"mem", "cpu", "ram"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {20, 33, 84};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"cpu", "memory"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {33, 20};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));
    }

    /**
     * Test for logical expressions in the where part of query.
     */
    @Test
    public void test3() throws IOException {

        Date d = new Date();

        // define query
        String query = "Select cpu, mem As memory WHERE (cpu>=0.4 aNd mem=0.2) oR noT mem>=0.3";
        StreamingQuery sq;
        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"cpu", "mem", "temp", "ram"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {2, 0.2, 20, 0.5};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"cpu", "memory"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {2, 0.2};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"cpu", "temp", "mem"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {0.1, 500, 0.29};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"cpu", "memory"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2;
        values_output_2 = new double[]{0.1, 0.29};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        // Input 3
        String[] input_3_strings = {"cpu", "mem"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3;
        values_input_3 = new double[]{0.3, 0.4};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        sq.writeSample(inputSample_3);
        // Expected Output 2
        // We expect no output sample for this input, so the list must still
        // have 2 samples
        int listSize = sink.samples.size();
        assertEquals(2, listSize);

        // define new query
        query = "Select ram As Ram, mem As Memory, cpu Where NOT(mem<=1 OR mem>=5) AND cpu=3 AND ram=4";
        sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 4
        String[] input_4_strings = {"ram", "cpu", "temp", "mem"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {4, 3, 20, 2.5};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        sq.writeSample(inputSample_4);
        // Expected Output 4
        String[] output_4_strings = {"Ram", "Memory", "cpu"};
        Header header_output_4 = new Header(output_4_strings);
        double[] values_output_4 = {4, 2.5, 3};
        Sample expectedOutputSample_4 = new Sample(header_output_4, values_output_4, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4, sink.samples.get(0)));

        // Input 5
        String[] input_5_strings = {"A", "ram", "cpu", "D", "temp", "mem"};
        Header header_input_5 = new Header(input_5_strings);
        double[] values_input_5 = {100, 4, 3, 203, 22, 0.5};
        Sample inputSample_5 = new Sample(header_input_5, values_input_5, d);
        sq.writeSample(inputSample_5);
        // Expected Output 5
        assertEquals(1, sink.samples.size());
    }

    /**
     * Test to produce IllegalStreamingQueryExceptions
     */
    @Test
    public void test4_1() throws IllegalStreamingQueryException {
        assertThrows(IllegalStreamingQueryException.class, () -> new StreamingQuery("Select ** "));
    }

    @Test
    public void test4_2() throws IllegalStreamingQueryException {
        assertThrows(IllegalStreamingQueryException.class, () -> new StreamingQuery("Select * Fromm input"));
    }

    @Test
    public void test4_3() throws IllegalStreamingQueryException {
        assertThrows(IllegalStreamingQueryException.class, () -> new StreamingQuery("Select * Where (cpu=2"));
    }

    /**
     * Test for MissingMetricException.
     */
    @Test
    public void test5() throws IOException {
        assertThrows(MissingMetricException.class, () -> {
            // define query
            String query = "Select cpu, mem As memory";
            StreamingQuery sq;
            MockContext sink = new MockContext();
            sq = new StreamingQuery(query);
            sq.initialize(sink);

            // Input 1
            String[] input_1_strings = {"cpu", "temp", "ram"};
            Header header_input_1 = new Header(input_1_strings);
            double[] values_input_1 = {2, 3, 0.5};
            Sample inputSample_1 = new Sample(header_input_1, values_input_1, new Date());
            sq.writeSample(inputSample_1);
        });
    }

    /**
     * Test for mathematically expressions in select part.
     */
    @Test
    public void test6() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select cpu*2 As TwoCpu, mem+cpu As MemCpu, ((cpu+cpu)+mem)*2";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"cpu", "mem", "temp", "ram"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {5.5, 10, 20, 0.5};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        inputSample_1.setTag("host", "foo");

        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"TwoCpu", "MemCpu", "((cpu+cpu)+mem)*2"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {11, 15.5, 42};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        expectedOutputSample_1.setTag("host", "foo");

        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        query = "Select power As Power, power*2, power+2, power- 2, power/2, ((power+power)*(power*ram)+3)*ram Where NOT power=2 AND ((power<100 AND power<=40 AND a>=4 AND b>10) OR e=100)";
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 2
        String[] input_2_strings = {"power", "ram", "cpu", "a", "b", "c", "d", "e", "Memory"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {10, 20, 100, 11.11, 13.387, 1, 0, 0.5, 12500.3491};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"Power", "power*2", "power+2", "power-2", "power/2", "((power+power)*(power*ram)+3)*ram"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {10, 20, 12, 8, 5, 80060};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));
    }

    /**
     * Test with the constants TRUE and FALSE.
     */
    @Test
    public void test7() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select cpu As CPU, ram Where TRUE OR FALSE";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"cpu", "ram"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {2, 10};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"CPU", "ram"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {2, 10};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        query = "Select cpu As CPU, ram Where TRUE AND FALSE";
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 2
        String[] input_2_strings = {"cpu", "ram"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {2, 10};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        int listSize = sink.samples.size();
        assertEquals(1, listSize);

        query = "Select mem As Memory, ram Where TRUE AND TRUE";
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 3
        String[] input_3_strings = {"mem", "temp", "ram"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {200, 10, 11};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        sq.writeSample(inputSample_3);
        // Expected Output 3
        String[] output_3_strings = {"Memory", "ram"};
        Header header_output_3 = new Header(output_3_strings);
        double[] values_output_3 = {200, 11};
        Sample expectedOutputSample_3 = new Sample(header_output_3, values_output_3, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3, sink.samples.get(1)));

        query = "Select cpu As CPU, ram Where FALSE AND FALSE";
        sq = new StreamingQuery(query);

        // Input 4
        String[] input_4_strings = {"cpu", "ram"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {2, 10};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        sq.writeSample(inputSample_4);
        // Expected Output 2
        listSize = sink.samples.size();
        assertEquals(2, listSize);
    }

    /**
     * test for some querys like metric1 > metric2 or 3.0>metric3
     */
    @Test
    public void test8() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select * Where (a>b AND C=c) OR 3.0>e";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"a", "b", "c", "C", "e"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {5, 3, 10, 10, 100};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"a", "b", "c", "C", "e"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {5, 3, 10, 10, 100};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"a", "b", "c", "C", "e"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {5, 3, 10, 11, 2};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"a", "b", "c", "C", "e"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {5, 3, 10, 11, 2};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        // Input 3
        String[] input_3_strings = {"a", "b", "c", "C", "e"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {11, 11, 10, 11, 50.5};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        sq.writeSample(inputSample_3);
        // Expected Output 3
        assertEquals(2, sink.samples.size());
    }
}
