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
import java.util.List;

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
        testWritePipeline.runAndWait();
        List<Sample> samplesWritten = testSampleSource.samples;
        List<Sample> samplesRead = testSampleSink.samplesRead;

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
