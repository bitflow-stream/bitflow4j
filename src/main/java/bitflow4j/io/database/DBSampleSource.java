package bitflow4j.io.database;

import bitflow4j.sample.AbstractSampleSource;
import bitflow4j.sample.Sample;
import bitflow4j.task.StoppableLoopTask;
import bitflow4j.task.StoppableTask;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by malcolmx on 21.03.17.
 */
public class DBSampleSource extends AbstractSampleSource implements StoppableTask {

    private static final Logger logger = Logger.getLogger(JDBCSampleSource.class.getName());
    private final DBSampleSource.ReaderTask task;
    private final JDBCReader reader;


    public DBSampleSource(JDBCReader reader) {
        this.reader = reader;
        this.task = new DBSampleSource.ReaderTask();
    }

    @Override
    public void start(TaskPool pool) throws IOException {
//        try {
//            connector.connect().prepareRead();
//        } catch (SQLException e) {
//            throw new IOException(e);
//        }
        pool.start(this.task);
    }

    public void stop() throws IOException {
//        try {
//            connector.disconnect();
//        } catch (SQLException e) {
//            throw new IOException(e);
//        }
    }

//    public JDBCConnector getConnector() {
//        return this.connector;
//    }

    private class ReaderTask extends StoppableLoopTask {
        @Override
        protected boolean executeIteration() throws IOException {
            if (!pool.isRunning())
                return false;

            Sample sampleReadInIteration;
//            try {
//                sampleReadInIteration = connector.nextSample();
//            } catch (SQLException e) {
//                throw new IOException(e);
//            }
//
//            if (sampleReadInIteration == null || !pool.isRunning())
//                return false;
//            output().writeSample(sampleReadInIteration);
            return true;
        }
    }
}
