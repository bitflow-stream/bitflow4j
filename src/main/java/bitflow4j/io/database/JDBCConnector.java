package bitflow4j.io.database;

import bitflow4j.sample.Sample;

import java.sql.SQLException;

/**
 * Created by malcolmx on 17.02.17.
 */
public interface JDBCConnector {
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

//    Collection<Sample> readSamples() throws SQLException;

    DB getDb();

    JDBCConnector setDb(DB db);

    String getDbTable();

    void setDbTable(String dbTable);

    Sample nextSample() throws SQLException;

    public enum DB {
        MYSQL(),//("com.mysql.jdbc.Driver"),
        POSTGRES(),//("org.postgresql.Driver"),
        ORACLE()//("oracle.jdbc.driver.OracleDriver")
        , H2()//("org.h2.Driver")
        , SQLite()//("");
//        private String driver;
//
//        DB(String driver) {
//            this.driver = driver;
//        }

//        public String getDriver() {
//            return driver;
//        }
    }
}
