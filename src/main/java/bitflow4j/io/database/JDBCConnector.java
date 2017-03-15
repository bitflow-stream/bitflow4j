package bitflow4j.io.database;

import bitflow4j.sample.Header;
import bitflow4j.sample.Sample;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by malcolmx on 17.02.17.
 */
public class JDBCConnector {

    //TODO fix case sensitivity
    //    private static final String BASE_ALTER_QUERY = "ALTER TABLE public.\"Samples\" ADD COLUMN tags text;";
    private static final String TIMESTAMP_COL = "timestamp";
    private static final String TAG_COL = "tags";
    private static final String BASE_INSERT_STATEMENT = "INSERT INTO %s (\"%s\",\"" + TIMESTAMP_COL + "\",\"" + TAG_COL + "\") VALUES (%s);";
    private static final String BASE_SELECT_STATEMENT = "SELECT * FROM %s;";
    private static final String BASE_CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS %s (\"timestamp\" %s,\"tags\" %s);";
    private static final String BASE_ALTER_STATEMENT = "ALTER TABLE %s %s;";

    private static final Logger logger = Logger.getLogger(JDBCConnector.class.getName());
    private static final char LINE_SEPERATOR = '\n';
    private int selectNumberOfColumns;

    private Sample lastWrittenSample = null;
    private String dbSchemaSelect;
    private String dbSchemaInsert;
    private Header header;
    //    private List<String> currentColumns;
    private ResultSetMetaData selectResultSetMetaData;
    private State state;
    private DB db;
    //    private String dbName;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private ResultSet selectResultSet;
    private String sqlSelectStatement;
    private String dbTableSelect;
    private String dbTableInsert;
    private Connection connection;
    //    private Mode mode;
    private String insertTableQualifier;
    private String selectTableQualifier;

    public JDBCConnector(DB db, String dbUrl, String dbUser, String dbPassword, String dbSchemaSelect, String dbSchemaInsert, String dbTableSelect, String dbTableInsert) {
        this.dbSchemaSelect = dbSchemaSelect; //TODO add schema to other methods
        this.dbSchemaInsert = dbSchemaInsert; //TODO add schema to other methods
//        this.dbName = dbName.toLowerCase();
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.db = db;
        if (dbTableSelect != null) this.dbTableSelect = dbTableSelect.toLowerCase();
        if (dbTableInsert != null) this.dbTableSelect = dbTableInsert.toLowerCase();
        this.init();
    }

    public JDBCConnector() {
        this.init();
    }

    //####################################################
    //                 SELECT
    //####################################################

    public Sample nextSample() throws SQLException {
        return processSelectionRow();
    }

    private Sample processSelectionRow() throws SQLException {
        return this.selectResultSet.next() ? parseSelectionRow() : null;
    }

    private Sample parseSelectionRow() throws SQLException {
        double[] values;
        Date timestamp = new Date(this.selectResultSet.getLong(TIMESTAMP_COL));
        String tagString = this.selectResultSet.getString(TAG_COL);
        Map<String, String> tags = parseTagString(tagString);
        values = new double[selectNumberOfColumns - 2];
        this.makeValues(values);
        Sample resultSample = new Sample(header, values, timestamp, tags);
        return resultSample;
    }

    private void makeValues(double[] values) throws SQLException {
        for (int i = 3; i <= selectNumberOfColumns; i++) {
            values[i - 3] = this.selectResultSet.getDouble(i);
        }
    }

    private Map<String, String> parseTagString(String encodedTags) {
        String[] tagTokens = encodedTags.split("(,)|(=)");
        //unsafe for malformatted tagStrings
        Map<String, String> result = new HashMap<>(tagTokens.length / 2);
        for (int i = 0; i < tagTokens.length / 2; i++) {
            result.put(tagTokens[i * 2], tagTokens[(i * 2) + 1]);
        }
        return result;
    }

    public JDBCConnector prepareRead() throws SQLException {
        this.sqlSelectStatement = String.format(BASE_SELECT_STATEMENT, selectTableQualifier);
        System.out.println("SQL Select Statement: " + sqlSelectStatement.toString());
        this.selectResultSet = executeQuery(sqlSelectStatement);
        this.selectResultSetMetaData = selectResultSet.getMetaData();
        this.selectNumberOfColumns = selectResultSetMetaData.getColumnCount();
        this.header = parseHeader();
        if (this.selectResultSet == null) logger.severe("ERROR while executing query: result set null");
        return this;
    }

    //####################################################
    //                 INSERT
    //####################################################
    //TODO lock table
    public void writeSample(Sample sample) throws SQLException {
        if (lastWrittenSample == null || sample.headerChanged(lastWrittenSample.getHeader())) {
            List<String> newColumns = checkTableColumns(sample);
            if (!newColumns.isEmpty()) addColumns(newColumns);
        }

        String valuesToInsert = buildValueString(sample);
        String columnsToInsert = buildColumnStrings(sample);
        String query = String.format(BASE_INSERT_STATEMENT, insertTableQualifier, columnsToInsert, valuesToInsert);
        System.out.println("query String: " + query);
        executeQuery(query);
        lastWrittenSample = sample;
        //TODO parse and handle result (e.g. any errors)
    }

    private Header parseHeader() throws SQLException {
        String[] header = new String[selectNumberOfColumns - 2];
        for (int i = 1; i <= selectNumberOfColumns - 2; i++) {
            String columnName = selectResultSetMetaData.getColumnName(i);
            header[i - 1] = columnName;
        }
        return new Header(header);
    }

    private String buildColumnStrings(Sample sample) {
        //TODO fix illegal characters for column names
//        StringBuilder resultBuilder = new StringBuilder();
        return String.join("\",\"", (CharSequence[]) (sample.getHeader().header));
    }

    private String buildValueString(Sample sample) {
        StringBuilder resultBuilder = new StringBuilder();
//        String parsedTags = buildTagString(sample.getTags());
        for (double metric : sample.getMetrics()) {
            resultBuilder.append(metric);
            resultBuilder.append(",");
        }
        resultBuilder.append(sample.getTimestamp().getTime());
        resultBuilder.append(",");
        resultBuilder.append(buildTagString(sample.getTags()));
        return resultBuilder.toString();
    }

    private String buildTagString(Map<String, String> tags) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("\'");
        tags.entrySet().forEach(entry -> {
            resultBuilder.append(entry.getKey());
            resultBuilder.append("=");
            resultBuilder.append(entry.getValue());
            resultBuilder.append(",");
        });
        //TODO not "clean"
        int lastIndexofSeparator = resultBuilder.lastIndexOf(",");
        resultBuilder.delete(lastIndexofSeparator, lastIndexofSeparator + 1);
        //clean
        resultBuilder.append("\'");
        return resultBuilder.toString();
    }

