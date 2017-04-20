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
 * Created by malcolmx on 21.03.17.
 */
public class DBSampleSource extends AbstractSampleSource implements StoppableTask {

    private static final Logger logger = Logger.getLogger(DBSampleSource.class.getName());
    private final DBSampleSource.ReaderTask task;
    private final JDBCReader reader;


    public DBSampleSource(JDBCReader reader) {
        this.reader = reader;
        this.task = new DBSampleSource.ReaderTask();
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        try {
            reader.connect().prepareRead();
        } catch (SQLException e) {
            throw new IOException(e);
        }
        pool.start(this.task);
    }

    public void stop() throws IOException {
        try {
            this.task.shuttingDown = true;
            reader.disconnect();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    private class ReaderTask extends StoppableLoopTask {
        boolean shuttingDown = false;

        @Override
        protected boolean executeIteration() throws IOException {
            if (!pool.isRunning())
                return false;

            Sample sampleReadInIteration;
            try {
                sampleReadInIteration = reader.nextSample();
            } catch (SQLException e) {
                if (!shuttingDown) {
                    throw new IOException(e);
                } else {
                    sampleReadInIteration = null;
                }
            }

            if (sampleReadInIteration == null || !pool.isRunning()) {
                output().close();
                return false;
            }
            output().writeSample(sampleReadInIteration);
            return true;
        }

        @Override
        public void run() throws IOException {
            try {
                super.run();
            } catch (IOException e) {
                throw e;
            } finally {
                DBSampleSource.this.output().close();
            }
        }
    }


}
