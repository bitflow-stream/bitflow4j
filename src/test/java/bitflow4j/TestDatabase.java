package bitflow4j;

import bitflow4j.io.database.JDBCConnector;
import bitflow4j.io.database.JDBCConnector.DB;
import bitflow4j.io.database.JDBCSampleSink;
import bitflow4j.io.database.JDBCSampleSource;
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
        JDBCConnector connector = new JDBCConnector(DB.SQLite, "jdbc:sqlite:" + DB_FILE, null, null, null, null, "samples", "samples");
        JDBCSampleSource jdbcSampleSource = new JDBCSampleSource(connector);
        JDBCSampleSink jdbcSampleSink = new JDBCSampleSink(connector);
        TestSampleSource testSampleSource = new TestSampleSource();
        TestSampleSink testSampleSink = new TestSampleSink();
        AlgorithmPipeline testWritePipeline = new AlgorithmPipeline().input(testSampleSource).output(jdbcSampleSink);
        AlgorithmPipeline testReadPipeline = new AlgorithmPipeline().input(jdbcSampleSource).output(testSampleSink);
        testWritePipeline.runAndWait();
        System.out.println("finished write pipeline test");
        testReadPipeline.runAndWait();
        List<Sample> samplesWritten = testSampleSource.samples;
        List<Sample> samplesRead = testSampleSink.samplesRead;
        Assert.assertTrue("Test failed because the samples written to the database and the samples read from the database where different.", samplesEqual(samplesRead, samplesWritten));
    }

    private void compareSamples(List<Sample> samplesWritten, List<Sample> samplesRead) {
        Assert.assertEquals("A different number of samples has been written and read from db: samples read = " + samplesRead.size() + "; samples written = " + samplesWritten.size(), samplesRead.size(), samplesWritten.size());
        for (int i = 0; i < samplesRead.size(); i++) {
            Assert.assertEquals("Sample read and sample written are different (samplenumber: " + (i + 1) + ").", samplesRead.get(i), samplesWritten.get(i));
        }
    }

    @After
    public void deleteDatabaseFile() {
        File file = new File(DB_FILE);
        System.out.println("Absolute file path: " + file.getAbsolutePath());
        boolean success = file.delete();
        if (success) System.out.println("deleted successfully");
        else System.out.println("delete failed");
    }

    private boolean samplesEqual(List<Sample> samples1, List<Sample> samples2) {
        if (samples1.size() != samples2.size()) return false;
        for (int i = 0; i < samples1.size(); i++) {
            if (!sampleEquals(samples1.get(i), samples2.get(i))) return false;
        }
        return true;
    }

    private boolean sampleEquals(Sample sample1, Sample sample2) {
        if (Arrays.equals(sample1.getHeader().header, sample2.getHeader().header)) {
            if (Arrays.equals(sample1.getMetrics(), sample2.getMetrics())) {
                if (tagsEqual(sample1.getTags(), sample2.getTags()))
                    return true;
                else System.out.println("failed because tags different");
            } else System.out.println("failed because metrics different");
        } else System.out.println("Failed because header different");
        return false;

    }

    private int[] findHeaderMapping(String[] header1, String[] header2) {
        if (header1.length != header2.length) return null;
        int[] mapping = new int[header1.length];
        for (int i = 0; i < header1.length; i++) {
            String headerField = header1[i];
//            for (int k = 0; k < header2.length; k++){
//            }
            int index = Arrays.binarySearch(header2, headerField);
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