    public JDBCConnector prepareInsert() throws SQLException {
        this.createTable();
        return this;
    }


    //####################################################
    //                 CREATE
    //####################################################

    private void createTable() throws SQLException {
        String query = String.format(BASE_CREATE_STATEMENT, this.insertTableQualifier, db.longType(), db.stringType());
        //TODO use correct execute and handle result: fix later
//        ResultSet resultSet =
        executeQuery(query);
    }

    //####################################################
    //                 ALTER
    //####################################################

    private List<String> checkTableColumns(Sample sample) throws SQLException {
        ResultSet resultSet = connection.getMetaData().getColumns(null, this.dbSchemaInsert, this.dbTableInsert, null);//this.dbTableInsert
        List<String> columns = new ArrayList<>(resultSet.getFetchSize());
        List<String> sampleColumns = new ArrayList<>(Arrays.asList(sample.getHeader().header));
        System.out.println("printing column result");
        while (resultSet.next()) {
            String currColumn = resultSet.getString("COLUMN_NAME");
            System.out.println(currColumn);
            columns.add(currColumn);
        }
        System.out.println("In check table columns ");
        System.out.println("sample columns");
        for (String s : sampleColumns) System.out.println(s);
        System.out.println("table columns");
        for (String s : columns) System.out.println(s);
        sampleColumns.removeAll(columns);
        System.out.println("result columns");
        for (String s : sampleColumns) System.out.println(s);
        return sampleColumns;
    }


