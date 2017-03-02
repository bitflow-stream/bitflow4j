package bitflow4j.io.database;

import bitflow4j.sample.AbstractSampleSink;
import bitflow4j.sample.Sample;

import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Created by malcolmx on 15.02.17.
 */
public abstract class JDBCSampleSink extends AbstractSampleSink implements JDBCConnector {
    private static final Logger logger = Logger.getLogger(JDBCSampleSink.class.getName());
    private JDBCConnector connector;

    public JDBCSampleSink(JDBCConnector jdbcConnector) {
        this.connector = jdbcConnector;
    }

    @Override
    public void writeSample(Sample sample) {
        try {
            connector.writeSample(sample);
        } catch (SQLException e) {
            logger.severe(e.getMessage());
        }
    }

    public JDBCConnector getConnector() {
        return this.connector;
    }
}
