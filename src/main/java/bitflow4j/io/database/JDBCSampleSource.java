package bitflow4j.io.database;

import bitflow4j.sample.AbstractSampleSource;
import bitflow4j.sample.Sample;
import bitflow4j.task.StoppableLoopTask;
import bitflow4j.task.StoppableTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * JDBC Sample Source
 */
public abstract class JDBCSampleSource extends AbstractSampleSource implements StoppableTask {

    private static final Logger logger = Logger.getLogger(JDBCSampleSource.class.getName());
    private final JDBCReaderTask task;
    private final JDBCConnector connector;

    public JDBCSampleSource(JDBCConnector jdbcConnector) {
        this.connector = jdbcConnector;
        this.task = new JDBCReaderTask();
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        pool.start(this.task);
    }

    public void stop() throws IOException {
        try {
            connector.disconnect();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    public JDBCConnector getConnector() {
        return this.connector;
    }

    private class JDBCReaderTask extends StoppableLoopTask {
        @Override
        protected boolean executeIteration() throws IOException {
            if (!pool.isRunning())
                return false;

            Sample sampleReadInIteration;
            try {
                sampleReadInIteration = connector.nextSample();
            } catch (SQLException e) {
                throw new IOException(e);
            }

            if (sampleReadInIteration == null || !pool.isRunning())
                return false;
            output().writeSample(sampleReadInIteration);
            return true;
        }
    }
}