    private void addColumns(List<String> columns) throws SQLException {
        buildColumnStrings(columns);
        for (String columnToAdd : columns) {
            String query = String.format(BASE_ALTER_STATEMENT, insertTableQualifier, columnToAdd);
            System.out.println("add columns query: " + query);
            try {
                ResultSet resultSet = executeQuery(query);
            } catch (SQLException e) {
                logger.severe(e.getMessage());//TODO replace after manual table query has been added
            }
        }
        //TODO change to update
    }

    private void buildColumnStrings(List<String> columns) {
//        String[] resultQueries = new String[columns.size()];
        String columnType = db.doubleType();
        for (int i = 0; i < columns.size(); i++) {
            String columnString = "ADD \"" + columns.get(i) + "\" " + columnType;
            System.out.println("buildColumns for alter, column string: " + columnString);
            columns.set(i, columnString);
//  resultBuilder.append(",");
        }
//        resultBuilder.deleteCharAt(resultBuilder.length() - 1);
//        int lastIndexofSeparator = resultBuilder.lastIndexOf(",");
//        resultBuilder.delete(lastIndexofSeparator, lastIndexofSeparator + 1);
//        return columns; //resultBuilder.toString();
        return;
    }

    //####################################################
    //                 ALL
    //####################################################

    private synchronized ResultSet executeQuery(String sqlQuery) throws SQLException {
        Statement sqlStatement = null;
        sqlStatement = connection.createStatement();
        sqlStatement.execute(sqlQuery);
        return sqlStatement.getResultSet();
    }

    //####################################################
    //                 GETTER
    //####################################################

    public String getSelectTableQualifier() {
        return this.selectTableQualifier;
    }

    public String getInsertTableQualifier() {
        return this.insertTableQualifier;
    }

    public String getDbSchemaSelect() {
        return this.dbSchemaSelect;
    }

