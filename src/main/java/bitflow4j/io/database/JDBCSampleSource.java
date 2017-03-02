package bitflow4j.io.database;

import bitflow4j.sample.AbstractSampleSource;
import bitflow4j.sample.StoppableSampleSource;
import bitflow4j.task.StoppableLoopTask;
import bitflow4j.task.StoppableTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * JDBC Sample Source
 */
public abstract class JDBCSampleSource extends AbstractSampleSource implements StoppableTask, StoppableSampleSource {

    private static final Logger logger = Logger.getLogger(JDBCSampleSource.class.getName());
    private final JDBCReaderTask task;

    private JDBCConnector connector;

    public JDBCSampleSource(JDBCConnector jdbcConnector) {
        this.connector = jdbcConnector;
        this.task = new JDBCReaderTask();
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        pool.start(this.task);
    }

    @Override
    public void stop() throws IOException {
        this.task.stop();
        //TODO really needed?
    }

    public JDBCConnector getConnector() {
        return this.connector;
    }

    private class JDBCReaderTask extends StoppableLoopTask {
        @Override
        protected boolean executeIteration() throws IOException {
            connector.nextSample();
            //TODO sample sink
            return true;
        }
    }
}
