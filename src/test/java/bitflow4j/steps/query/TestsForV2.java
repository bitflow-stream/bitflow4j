package bitflow4j.steps.query;

import bitflow4j.Header;
import bitflow4j.MockContext;
import bitflow4j.Sample;
import bitflow4j.steps.query.exceptions.IllegalStreamingQueryException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that must run without error after version 2 of the program.
 */
@Disabled("The query script language is currently not further developed")
public class TestsForV2 {

    /**
     * Test for the Sum() function and window value mode.
     */
    @Test
    public void test1() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select Sum(Cpu) Where Cpu>10 Window 3";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"Cpu", "hosts", "memory"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {20, 40, 30};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"Sum(Cpu)"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {20};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"Cpu", "hosts", "memory"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {50, 40, 30};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"Sum(Cpu)"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {70};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        // Input 3
        String[] input_3_strings = {"Cpu", "hosts"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {100, 40};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        sq.writeSample(inputSample_3);
        // Expected Output 3
        String[] output_3_strings = {"Sum(Cpu)"};
        Header header_output_3 = new Header(output_3_strings);
        double[] values_output_3 = {170};
        Sample expectedOutputSample_3 = new Sample(header_output_3, values_output_3, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3, sink.samples.get(2)));

        // Input 4
        String[] input_4_strings = {"Cpu", "hosts"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {25, 40};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        sq.writeSample(inputSample_4);
        // Expected Output 4
        String[] output_4_strings = {"Sum(Cpu)"};
        Header header_output_4 = new Header(output_4_strings);
        double[] values_output_4 = {175};
        Sample expectedOutputSample_4 = new Sample(header_output_4, values_output_4, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4, sink.samples.get(3)));

        // Input 5
        String[] input_5_strings = {"hosts", "Cpu"};
        Header header_input_5 = new Header(input_5_strings);
        double[] values_input_5 = {50, 5};
        Sample inputSample_5 = new Sample(header_input_5, values_input_5, d);
        sq.writeSample(inputSample_5);
        // Expected Output 5
        String[] output_5_strings = {"Sum(Cpu)"};
        Header header_output_5 = new Header(output_5_strings);
        double[] values_output_5 = {175};
        Sample expectedOutputSample_5 = new Sample(header_output_5, values_output_5, d);

