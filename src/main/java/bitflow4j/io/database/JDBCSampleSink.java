package bitflow4j.io.database;

import bitflow4j.sample.AbstractSampleSink;
import bitflow4j.sample.Sample;
import bitflow4j.task.StoppableTask;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by malcolmx on 15.02.17.
 */
public abstract class JDBCSampleSink extends AbstractSampleSink implements StoppableTask {

    private static final Logger logger = Logger.getLogger(JDBCSampleSink.class.getName());
    private final JDBCConnector connector;

    public JDBCSampleSink(JDBCConnector jdbcConnector) {
        this.connector = jdbcConnector;
    }

    @Override
    public void stop() throws IOException {
        try {
            connector.disconnect();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void writeSample(Sample sample) throws IOException {
        // TODO check if connection is established and optionally connect
        // On exception: disconnect + still throw exception
        try {
            connector.writeSample(sample);
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public JDBCConnector getConnector() {
        return this.connector;
    }
}