    public JDBCConnector setDbSchemaSelect(String schema) {
        this.dbSchemaSelect = schema.toLowerCase();
        buildSelectTableQualifier();
        return this;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public JDBCConnector setDbPassword(String dbPassword) {
        if (this.state == State.CONNECTED || this.state == State.READY)
            throw new IllegalStateException("Cannot change dbPassword while connected to db. Disconnect first and then reconnect.");
        this.dbPassword = dbPassword;
        return this;
    }

    public String getDbUser() {
        return dbUser;
    }

    public JDBCConnector setDbUser(String dbUser) {
        if (this.state == State.CONNECTED || this.state == State.READY)
            throw new IllegalStateException("Cannot change dbUser while connected to db. Disconnect first and then reconnect.");
        this.dbUser = dbUser;
        return this;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public JDBCConnector setDbUrl(String dbUrl) {
        if (this.state == State.CONNECTED || this.state == State.READY)
            throw new IllegalStateException("Cannot change dbUrl while connected to db. Disconnect first and then reconnect.");
        this.dbUrl = dbUrl;
        return this;
    }

    //####################################################

    //####################################################
    //                 SETTER

//    public String getDbName() {
//        return dbName;
//    }

//    public JDBCConnector setDbName(String dbName) {
//        if (this.state == State.CONNECTED || this.state == State.READY)
//            throw new IllegalStateException("Cannot change dbName while connected to db. Disconnect first and then reconnect.");
//        this.dbName = dbName;
//        return this;
//    }

    public DB getDb() {
        return db;
    }

    public JDBCConnector setDb(DB db) {
        this.db = db;
        return this;
    }

    public String getDbTableSelect() {
        return dbTableSelect;
    }

    public void setDbTableSelect(String dbTableSelect) {
        if (this.state == State.CONNECTED || this.state == State.READY)
            throw new IllegalStateException("Cannot change dbTableSelect while connected to db. Disconnect first and then reconnect.");
        this.dbTableSelect = dbTableSelect.toLowerCase();
        buildSelectTableQualifier();
    }

    public String getDbTableInsert() {
        return dbTableInsert;
    }

    public void setDbTableInsert(String dbTableInsert) {
        this.dbTableInsert = dbTableInsert.toLowerCase();
        this.buildInsertTableQualifier();
    }

    public JDBCConnector setDbSchemaInsert(String schema) {
        this.dbSchemaInsert = schema.toLowerCase();
        buildInsertTableQualifier();
        return this;
    }

    //####################################################
    //                 GENERAL
    //####################################################

    private void buildInsertTableQualifier() {
        StringBuilder table = new StringBuilder();
        if (this.dbSchemaInsert != null) {
            table.append(this.dbSchemaInsert);
            table.append(".");
        }
        table.append(this.dbTableInsert);
        this.insertTableQualifier = table.toString();
    }

    private void buildSelectTableQualifier() {
        StringBuilder table = new StringBuilder();
        if (this.dbSchemaSelect != null) {
            table.append(this.dbSchemaSelect);
            table.append(".");
        }
//        table.append("\"");
        table.append(this.dbTableSelect);
//        table.append("\"");
        this.selectTableQualifier = table.toString();
    }

    private void init() {
//        if (this.dbName == null) this.dbName = "bitflow4j-sample-db";
        if (this.dbTableSelect == null) this.dbTableSelect = "SamplesIn";
        if (this.dbTableInsert == null) this.dbTableInsert = "SamplesOut";
        if (this.dbUser == null) this.dbUser = "root";
        if (this.dbPassword == null) this.dbPassword = "";
        if (this.dbUrl == null) this.dbUrl = "jdbc:h2:~/bitflow4j-sample-db";

        this.buildInsertTableQualifier();
        this.buildSelectTableQualifier();
        this.state = State.INITIALIZED;
    }

    public JDBCConnector connect() throws SQLException, IllegalStateException {
//        if (state != State.INITIALIZED) this.init();
        if (state == State.CONNECTED) return this;
        this.connection = DriverManager.getConnection(this.dbUrl, this.dbUser, this.dbPassword);
        this.state = State.CONNECTED;
        return this;
    }

    public JDBCConnector disconnect() throws SQLException {
        if (this.state != State.CONNECTED) return this;
        this.connection.close();
        this.state = State.INITIALIZED;
        return this;
    }
//
//    private boolean checkIfTableExists() throws SQLException {
//        ResultSet resultSet = connection.getMetaData().getTables(null, this.dbSchemaSelect, this.dbTableSelect, new String[]{"TABLE"});
//        if (resultSet.next()) {
//            if (resultSet.getString("TABLE_NAME").equals(this.dbTableSelect)) return true;
//        }
//        return false;
//    }

    @Override
    public String toString() {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("#JDBCConnectorImpl#\n");
        resultBuilder.append("state: ");
        resultBuilder.append(state);
        resultBuilder.append(LINE_SEPERATOR);
        resultBuilder.append("db: ");
        resultBuilder.append(this.db);
        resultBuilder.append(LINE_SEPERATOR);
//        resultBuilder.append("database name: ");
//        resultBuilder.append(this.dbName);
//        resultBuilder.append(LINE_SEPERATOR);
        resultBuilder.append("db user: ");
        resultBuilder.append(dbUser);
        resultBuilder.append(LINE_SEPERATOR);
        resultBuilder.append("db password: ");
        resultBuilder.append(dbPassword);
        resultBuilder.append(LINE_SEPERATOR);
        resultBuilder.append("db url: ");
        resultBuilder.append(dbUrl);
        resultBuilder.append(LINE_SEPERATOR);
        resultBuilder.append("db table: ");
        resultBuilder.append(dbTableSelect);
        resultBuilder.append(LINE_SEPERATOR);
        resultBuilder.append("select statement: ");
        resultBuilder.append(sqlSelectStatement);
        resultBuilder.append(LINE_SEPERATOR);
        resultBuilder.append("connection: ");
        resultBuilder.append(connection == null ? "null" : connection.toString());
        resultBuilder.append(LINE_SEPERATOR);
        resultBuilder.append("select result set: ");
        resultBuilder.append(selectResultSet == null ? "null" : selectResultSet.toString());
        return resultBuilder.toString();
    }

    public enum State {
        INITIALIZED, CONNECTED, READY
    }

    public enum Mode {
        R, W, RW
    }

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