        if (sq.getCalculateOutputIfWhereIsFalse()) {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_5, sink.samples.get(4)));
        } else {
            assertEquals(4, sink.samples.size());
        }

        // Input 6
        String[] input_6_strings = {"Cpu", "hosts"};
        Header header_input_6 = new Header(input_6_strings);
        double[] values_input_6 = {1000.54, 45};
        Sample inputSample_6 = new Sample(header_input_6, values_input_6, d);
        sq.writeSample(inputSample_6);
        // Expected Output 6
        String[] output_6_strings = {"Sum(Cpu)"};
        Header header_output_6 = new Header(output_6_strings);
        double[] values_output_6 = {1125.54};
        Sample expectedOutputSample_6 = new Sample(header_output_6, values_output_6, d);

        if (sq.getCalculateOutputIfWhereIsFalse()) {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_6, sink.samples.get(5)));
        } else {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_6, sink.samples.get(4)));
        }
    }

    /**
     * Test the window all mode in combination with all aggregation functions.
     */
    @Test
    public void test2() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select min(a) As min, MAX(a) As max, AvG(a), meDiAn(a), cOUNT(*) As count, sUm(a) As Summe Where a>1 Window all";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"a", "b"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {5, 10};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"min", "max", "Avg(a)", "Median(a)", "count", "Summe"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {5, 5, 5, 5, 1, 5};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"b", "a"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {100, 1};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"min", "max", "Avg(a)", "Median(a)", "count", "Summe"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {5, 5, 5, 5, 1, 5};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);

        if (sq.getCalculateOutputIfWhereIsFalse()) {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));
        } else {
            assertEquals(1, sink.samples.size());
        }

        // Input 3
        String[] input_3_strings = {"a"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {10};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        sq.writeSample(inputSample_3);
        // Expected Output 3
        String[] output_3_strings = {"min", "max", "Avg(a)", "Median(a)", "count", "Summe"};
        Header header_output_3 = new Header(output_3_strings);
        double[] values_output_3 = {5, 10, 7.5, 7.5, 2, 15};
        Sample expectedOutputSample_3 = new Sample(header_output_3, values_output_3, d);

        if (sq.getCalculateOutputIfWhereIsFalse()) {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_3, sink.samples.get(2)));
        } else {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_3, sink.samples.get(1)));
        }

        // Input 4
        String[] input_4_strings = {"b", "d", "a"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {12, 0.5, 20};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        sq.writeSample(inputSample_4);
        // Expected Output 4
        String[] output_4_strings = {"min", "max", "Avg(a)", "Median(a)", "count", "Summe"};
        Header header_output_4 = new Header(output_4_strings);
        double avg = (5.0 + 10.0 + 20.0) / 3.0;
        double[] values_output_4 = {5, 20, avg, 10, 3, 35};
        Sample expectedOutputSample_4 = new Sample(header_output_4, values_output_4, d);

        if (sq.getCalculateOutputIfWhereIsFalse()) {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_4, sink.samples.get(3)));
        } else {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_4, sink.samples.get(2)));
        }

        // Input 5
        String[] input_5_strings = {"a"};
        Header header_input_5 = new Header(input_5_strings);
        double[] values_input_5 = {2};
        Sample inputSample_5 = new Sample(header_input_5, values_input_5, d);
        sq.writeSample(inputSample_5);
        // Expected Output 5
        String[] output_5_strings = {"min", "max", "Avg(a)", "Median(a)", "count", "Summe"};
        Header header_output_5 = new Header(output_5_strings);
        avg = (5.0 + 10.0 + 20.0 + 2.0) / 4.0;
        double[] values_output_5 = {2, 20, avg, 7.5, 4, 37};
        Sample expectedOutputSample_5 = new Sample(header_output_5, values_output_5, d);

        if (sq.getCalculateOutputIfWhereIsFalse()) {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_5, sink.samples.get(4)));
        } else {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_5, sink.samples.get(3)));
        }
    }

    /**
     * Test for the window time mode.
     */
    @Test
    public void test3() throws IOException, InterruptedException {
        Date d = new Date();

        // define query
        String query = "Select Count(*) As c Where CPU>1.5 Window 10 s";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"CPU"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {100};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"c"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {1};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        System.out.println("Sleep 5 seconds");
        Thread.sleep(5000);

        // Input 2
        d = new Date();
        String[] input_2_strings = {"CPU"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {5000};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"c"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {2};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        // Input 3
        d = new Date();
        String[] input_3_strings = {"CPU"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {33};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        sq.writeSample(inputSample_3);
        // Expected Output 3
        String[] output_3_strings = {"c"};
        Header header_output_3 = new Header(output_3_strings);
        double[] values_output_3 = {3};
        Sample expectedOutputSample_3 = new Sample(header_output_3, values_output_3, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3, sink.samples.get(2)));

        System.out.println("Sleep 6 seconds");
        Thread.sleep(6000);

        // Input 4
        d = new Date();
        String[] input_4_strings = {"CPU"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {55};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        sq.writeSample(inputSample_4);
        // Expected Output 4
        String[] output_4_strings = {"c"};
        Header header_output_4 = new Header(output_4_strings);
        double[] values_output_4 = {3};
        Sample expectedOutputSample_4 = new Sample(header_output_4, values_output_4, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4, sink.samples.get(3)));

        System.out.println("Sleep 11 seconds");
        Thread.sleep(11000);

        // Input 5
        d = new Date();
        String[] input_5_strings = {"CPU"};
        Header header_input_5 = new Header(input_5_strings);
        double[] values_input_5 = {2};
        Sample inputSample_5 = new Sample(header_input_5, values_input_5, d);
        sq.writeSample(inputSample_5);
        // Expected Output 5
        String[] output_5_strings = {"c"};
        Header header_output_5 = new Header(output_5_strings);
        double[] values_output_5 = {1};
        Sample expectedOutputSample_5 = new Sample(header_output_5, values_output_5, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_5, sink.samples.get(4)));
    }

    /**
     * Test if some illegal querys are recognised as illegal.
     */
    @Test
    public void test4_1() throws IllegalStreamingQueryException {
        assertThrows(IllegalStreamingQueryException.class, () ->
                new StreamingQuery("Select Cpu Where CPU>1.5 Window 10 s 1 d"));
    }

    @Test
    public void test4_2() throws IllegalStreamingQueryException {
        assertThrows(IllegalStreamingQueryException.class, () ->
                new StreamingQuery("Select Cpu Where CPU>1.5 Window 1 d 1 h 1 s 1 m"));
    }

    @Test
    public void test4_3() throws IllegalStreamingQueryException {
        assertThrows(IllegalStreamingQueryException.class, () ->
                new StreamingQuery("Select Sum(Cpu)"));
    }

    /**
     * Test for the count function - with and without a tag.
     */
    @Test
    public void test5() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select Count(*) As c, Count(host) As hosts Where CPU>1.5 Window all";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"CPU"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {100};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        inputSample_1.setTag("host", "a");
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"c", "hosts"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {1, 1};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        expectedOutputSample_1.setTag("host", "a");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"CPU"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {2};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        inputSample_2.setTag("host", "a");
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"c", "hosts"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {2, 1};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        expectedOutputSample_2.setTag("host", "a");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        // Input 3
        String[] input_3_strings = {"CPU"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {111};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        inputSample_3.setTag("host", "b");
        sq.writeSample(inputSample_3);
        // Expected Output 3
        String[] output_3_strings = {"c", "hosts"};
        Header header_output_3 = new Header(output_3_strings);
        double[] values_output_3 = {3, 2};
        Sample expectedOutputSample_3 = new Sample(header_output_3, values_output_3, d);
        expectedOutputSample_3.setTag("host", "b");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3, sink.samples.get(2)));

        // Input 4
        String[] input_4_strings = {"CPU"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {2222};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        inputSample_4.setTag("host", "b");
        sq.writeSample(inputSample_4);
        // Expected Output 4
        String[] output_4_strings = {"c", "hosts"};
        Header header_output_4 = new Header(output_4_strings);
        double[] values_output_4 = {4, 2};
        Sample expectedOutputSample_4 = new Sample(header_output_4, values_output_4, d);
        expectedOutputSample_4.setTag("host", "b");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4, sink.samples.get(3)));

        // Input 5
        String[] input_5_strings = {"CPU"};
        Header header_input_5 = new Header(input_5_strings);
        double[] values_input_5 = {2222};
        Sample inputSample_5 = new Sample(header_input_5, values_input_5, d);
        inputSample_5.setTag("host", "c");
        sq.writeSample(inputSample_5);
        // Expected Output 5
        String[] output_5_strings = {"c", "hosts"};
        Header header_output_5 = new Header(output_5_strings);
        double[] values_output_5 = {5, 3};
        Sample expectedOutputSample_5 = new Sample(header_output_5, values_output_5, d);
        expectedOutputSample_5.setTag("host", "c");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_5, sink.samples.get(4)));

        // Input 6
        String[] input_6_strings = {"CPU"};
        Header header_input_6 = new Header(input_6_strings);
        double[] values_input_6 = {1.4};
        Sample inputSample_6 = new Sample(header_input_6, values_input_6, d);
        inputSample_6.setTag("host", "c");
        inputSample_6.setTag("ip", "123");
        sq.writeSample(inputSample_6);
        // Expected Output 6
        String[] output_6_strings = {"c", "hosts"};
        Header header_output_6 = new Header(output_6_strings);
        double[] values_output_6 = {6, 3};
        Sample expectedOutputSample_6 = new Sample(header_output_6, values_output_6, d);
        expectedOutputSample_6.setTag("host", "c");
        inputSample_6.setTag("ip", "123");

        if (sq.getCalculateOutputIfWhereIsFalse()) {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_5, sink.samples.get(5)));
            assertEquals(6, sink.samples.size());
        } else {
            assertEquals(5, sink.samples.size());
        }

        // Input 7
        String[] input_7_strings = {"CPU"};
        Header header_input_7 = new Header(input_7_strings);
        double[] values_input_7 = {22};
        Sample inputSample_7 = new Sample(header_input_7, values_input_7, d);
        inputSample_7.setTag("host", "d");
        sq.writeSample(inputSample_7);
        // Expected Output 7
        String[] output_7_strings = {"c", "hosts"};
        Header header_output_7 = new Header(output_7_strings);
        double[] values_output_7 = {6, 4};
        Sample expectedOutputSample_7 = new Sample(header_output_7, values_output_7, d);
        expectedOutputSample_7.setTag("host", "d");

        if (sq.getCalculateOutputIfWhereIsFalse()) {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_7, sink.samples.get(6)));
            assertEquals(7, sink.samples.size());
        } else {
            assertEquals(6, sink.samples.size());
        }
    }

    /**
     * A complex test with window value mode and some aggregation functions.
     */
    @Test
    public void test6() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select CPU, sum(CPU) As CPUSUM, Min(RAM)+MAX(RAM), Count(*) As Samples, Count(host) As hosts Where RAM>=20 AND CPU>0.5 Window 4";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"CPU", "RAM"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {1, 20};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        inputSample_1.setTag("host", "a");
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"CPU", "CPUSUM", "Min(RAM)+Max(RAM)", "Samples", "hosts"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {1, 1, 40, 1, 1};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        expectedOutputSample_1.setTag("host", "a");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"CPU", "RAM"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {5, 100};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        inputSample_2.setTag("host", "b");
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"CPU", "CPUSUM", "Min(RAM)+Max(RAM)", "Samples", "hosts"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {5, 6, 120, 2, 2};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        expectedOutputSample_2.setTag("host", "b");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        // Input 3
        String[] input_3_strings = {"CPU", "RAM"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {10, 200};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        inputSample_3.setTag("host", "c");
        sq.writeSample(inputSample_3);
        // Expected Output 3
        String[] output_3_strings = {"CPU", "CPUSUM", "Min(RAM)+Max(RAM)", "Samples", "hosts"};
        Header header_output_3 = new Header(output_3_strings);
        double[] values_output_3 = {10, 16, 220, 3, 3};
        Sample expectedOutputSample_3 = new Sample(header_output_3, values_output_3, d);
        expectedOutputSample_3.setTag("host", "c");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3, sink.samples.get(2)));

        // Input 4
        String[] input_4_strings = {"CPU", "RAM"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {10, 500};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        inputSample_4.setTag("host", "c");
        sq.writeSample(inputSample_4);
        // Expected Output 4
        String[] output_4_strings = {"CPU", "CPUSUM", "Min(RAM)+Max(RAM)", "Samples", "hosts"};
        Header header_output_4 = new Header(output_4_strings);
        double[] values_output_4 = {10, 26, 520, 4, 3};
        Sample expectedOutputSample_4 = new Sample(header_output_4, values_output_4, d);
        expectedOutputSample_4.setTag("host", "c");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4, sink.samples.get(3)));

        // Input 5
        String[] input_5_strings = {"CPU", "RAM"};
        Header header_input_5 = new Header(input_5_strings);
        double[] values_input_5 = {5, 200};
        Sample inputSample_5 = new Sample(header_input_5, values_input_5, d);
        inputSample_5.setTag("host", "c");
        sq.writeSample(inputSample_5);
        // Expected Output 5
        String[] output_5_strings = {"CPU", "CPUSUM", "Min(RAM)+Max(RAM)", "Samples", "hosts"};
        Header header_output_5 = new Header(output_5_strings);
        double[] values_output_5 = {5, 30, 600, 4, 2};
        Sample expectedOutputSample_5 = new Sample(header_output_5, values_output_5, d);
        expectedOutputSample_5.setTag("host", "c");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_5, sink.samples.get(4)));

        // Input 6
        String[] input_6_strings = {"CPU", "RAM"};
        Header header_input_6 = new Header(input_6_strings);
        double[] values_input_6 = {0.5, 100};
        Sample inputSample_6 = new Sample(header_input_6, values_input_6, d);
        inputSample_6.setTag("host", "a");
        sq.writeSample(inputSample_6);
        // Expected Output 6
        String[] output_6_strings = {"CPU", "CPUSUM", "Min(RAM)+Max(RAM)", "Samples", "hosts"};
        Header header_output_6 = new Header(output_6_strings);
        double[] values_output_6 = {5, 30, 600, 4, 2};
        Sample expectedOutputSample_6 = new Sample(header_output_6, values_output_6, d);
        expectedOutputSample_6.setTag("host", "c");

        if (sq.getCalculateOutputIfWhereIsFalse()) {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_6, sink.samples.get(5)));
            assertEquals(6, sink.samples.size());
        } else {
            assertEquals(5, sink.samples.size());
        }

        // Input 7
        String[] input_7_strings = {"CPU", "RAM"};
        Header header_input_7 = new Header(input_7_strings);
        double[] values_input_7 = {2, 19};
        Sample inputSample_7 = new Sample(header_input_7, values_input_7, d);
        inputSample_7.setTag("host", "b");
        sq.writeSample(inputSample_7);
        // Expected Output 7
        String[] output_7_strings = {"CPU", "CPUSUM", "Min(RAM)+Max(RAM)", "Samples", "hosts"};
        Header header_output_7 = new Header(output_7_strings);
        double[] values_output_7 = {5, 30, 600, 4, 2};
        Sample expectedOutputSample_7 = new Sample(header_output_7, values_output_7, d);
        expectedOutputSample_7.setTag("host", "c");

        if (sq.getCalculateOutputIfWhereIsFalse()) {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_7, sink.samples.get(6)));
            assertEquals(7, sink.samples.size());
        } else {
            assertEquals(5, sink.samples.size());
        }

        // Input 8
        String[] input_8_strings = {"CPU", "RAM"};
        Header header_input_8 = new Header(input_8_strings);
        double[] values_input_8 = {0.7, 25.55};
        Sample inputSample_8 = new Sample(header_input_8, values_input_8, d);
        inputSample_8.setTag("host", "c");
        sq.writeSample(inputSample_8);
        // Expected Output 8
        String[] output_8_strings = {"CPU", "CPUSUM", "Min(RAM)+Max(RAM)", "Samples", "hosts"};
        Header header_output_8 = new Header(output_8_strings);
        double[] values_output_8 = {0.7, 25.7, 525.55, 4, 1};
        Sample expectedOutputSample_8 = new Sample(header_output_8, values_output_8, d);
        expectedOutputSample_8.setTag("host", "c");

        if (sq.getCalculateOutputIfWhereIsFalse()) {
            assertTrue(TestHelpers.compareSamples(expectedOutputSample_8, sink.samples.get(7)));
            assertEquals(8, sink.samples.size());
        } else {
            assertEquals(6, sink.samples.size());
        }
    }

    /**
     * Test for the window FILE time mode.
     */
    @Test
    public void test7() throws IOException, InterruptedException {

        // define query
        String query = "Select Count(*) As c Window FIle 10 s";
        StreamingQuery sq;

        System.out.println("Preparing timestamps...");
        Date d1 = new Date();
        Thread.sleep(5000);
        Date d2 = new Date();
        Date d3 = new Date();
        Thread.sleep(6000);
        Date d4 = new Date();
        Thread.sleep(11000);
        Date d5 = new Date();

        System.out.println("... timestamps ready! \n");

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"CPU"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {100};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d1);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"c"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {1};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d1);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"CPU"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {200};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d2);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"c"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {2};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d2);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        // Input 3
        String[] input_3_strings = {"CPU"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {33};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d3);
        sq.writeSample(inputSample_3);
        // Expected Output 3
        String[] output_3_strings = {"c"};
        Header header_output_3 = new Header(output_3_strings);
        double[] values_output_3 = {3};
        Sample expectedOutputSample_3 = new Sample(header_output_3, values_output_3, d3);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3, sink.samples.get(2)));

        // Input 4
        String[] input_4_strings = {"CPU"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {66};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d4);
        sq.writeSample(inputSample_4);
        // Expected Output 4
        String[] output_4_strings = {"c"};
        Header header_output_4 = new Header(output_4_strings);
        double[] values_output_4 = {3};
        Sample expectedOutputSample_4 = new Sample(header_output_4, values_output_4, d4);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4, sink.samples.get(3)));

        // Input 5
        String[] input_5_strings = {"CPU"};
        Header header_input_5 = new Header(input_5_strings);
        double[] values_input_5 = {66};
        Sample inputSample_5 = new Sample(header_input_5, values_input_5, d5);
        sq.writeSample(inputSample_5);
        // Expected Output 5
        String[] output_5_strings = {"c"};
        Header header_output_5 = new Header(output_5_strings);
        double[] values_output_5 = {1};
        Sample expectedOutputSample_5 = new Sample(header_output_5, values_output_5, d5);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_5, sink.samples.get(4)));
    }

    /**
     * Test for Count(tag) if the tag is not part of sample
     */
    @Test
    public void test8() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select Count(host) As c Window all";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"CPU", "RAM"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {1, 20};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"c"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {0};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);

        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));
    }

}
