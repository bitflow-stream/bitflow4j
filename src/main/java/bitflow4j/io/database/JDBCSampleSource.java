package bitflow4j.io.database;

import bitflow4j.sample.AbstractSampleSource;
import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * JDBC Sample Source
 */
public abstract class JDBCSampleSource extends AbstractSampleSource implements JDBCConnector {

    private static final Logger logger = Logger.getLogger(JDBCSampleSource.class.getName());

    private JDBCConnector connector;

    public JDBCSampleSource() {
        this.connector = new JDBCConnectorImpl();
    }

    public JDBCSampleSource(JDBCConnector jdbcConnector) {
        this.connector = jdbcConnector;
    }

    public JDBCSampleSource(String dbDriver, String dbName, String dbUrl, String dbUser, String dbPassword) {
//        this.connector = new JDBCConnectorImpl(dbDriver, dbName, dbUrl, dbUser, dbPassword);
    }

    public JDBCSampleSource(DB db, String dbName, String dbUrl, String dbUser, String dbPassword) {
//        this.connector = new JDBCConnectorImpl(db, dbName, dbUrl, dbUser, dbPassword);
    }

    @Override
    public void start(TaskPool pool) throws IOException {

    }

    @Override
    public JDBCSampleSource setDb(DB db) {
        connector.setDb(db);
        return this;
    }

    @Override
    public JDBCConnector connect() {
        //TODO auto generated
        return null;
    }

    @Override
    public String getDbPassword() {
        return connector.getDbPassword();
    }

    @Override
    public JDBCSampleSource setDbPassword(String dbPassword) {
        connector.setDbPassword(dbPassword);
        return this;
    }

    @Override
    public String getDbUser() {
        return connector.getDbUser();
    }

    @Override
    public JDBCSampleSource setDbUser(String dbUser) {
        connector.setDbUser(dbUser);
        return this;
    }

    @Override
    public String getDbUrl() {
        return connector.getDbUrl();
    }

    @Override
    public JDBCSampleSource setDbUrl(String dbUrl) {
        connector.setDbUrl(dbUrl);
        return this;
    }

    @Override
    public String getDbName() {
        return connector.getDbName();
    }

    @Override
    public JDBCSampleSource setDbName(String dbName) {
        connector.setDbName(dbName);
        return this;
    }

    public String getDbDriverName() {
        return connector.getDbDriverName();
    }

    @Override
    public JDBCSampleSource setDbDriver(String dbDriver) {
        connector.setDbDriver(dbDriver);
        return this;
    }

    public JDBCConnector getConnector() {
        return this.connector;
    }
}
