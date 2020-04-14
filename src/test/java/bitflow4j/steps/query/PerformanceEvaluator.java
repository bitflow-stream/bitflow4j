package bitflow4j.steps.query;

import bitflow4j.Header;
import bitflow4j.MockContext;
import bitflow4j.Sample;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The use of this class is to measure the performance of the implementation.
 */
public class PerformanceEvaluator {

    public static void main(String[] args) throws IOException {
        //Select *
        testQuery("Select *", 10, "Query1_10.txt", false);
        testQuery("Select *", 100, "Query1_100.txt", false);
        testQuery("Select *", 1000, "Query1_1000.txt", false);
        testQuery("Select *", 10000, "Query1_10000.txt", false);
        testQuery("Select *", 100000, "Query1_100000.txt", false);
        testQuery("Select *", 500000, "Query1_500000.txt", false);
        testQuery("Select *", 1000000, "Query1_1000000.txt", false);

        // Select First As F Where First>Second AND (Third<=Fourth OR Fifth>5)
        testQuery("Select First As F Where First>Second AND (Third<=Fourth OR Fifth>5)", 10, "Query2_10.txt", false);
        testQuery("Select First As F Where First>Second AND (Third<=Fourth OR Fifth>5)", 100, "Query2_100.txt", false);
        testQuery("Select First As F Where First>Second AND (Third<=Fourth OR Fifth>5)", 1000, "Query2_1000.txt", false);
        testQuery("Select First As F Where First>Second AND (Third<=Fourth OR Fifth>5)", 10000, "Query2_10000.txt", false);
        testQuery("Select First As F Where First>Second AND (Third<=Fourth OR Fifth>5)", 100000, "Query2_100000.txt", false);
        testQuery("Select First As F Where First>Second AND (Third<=Fourth OR Fifth>5)", 500000, "Query2_500000.txt", false);
        testQuery("Select First As F Where First>Second AND (Third<=Fourth OR Fifth>5)", 1000000, "Query2_1000000.txt", false);

        //Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Window 1000
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Window 1000", 10, "Query3_10.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Window 1000", 100, "Query3_100.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Window 1000", 1000, "Query3_1000.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Window 1000", 10000, "Query3_10000.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Window 1000", 100000, "Query3_100000.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Window 1000", 500000, "Query3_500000.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Window 1000", 1000000, "Query3_1000000.txt", false);

        //Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000", 10, "Query4_10.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000", 100, "Query4_100.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000", 1000, "Query4_1000.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000", 10000, "Query4_10000.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000", 100000, "Query4_100000.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000", 500000, "Query4_500000.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000", 1000000, "Query4_1000000.txt", false);

        //Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000 Having Sum>1600
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000 Having (Sum>1600 AND Sum<1800)", 10, "Query5_10.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000 Having (Sum>1600 AND Sum<1800)", 100, "Query5_100.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000 Having (Sum>1600 AND Sum<1800)", 1000, "Query5_1000.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000 Having (Sum>1600 AND Sum<1800)", 10000, "Query5_10000.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000 Having (Sum>1600 AND Sum<1800)", 100000, "Query5_100000.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000 Having (Sum>1600 AND Sum<1800)", 500000, "Query5_500000.txt", false);
        testQuery("Select Sum(First) As Sum Where First>Second AND (Third<=Fourth OR Fifth>5) Group by host Window 1000 Having (Sum>1600 AND Sum<1800)", 1000000, "Query5_1000000.txt", false);

        //Select First
        testQuery("Select First", 50000, "Query6_unoptimized_50000.txt", false);
        testQuery("Select First", 50000, "Query6_optimized_50000.txt", true);

        //Select Sum(First) As SUM Where First>5 AND Second<5 Group by host Window 1000 Having SUM>1000
        testQuery("Select Sum(First) As SUM Where First>5 AND Second<5 Group by host Window 1000 Having SUM>1000", 50000, "Query7_unoptimized_50000.txt", false);
        testQuery("Select Sum(First) As SUM Where First>5 AND Second<5 Group by host Window 1000 Having SUM>1000", 50000, "Query7_optimized_50000.txt", true);
    }

