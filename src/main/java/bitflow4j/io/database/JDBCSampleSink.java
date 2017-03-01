package bitflow4j.io.database;

import bitflow4j.sample.AbstractSampleSink;
import bitflow4j.sample.Sample;

import java.util.logging.Logger;

/**
 * Created by malcolmx on 15.02.17.
 */
public abstract class JDBCSampleSink extends AbstractSampleSink implements JDBCConnector {
    private static final Logger logger = Logger.getLogger(JDBCSampleSink.class.getName());
    private JDBCConnector connector;

    public JDBCSampleSink() {
        this.connector = new JDBCConnectorImpl();
    }

    public JDBCSampleSink(JDBCConnector jdbcConnector) {
        this.connector = jdbcConnector;
    }

    public JDBCSampleSink(String dbDriver, String dbName, String dbUrl, String dbUser, String dbPassword) {
//        this.connector = new JDBCConnectorImpl(dbDriver, dbName, dbUrl, dbUser, dbPassword);
    }

    public JDBCSampleSink(DB db, String dbName, String dbUrl, String dbUser, String dbPassword) {
//        this.connector = new JDBCConnectorImpl(db, dbName, dbUrl, dbUser, dbPassword);
    }

    @Override
    public void writeSample(Sample sample) {
        //TODO
    }

    @Override
    public JDBCSampleSink setDb(DB db) {
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
    public JDBCSampleSink setDbPassword(String dbPassword) {
        connector.setDbPassword(dbPassword);
        return this;
    }

    @Override
    public String getDbUser() {
        return connector.getDbUser();
    }

    @Override
    public JDBCSampleSink setDbUser(String dbUser) {
        connector.setDbUser(dbUser);
        return this;
    }

    @Override
    public String getDbUrl() {
        return connector.getDbUrl();
    }

    @Override
    public JDBCSampleSink setDbUrl(String dbUrl) {
        connector.setDbUrl(dbUrl);
        return this;
    }

    @Override
    public String getDbName() {
        return connector.getDbName();
    }

    @Override
    public JDBCSampleSink setDbName(String dbName) {
        connector.setDbName(dbName);
        return this;
    }

    public String getDbDriverName() {
        return connector.getDbDriverName();
    }

    @Override
    public JDBCSampleSink setDbDriver(String dbDriver) {
        connector.setDbDriver(dbDriver);
        return this;
    }

    public JDBCConnector getConnector() {
        return this.connector;
    }
}
