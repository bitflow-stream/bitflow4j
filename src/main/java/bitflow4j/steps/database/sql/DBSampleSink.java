package bitflow4j.steps.database.sql;

import bitflow4j.AbstractProcessingStep;
import bitflow4j.Context;
import bitflow4j.Sample;
import jdk.jfr.Name;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by malcolmx on 21.03.17.
 */
@Name("sql-store")
public class DBSampleSink extends AbstractProcessingStep {

    private static final Logger logger = Logger.getLogger(DBSampleSink.class.getName());
    private final JDBCWriter writer;

    public DBSampleSink(String url, String schema, String table, String user, String password) {
        this(new JDBCWriter(url, schema, table, user, password));
    }

    public DBSampleSink(JDBCWriter writer) {
        this.writer = writer;
    }

    @Override
    public String toString() {
        return String.format("SQL output (%s)", writer);
    }

    public void initialize(Context context) throws IOException {
        try {
            writer.connect().prepareInsert();
        } catch (SQLException e) {
            throw new IOException(e);
        }
        super.initialize(context);
    }

    @Override
    public void handleSample(Sample sample) throws IOException {
        // TODO check if connection is established and optionally connect
        // On exception: disconnect + still throw exception
        try {
            writer.connect().prepareInsert().writeSample(sample);
        } catch (SQLException e) {
            try {
                writer.disconnect();
            } catch (SQLException e2) {
                logger.log(Level.SEVERE, "Failed to disconnect writer after writing sample failed", e2);
            }
            throw new IOException(e);
        }
    }

    @Override
    public void cleanup() {
        try {
            writer.disconnect();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to disconnect from database", e);
        }
    }

}
