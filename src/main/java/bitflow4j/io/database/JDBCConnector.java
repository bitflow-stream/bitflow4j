package bitflow4j.io.database;

import bitflow4j.sample.Sample;

import java.sql.SQLException;

/**
 * Created by malcolmx on 17.02.17.
 */
public interface JDBCConnector {
    JDBCConnector connect() throws SQLException;

    JDBCConnector disconnect() throws SQLException;

    String getDbPassword();

    JDBCConnector setDbPassword(String dbPassword);

    String getDbUser();

    JDBCConnector setDbUser(String dbUser);

    String getDbUrl();

    JDBCConnector setDbUrl(String dbUrl);

    String getDbName();

    JDBCConnector setDbName(String dbName);

    JDBCConnector setDbSchemaInsert(String schema);

    JDBCConnector prepareRead() throws SQLException;

    void writeSample(Sample sample) throws SQLException;

//    Collection<Sample> readSamples() throws SQLException;

    DB getDb();

    JDBCConnector setDb(DB db);

    JDBCConnectorImpl prepareInsert() throws SQLException;

    String getDbSchemaSelect();

    JDBCConnector setDbSchemaSelect(String schema);

    String getDbTableSelect();

    void setDbTableSelect(String dbTableSelect);

    Sample nextSample() throws SQLException;

    void setDbTableInsert(String dbTableInsert);

    public enum DB {
        MYSQL("DOUBLE", "BIGINT", "TEXT"),//("com.mysql.jdbc.Driver"),
        POSTGRES("double precision", "bigint", "text"),//("org.postgresql.Driver"),
        //        ORACLE("real", "", ""),//("oracle.jdbc.driver.OracleDriver")   -- support maybe later, Strings allways have a max length
        H2("DOUBLE", "BIGINT", "VARCHAR")//("org.h2.Driver")
        , SQLite("REAL", "INTEGER", "TEXT")//("");
        ;
        private String doubleTypeString;

        //        public String getDriver() {
//            return driver;
//        }
        private String stringTypeString;
        private String longTypeString;

        //        private String driver;
//
        DB(String doubleTypeString, String longTypeString, String stringTypeString) {
//            this.driver = driver;
            this.doubleTypeString = doubleTypeString;
            this.longTypeString = longTypeString;
            this.stringTypeString = stringTypeString;
        }

        public String doubleType() {
            return this.doubleTypeString;
        }

        public String longType() {
            return this.longTypeString;
        }

        public String stringType() {
            return this.stringTypeString;
        }
    }
}
