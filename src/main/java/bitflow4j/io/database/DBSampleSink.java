package bitflow4j.io.database;

import bitflow4j.sample.AbstractSampleSink;
import bitflow4j.sample.Sample;
import bitflow4j.task.StoppableTask;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by malcolmx on 21.03.17.
 */
public class DBSampleSink extends AbstractSampleSink implements StoppableTask {

    private static final Logger logger = Logger.getLogger(DBSampleSink.class.getName());
    private final JDBCWriter writer;

    public DBSampleSink(JDBCWriter writer) {
        this.writer = writer;
    }

    @Override
    public void stop() throws IOException {
        try {
            writer.disconnect();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        // TODO check if connection is established and optionally connect
        // On exception: disconnect + still throw exception

        try {
            writer.connect().prepareInsert().writeSample(sample);
        } catch (SQLException e) {
            try {
                writer.disconnect();
            } finally {
                throw new IOException(e);
            }
        }
    }
}
