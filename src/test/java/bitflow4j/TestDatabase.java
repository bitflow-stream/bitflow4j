package bitflow4j;

import bitflow4j.io.list.ListSink;
import bitflow4j.io.list.ListSource;
import bitflow4j.io.sql.DBSampleSink;
import bitflow4j.io.sql.DBSampleSource;
import bitflow4j.io.sql.JDBCReader;
import bitflow4j.io.sql.JDBCWriter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by malcolmx on 21.03.17.
 */
public class TestDatabase extends TestWithSamples {

    private static final String DB_FILE = "sqlitetest2";

    @Test
    public void testSQLite() {
        DBSampleSource dbSampleSource = new DBSampleSource(new JDBCReader("jdbc:sqlite:" + DB_FILE, null, "samples", null, null));
        DBSampleSink dbSampleSink = new DBSampleSink(new JDBCWriter("jdbc:sqlite:" + DB_FILE, null, "samples", null, null));
        List<Sample> samples = flatten(createSamples());
        ListSource testSampleSource = new ListSource(samples);
        ListSink testSampleSink = new ListSink();
        Pipeline testWritePipeline = new Pipeline().input(testSampleSource).step(dbSampleSink);
        Pipeline testReadPipeline = new Pipeline().input(dbSampleSource).step(testSampleSink);
        testWritePipeline.runAndWait();
        testReadPipeline.runAndWait();
        List<Sample> samplesRead = testSampleSink.samples;
        assertTrue(samplesEqual(samples, samplesRead), "Test failed because the samples written to the sql and the samples read from the sql where different.");
    }

    @AfterEach
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
        assert tags1 != null;
        return tags1.equals(tags2);
    }

}
