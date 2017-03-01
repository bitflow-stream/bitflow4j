package bitflow4j.io.database;

import bitflow4j.sample.Sample;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by malcolmx on 17.02.17.
 */
public interface JDBCConnector {
    JDBCConnector setDb(DB db);

    JDBCConnector connect() throws SQLException;

    String getDbPassword();

    JDBCConnector setDbPassword(String dbPassword);

    String getDbUser();

    JDBCConnector setDbUser(String dbUser);

    String getDbUrl();

    JDBCConnector setDbUrl(String dbUrl);

    String getDbName();

    JDBCConnector setDbName(String dbName);

    JDBCConnector executeReadQuery() throws SQLException;

    void writeSample(Sample sample) throws SQLException;

    Collection<Sample> readSamples() throws SQLException;

    String getDbDriverName();

    JDBCConnector setDbDriver(String dbDriver);

    String getDbTable();

    void setDbTable(String dbTable);

    public enum DB {
        MYSQL, POSTGRES, ORACLE, H2;
    }
}
