package bitflow4j.steps.query;

import bitflow4j.Header;
import bitflow4j.MockContext;
import bitflow4j.Sample;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that must run without error after version 3 of the program.
 */
@Disabled("The query script language is currently not further developed")
public class TestsForV3 {

    /**
     * A first test for group by, in combination with window all and some
     * different types at the select part
     */
    @Test
    public void test1() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select RAM, Sum(RAM), Count(*), Count(host) Group by host Window all";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"RAM"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {10};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        inputSample_1.setTag("host", "a");
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {10, 10, 1, 1};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        expectedOutputSample_1.setTag("host", "a");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"RAM"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {20};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        inputSample_2.setTag("host", "a");
        sq.writeSample(inputSample_2);
        // Expected Output 2
        String[] output_2_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_2 = new Header(output_2_strings);
        double[] values_output_2 = {20, 30, 2, 1};
        Sample expectedOutputSample_2 = new Sample(header_output_2, values_output_2, d);
        expectedOutputSample_2.setTag("host", "a");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2, sink.samples.get(1)));

        // Input 3
        String[] input_3_strings = {"RAM"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {5};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        inputSample_3.setTag("host", "b");
        sq.writeSample(inputSample_3);

        // Expected Output 3a
        String[] output_3a_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_3a = new Header(output_3a_strings);
        double[] values_output_3a = {20, 30, 2, 1};
        Sample expectedOutputSample_3a = new Sample(header_output_3a, values_output_3a, d);
        expectedOutputSample_3a.setTag("host", "a");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3a, sink.samples.get(2)));
        // Expected Output 3b
        String[] output_3b_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_3b = new Header(output_3b_strings);
        double[] values_output_3b = {5, 5, 1, 1};
        Sample expectedOutputSample_3b = new Sample(header_output_3b, values_output_3b, d);
        expectedOutputSample_3b.setTag("host", "b");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3b, sink.samples.get(3)));

        // Input 4
        String[] input_4_strings = {"RAM"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {25};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        inputSample_4.setTag("host", "a");
        sq.writeSample(inputSample_4);

        // Expected Output 4a
        String[] output_4a_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_4a = new Header(output_4a_strings);
        double[] values_output_4a = {25, 55, 3, 1};
        Sample expectedOutputSample_4a = new Sample(header_output_4a, values_output_4a, d);
        expectedOutputSample_4a.setTag("host", "a");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4a, sink.samples.get(4)));
        // Expected Output 4b
        String[] output_4b_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_4b = new Header(output_4b_strings);
        double[] values_output_4b = {5, 5, 1, 1};
        Sample expectedOutputSample_4b = new Sample(header_output_4b, values_output_4b, d);
        expectedOutputSample_4b.setTag("host", "b");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4b, sink.samples.get(5)));

        // Input 5
        String[] input_5_strings = {"RAM"};
        Header header_input_5 = new Header(input_5_strings);
        double[] values_input_5 = {15};
        Sample inputSample_5 = new Sample(header_input_5, values_input_5, d);
        inputSample_5.setTag("host", "b");
        sq.writeSample(inputSample_5);

        // Expected Output 5a
        String[] output_5a_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_5a = new Header(output_5a_strings);
        double[] values_output_5a = {25, 55, 3, 1};
        Sample expectedOutputSample_5a = new Sample(header_output_5a, values_output_5a, d);
        expectedOutputSample_5a.setTag("host", "a");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_5a, sink.samples.get(6)));
        // Expected Output 5b
        String[] output_5b_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_5b = new Header(output_5b_strings);
        double[] values_output_5b = {15, 20, 2, 1};
        Sample expectedOutputSample_5b = new Sample(header_output_5b, values_output_5b, d);
        expectedOutputSample_5b.setTag("host", "b");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_5b, sink.samples.get(7)));

        // Input 6
        String[] input_6_strings = {"RAM"};
        Header header_input_6 = new Header(input_6_strings);
        double[] values_input_6 = {1};
        Sample inputSample_6 = new Sample(header_input_6, values_input_6, d);
        inputSample_6.setTag("host", "c");
        sq.writeSample(inputSample_6);

        // Expected Output 6a
        String[] output_6a_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_6a = new Header(output_6a_strings);
        double[] values_output_6a = {25, 55, 3, 1};
        Sample expectedOutputSample_6a = new Sample(header_output_6a, values_output_6a, d);
        expectedOutputSample_6a.setTag("host", "a");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_6a, sink.samples.get(8)));
        // Expected Output 6b
        String[] output_6b_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_6b = new Header(output_6b_strings);
        double[] values_output_6b = {15, 20, 2, 1};
        Sample expectedOutputSample_6b = new Sample(header_output_6b, values_output_6b, d);
        expectedOutputSample_6b.setTag("host", "b");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_6b, sink.samples.get(9)));
        // Expected Output 6c
        String[] output_6c_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_6c = new Header(output_6c_strings);
        double[] values_output_6c = {1, 1, 1, 1};
        Sample expectedOutputSample_6c = new Sample(header_output_6c, values_output_6c, d);
        expectedOutputSample_6c.setTag("host", "c");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_6c, sink.samples.get(10)));

        // Input 7
        String[] input_7_strings = {"RAM"};
        Header header_input_7 = new Header(input_7_strings);
        double[] values_input_7 = {1337};
        Sample inputSample_7 = new Sample(header_input_7, values_input_7, d);
        sq.writeSample(inputSample_7);

        // Expected Output 7a
        String[] output_7a_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_7a = new Header(output_7a_strings);
        double[] values_output_7a = {25, 55, 3, 1};
        Sample expectedOutputSample_7a = new Sample(header_output_7a, values_output_7a, d);
        expectedOutputSample_7a.setTag("host", "a");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_7a, sink.samples.get(11)));
        // Expected Output 7b
        String[] output_7b_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_7b = new Header(output_7b_strings);
        double[] values_output_7b = {15, 20, 2, 1};
        Sample expectedOutputSample_7b = new Sample(header_output_7b, values_output_7b, d);
        expectedOutputSample_7b.setTag("host", "b");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_7b, sink.samples.get(12)));
        // Expected Output 7c
        String[] output_7c_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_7c = new Header(output_7c_strings);
        double[] values_output_7c = {1, 1, 1, 1};
        Sample expectedOutputSample_7c = new Sample(header_output_7c, values_output_7c, d);
        expectedOutputSample_7c.setTag("host", "c");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_7c, sink.samples.get(13)));
        // Expected Output 7d
        String[] output_7d_strings = {"RAM", "Sum(RAM)", "Count(*)", "Count(host)"};
        Header header_output_7d = new Header(output_7d_strings);
        double[] values_output_7d = {1337, 1337, 1, 0};
        Sample expectedOutputSample_7d = new Sample(header_output_7d, values_output_7d, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_7d, sink.samples.get(14)));
    }

    /**
     * A test for the having keyword.
     */
    @Test
    public void test2() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select a*2, b As B Where a>1 Having B>9";
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
        String[] output_1_strings = {"a*2", "B"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {10, 10};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"a", "b"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {5, 3};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        assertEquals(1, sink.samples.size());
    }

    /**
     * Another test for the having keyword.
     */
    @Test
    public void test3() throws IOException {

        Date d = new Date();

        // define query
        String query = "Select CPU, Sum(CPU) As SUM Window all Having SUM>100 AND CPU>30";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"CPU"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {90};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        sq.writeSample(inputSample_1);
        // Expected Output 1
        assertEquals(0, sink.samples.size());

        // Input 2
        String[] input_2_strings = {"CPU"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {20};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        sq.writeSample(inputSample_2);
        // Expected Output 2
        assertEquals(0, sink.samples.size());

        // Input 3
        String[] input_3_strings = {"CPU"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {40};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        sq.writeSample(inputSample_3);
        // Expected Output 3
        String[] output_3_strings = {"CPU", "SUM"};
        Header header_output_3 = new Header(output_3_strings);
        double[] values_output_3 = {40, 150};
        Sample expectedOutputSample_3 = new Sample(header_output_3, values_output_3, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3, sink.samples.get(0)));
    }

    /**
     * Test for group by with 2 tags.
     */
    @Test
    public void test4() throws IOException {
        Date d = new Date();

        // define query
        String query = "Select Min(CPU), Max(CPU), Sum(CPU) Group by host, IP Window all";
        StreamingQuery sq;

        MockContext sink = new MockContext();
        sq = new StreamingQuery(query);
        sq.initialize(sink);

        // Input 1
        String[] input_1_strings = {"CPU"};
        Header header_input_1 = new Header(input_1_strings);
        double[] values_input_1 = {5};
        Sample inputSample_1 = new Sample(header_input_1, values_input_1, d);
        inputSample_1.setTag("host", "luttmann");
        inputSample_1.setTag("IP", "123");
        sq.writeSample(inputSample_1);
        // Expected Output 1
        String[] output_1_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_1 = new Header(output_1_strings);
        double[] values_output_1 = {5, 5, 5};
        Sample expectedOutputSample_1 = new Sample(header_output_1, values_output_1, d);
        expectedOutputSample_1.setTag("host", "luttmann");
        expectedOutputSample_1.setTag("IP", "123");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_1, sink.samples.get(0)));

        // Input 2
        String[] input_2_strings = {"CPU"};
        Header header_input_2 = new Header(input_2_strings);
        double[] values_input_2 = {25};
        Sample inputSample_2 = new Sample(header_input_2, values_input_2, d);
        inputSample_2.setTag("host", "Gulenko");
        inputSample_2.setTag("IP", "124");
        sq.writeSample(inputSample_2);
        // Expected Output 2a
        String[] output_2a_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_2a = new Header(output_2a_strings);
        double[] values_output_2a = {5, 5, 5};
        Sample expectedOutputSample_2a = new Sample(header_output_2a, values_output_2a, d);
        expectedOutputSample_2a.setTag("host", "luttmann");
        expectedOutputSample_2a.setTag("IP", "123");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2a, sink.samples.get(1)));
        // Expected Output 2b
        String[] output_2b_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_2b = new Header(output_2b_strings);
        double[] values_output_2b = {25, 25, 25};
        Sample expectedOutputSample_2b = new Sample(header_output_2b, values_output_2b, d);
        expectedOutputSample_2b.setTag("host", "Gulenko");
        expectedOutputSample_2b.setTag("IP", "124");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_2b, sink.samples.get(2)));

        // Input 3
        String[] input_3_strings = {"CPU"};
        Header header_input_3 = new Header(input_3_strings);
        double[] values_input_3 = {10};
        Sample inputSample_3 = new Sample(header_input_3, values_input_3, d);
        inputSample_3.setTag("host", "luttmann");
        inputSample_3.setTag("IP", "123");
        sq.writeSample(inputSample_3);
        // Expected Output 3a
        String[] output_3a_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_3a = new Header(output_3a_strings);
        double[] values_output_3a = {5, 10, 15};
        Sample expectedOutputSample_3a = new Sample(header_output_3a, values_output_3a, d);
        expectedOutputSample_3a.setTag("host", "luttmann");
        expectedOutputSample_3a.setTag("IP", "123");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3a, sink.samples.get(3)));
        // Expected Output 3b
        String[] output_3b_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_3b = new Header(output_3b_strings);
        double[] values_output_3b = {25, 25, 25};
        Sample expectedOutputSample_3b = new Sample(header_output_3b, values_output_3b, d);
        expectedOutputSample_3b.setTag("host", "Gulenko");
        expectedOutputSample_3b.setTag("IP", "124");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_3b, sink.samples.get(4)));

        // Input 4
        String[] input_4_strings = {"CPU"};
        Header header_input_4 = new Header(input_4_strings);
        double[] values_input_4 = {20};
        Sample inputSample_4 = new Sample(header_input_4, values_input_4, d);
        inputSample_4.setTag("host", "luttmann");
        inputSample_4.setTag("IP", "456");
        sq.writeSample(inputSample_4);
        // Expected Output 4a
        String[] output_4a_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_4a = new Header(output_4a_strings);
        double[] values_output_4a = {5, 10, 15};
        Sample expectedOutputSample_4a = new Sample(header_output_4a, values_output_4a, d);
        expectedOutputSample_4a.setTag("host", "luttmann");
        expectedOutputSample_4a.setTag("IP", "123");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4a, sink.samples.get(5)));
        // Expected Output 4b
        String[] output_4b_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_4b = new Header(output_4b_strings);
        double[] values_output_4b = {25, 25, 25};
        Sample expectedOutputSample_4b = new Sample(header_output_4b, values_output_4b, d);
        expectedOutputSample_4b.setTag("host", "Gulenko");
        expectedOutputSample_4b.setTag("IP", "124");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4b, sink.samples.get(6)));
        // Expected Output 4c
        String[] output_4c_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_4c = new Header(output_4c_strings);
        double[] values_output_4c = {20, 20, 20};
        Sample expectedOutputSample_4c = new Sample(header_output_4c, values_output_4c, d);
        expectedOutputSample_4c.setTag("host", "luttmann");
        expectedOutputSample_4c.setTag("IP", "456");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_4c, sink.samples.get(7)));

        // Input 5
        String[] input_5_strings = {"CPU"};
        Header header_input_5 = new Header(input_5_strings);
        double[] values_input_5 = {33};
        Sample inputSample_5 = new Sample(header_input_5, values_input_5, d);
        inputSample_5.setTag("host", "luttmann");
        sq.writeSample(inputSample_5);
        // Expected Output 5a
        String[] output_5a_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_5a = new Header(output_5a_strings);
        double[] values_output_5a = {5, 10, 15};
        Sample expectedOutputSample_5a = new Sample(header_output_5a, values_output_5a, d);
        expectedOutputSample_5a.setTag("host", "luttmann");
        expectedOutputSample_5a.setTag("IP", "123");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_5a, sink.samples.get(8)));
        // Expected Output 5b
        String[] output_5b_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_5b = new Header(output_5b_strings);
        double[] values_output_5b = {25, 25, 25};
        Sample expectedOutputSample_5b = new Sample(header_output_5b, values_output_5b, d);
        expectedOutputSample_5b.setTag("host", "Gulenko");
        expectedOutputSample_5b.setTag("IP", "124");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_5b, sink.samples.get(9)));
        // Expected Output 5c
        String[] output_5c_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_5c = new Header(output_5c_strings);
        double[] values_output_5c = {20, 20, 20};
        Sample expectedOutputSample_5c = new Sample(header_output_5c, values_output_5c, d);
        expectedOutputSample_5c.setTag("host", "luttmann");
        expectedOutputSample_5c.setTag("IP", "456");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_5c, sink.samples.get(10)));
        // Expected Output 5d
        String[] output_5d_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_5d = new Header(output_5d_strings);
        double[] values_output_5d = {33, 33, 33};
        Sample expectedOutputSample_5d = new Sample(header_output_5d, values_output_5d, d);
        expectedOutputSample_5d.setTag("host", "luttmann");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_5d, sink.samples.get(11)));

        // Input 6
        String[] input_6_strings = {"CPU"};
        Header header_input_6 = new Header(input_6_strings);
        double[] values_input_6 = {75};
        Sample inputSample_6 = new Sample(header_input_6, values_input_6, d);
        inputSample_6.setTag("host", "Gulenko");
        inputSample_6.setTag("IP", "124");
        sq.writeSample(inputSample_6);
        // Expected Output 6a
        String[] output_6a_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_6a = new Header(output_6a_strings);
        double[] values_output_6a = {5, 10, 15};
        Sample expectedOutputSample_6a = new Sample(header_output_6a, values_output_6a, d);
        expectedOutputSample_6a.setTag("host", "luttmann");
        expectedOutputSample_6a.setTag("IP", "123");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_6a, sink.samples.get(12)));
        // Expected Output 6b
        String[] output_6b_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_6b = new Header(output_6b_strings);
        double[] values_output_6b = {25, 75, 100};
        Sample expectedOutputSample_6b = new Sample(header_output_6b, values_output_6b, d);
        expectedOutputSample_6b.setTag("host", "Gulenko");
        expectedOutputSample_6b.setTag("IP", "124");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_6b, sink.samples.get(13)));
        // Expected Output 6c
        String[] output_6c_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_6c = new Header(output_6c_strings);
        double[] values_output_6c = {20, 20, 20};
        Sample expectedOutputSample_6c = new Sample(header_output_6c, values_output_6c, d);
        expectedOutputSample_6c.setTag("host", "luttmann");
        expectedOutputSample_6c.setTag("IP", "456");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_6c, sink.samples.get(14)));
        // Expected Output 6d
        String[] output_6d_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_6d = new Header(output_6d_strings);
        double[] values_output_6d = {33, 33, 33};
        Sample expectedOutputSample_6d = new Sample(header_output_6d, values_output_6d, d);
        expectedOutputSample_6d.setTag("host", "luttmann");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_6d, sink.samples.get(15)));

        // Input 7
        String[] input_7_strings = {"CPU"};
        Header header_input_7 = new Header(input_7_strings);
        double[] values_input_7 = {66};
        Sample inputSample_7 = new Sample(header_input_7, values_input_7, d);
        inputSample_7.setTag("IP", "789");
        sq.writeSample(inputSample_7);
        // Expected Output 7a
        String[] output_7a_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_7a = new Header(output_7a_strings);
        double[] values_output_7a = {5, 10, 15};
        Sample expectedOutputSample_7a = new Sample(header_output_7a, values_output_7a, d);
        expectedOutputSample_7a.setTag("host", "luttmann");
        expectedOutputSample_7a.setTag("IP", "123");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_7a, sink.samples.get(16)));
        // Expected Output 7b
        String[] output_7b_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_7b = new Header(output_7b_strings);
        double[] values_output_7b = {25, 75, 100};
        Sample expectedOutputSample_7b = new Sample(header_output_7b, values_output_7b, d);
        expectedOutputSample_7b.setTag("host", "Gulenko");
        expectedOutputSample_7b.setTag("IP", "124");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_7b, sink.samples.get(17)));
        // Expected Output 7c
        String[] output_7c_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_7c = new Header(output_7c_strings);
        double[] values_output_7c = {20, 20, 20};
        Sample expectedOutputSample_7c = new Sample(header_output_7c, values_output_7c, d);
        expectedOutputSample_7c.setTag("host", "luttmann");
        expectedOutputSample_7c.setTag("IP", "456");
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_7c, sink.samples.get(18)));
        // Expected Output 7d
        String[] output_7d_strings = {"Min(CPU)", "Max(CPU)", "Sum(CPU)"};
        Header header_output_7d = new Header(output_7d_strings);
        double[] values_output_7d = {33, 66, 99};
        Sample expectedOutputSample_7d = new Sample(header_output_7d, values_output_7d, d);
        assertTrue(TestHelpers.compareSamples(expectedOutputSample_7d, sink.samples.get(19)));
    }

}
