package bitflow4j;

import bitflow4j.io.database.DBSampleSink;
import bitflow4j.io.database.DBSampleSource;
import bitflow4j.io.database.JDBCReader;
import bitflow4j.io.database.JDBCWriter;
import bitflow4j.main.AlgorithmPipeline;
import bitflow4j.sample.AbstractSampleSink;
import bitflow4j.sample.AbstractSampleSource;
import bitflow4j.sample.Header;
import bitflow4j.sample.Sample;
import bitflow4j.task.StoppableLoopTask;
import bitflow4j.task.TaskPool;
import javafx.util.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by malcolmx on 21.03.17.
 */
public class TestDatabase extends TestWithSamples {

    private static final String DB_FILE = "sqlitetest2";

    @Test
    public void testSQLite() {
        DBSampleSource dbSampleSource = new DBSampleSource(new JDBCReader(bitflow4j.io.database.DB.SQLite, "jdbc:sqlite:" + DB_FILE, null, "samples", null, null));
        DBSampleSink dbSampleSink = new DBSampleSink(new JDBCWriter(bitflow4j.io.database.DB.SQLite, "jdbc:sqlite:" + DB_FILE, null, "samples", null, null));
        TestSampleSource testSampleSource = new TestSampleSource();
        TestSampleSink testSampleSink = new TestSampleSink();
        AlgorithmPipeline testWritePipeline = new AlgorithmPipeline().input(testSampleSource).output(dbSampleSink);
        AlgorithmPipeline testReadPipeline = new AlgorithmPipeline().input(dbSampleSource).output(testSampleSink);
        testWritePipeline.runAndWait();
        testReadPipeline.runAndWait();
        List<Sample> samplesWritten = testSampleSource.samples;
        List<Sample> samplesRead = testSampleSink.samplesRead;
        Assert.assertTrue("Test failed because the samples written to the database and the samples read from the database where different.", samplesEqual(samplesWritten, samplesRead));
    }

    @After
    public void deleteDatabaseFile() {
        File file = new File(DB_FILE);
        System.out.println("Absolute file path: " + file.getAbsolutePath());
        boolean success = file.delete();
        if (success) System.out.println("deleted successfully");
        else System.out.println("delete failed");
    }

    private boolean samplesEqual(List<Sample> samplesExpected, List<Sample> samplesRead) {
        if (samplesExpected.size() != samplesRead.size()) return false;
        for (int i = 0; i < samplesExpected.size(); i++) {
            if (!sampleEquals(samplesExpected.get(i), samplesRead.get(i))) return false;
        }
        return true;
    }

    private boolean sampleEquals(Sample sampleExpected, Sample sampleRead) {
        int[] headerMappingExpectedToRead = findHeaderMapping(sampleExpected.getHeader().header, sampleRead.getHeader().header);
        if (headerMappingExpectedToRead != null) {
            if (metricsEqual(sampleExpected.getMetrics(), sampleRead.getMetrics(), headerMappingExpectedToRead)) {
                if (tagsEqual(sampleExpected.getTags(), sampleRead.getTags()))
                    return true;
                else System.out.println("failed because tags different");
            } else System.out.println("failed because metrics different");
        } else System.out.println("Failed because header different");
        return false;
    }

    private boolean metricsEqual(double[] metricsExpected, double[] metricsRead, int[] mapping) {
        for (int i = 0; i < metricsExpected.length; i++) {
            if (metricsExpected[i] != metricsRead[mapping[i]]) return false;
        }
        return true;
    }

    private int[] findHeaderMapping(String[] headerExpected, String[] headerRead) {
        if (headerExpected.length > headerRead.length) return null;
        int[] mapping = new int[headerExpected.length];
        for (int i = 0; i < headerExpected.length; i++) {
            String headerField = headerExpected[i];
            int index = Arrays.binarySearch(headerRead, headerField);
            if (index < 0) return null;
            mapping[i] = index; // cannot handle duplicate headers
        }
        return mapping;
    }


    private boolean tagsEqual(Map<String, String> tags1, Map<String, String> tags2) {
        if ((tags1 == null || tags1.isEmpty()) && (tags2 == null || tags2.isEmpty())) return true;

        return tags1.equals(tags2);
    }

    private class TestSampleSource extends AbstractSampleSource {

        List<Sample> samples = new ArrayList<>();

        @Override
        public void start(TaskPool pool) throws IOException {
            List<Pair<Header, List<Sample>>> headers = createSamples();
            buildSampleList(headers);
            pool.start(new SampleWriterTask());
        }

        private void buildSampleList(List<Pair<Header, List<Sample>>> headers) {
            headers.forEach(pair -> {
                pair.getValue().forEach(sample -> {
                    samples.add(sample);
                });
            });
        }

        private class SampleWriterTask extends StoppableLoopTask {
            private int cursor = 0;

            @Override
            protected boolean executeIteration() throws IOException {
                if (samples.size() > cursor) {
//                    System.out.println("Writing Sample with tags: " + samples.get(cursor).getTags());
//                    for (Map.Entry<String, String> entry : samples.get(cursor).getTags().entrySet()){
//                        System.out.println("key: " + entry.getKey() + " value: " + entry.getValue());
//                    }
                    output().writeSample(samples.get(cursor));
                    cursor++;
                    return true;
                }
                output().close();
                return false;
            }
        }
    }

    private class TestSampleSink extends AbstractSampleSink {
        List<Sample> samplesRead = new ArrayList<>();


        @Override
        public void writeSample(Sample sample) throws IOException {
            samplesRead.add(sample);
        }
    }
}