    /**
     * Creates a list with n samples.
     */
    public static ArrayList<Sample> createSamples(double n) {
        ArrayList<Sample> list = new ArrayList<>();
        Random r = new Random();

        for (int i = 0; i < n; i++) {
            Date timestamp = new Date();
            String[] headerStrings = {"First", "Second", "Third", "Fourth", "Fifth"};
            Header header = new Header(headerStrings);
            double d1 = 10 * r.nextDouble();
            double d2 = 10 * r.nextDouble();
            double d3 = 10 * r.nextDouble();
            double d4 = 10 * r.nextDouble();
            double d5 = 10 * r.nextDouble();
            double[] values_input_1 = {d1, d2, d3, d4, d5};

            Sample s = new Sample(header, values_input_1, timestamp);

            // add some tags
            int randomNum = ThreadLocalRandom.current().nextInt(1, 4 + 1);
            if (randomNum == 2) s.setTag("host", "Germany");
            if (randomNum == 3) s.setTag("host", "England");
            if (randomNum == 4) s.setTag("host", "France");
            list.add(s);
        }
        return list;
    }

    /**
     * Test the query query with n samples, stores measurements at the given filename.
     * If optimization is true use the optimization of the algorithm.
     */
    public static void testQuery(String query, double n, String fileName, boolean optimization) throws IOException {
        FileWriter fw = new FileWriter(fileName, false);
        fw.write("\n");
        fw.close();

        StreamingQuery sq;
        double totalTime = 0;
        long absoluteTime = 0;

        // Do every evaluation for 12 runs (but do not use the first and the second)
        for (int i = 0; i < 12; i++) {

            // Build sample list before testing
            ArrayList<Sample> list = createSamples(n);
            QueryOptimization queryOptimization = new QueryOptimization(query);

            MockContext context = new MockContext();
            sq = new StreamingQuery(query, false);
            sq.initialize(context);

            //start test
            long estimatedTime = 0;
            long startTime;
            if (optimization) {
                for (Sample s : list) {
                    startTime = System.nanoTime();
                    queryOptimization.writeSampleOptimization(s);
                    estimatedTime = estimatedTime + System.nanoTime() - startTime;
                }
            } else {
                for (Sample s : list) {
                    startTime = System.nanoTime();
                    sq.writeSample(s);
                    estimatedTime = estimatedTime + System.nanoTime() - startTime;
                }
            }

            if (i > 1) {
                double estimatedTimePerSample = estimatedTime / n;
                double milliSecondsPerSample = estimatedTimePerSample / 1e6;
                totalTime = totalTime + milliSecondsPerSample;
                absoluteTime = absoluteTime + estimatedTime;
                System.out.println("Time: " + milliSecondsPerSample);

                fw = new FileWriter(fileName, true); //the true will append the new data
                fw.write(milliSecondsPerSample + "\n");//appends the string to the file
                fw.close();
            }
        }

        double averageTimePerSampleInMilli = totalTime / 10.0;
        double absoluteMilliseconds = absoluteTime / 1e6;
        System.out.println("Average Time: " + averageTimePerSampleInMilli);
        System.out.println("Total Time: " + absoluteMilliseconds);

        fw = new FileWriter(fileName, true); //the true will append the new data
        fw.write("Total: \n" + absoluteMilliseconds + "\n");//appends the string to the file
        fw.write("Average: \n" + averageTimePerSampleInMilli + "\n");//appends the string to the file
        fw.close();
    }

    private static void runOptimizationWriteForHPROF() throws IOException {
        ArrayList<Sample> list = createSamples(50000);
        QueryOptimization queryOptimization = new QueryOptimization("Select Sum(First) As SUM Where First>5 AND Second<5 Group by host Window 1000 Having SUM>1000");

        for (Sample s : list) {
            queryOptimization.writeSampleOptimization(s);
        }
    }

    private static void runWriteForHPROF() throws IOException {
        ArrayList<Sample> list = createSamples(50000);
        MockContext context = new MockContext();
        StreamingQuery sq;

        sq = new StreamingQuery("Select Sum(First) As SUM Where First>5 AND Second<5 Group by host Window 1000 Having SUM>1000", false);
        sq.initialize(context);
        for (Sample s : list) {
            sq.writeSample(s);
        }
    }
}
