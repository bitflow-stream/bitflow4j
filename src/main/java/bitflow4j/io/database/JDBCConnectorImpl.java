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
//TODO handle invalid characters in sample headers
public class JDBCConnectorImpl implements JDBCConnector {


    //    private static final String BASE_ALTER_QUERY = "ALTER TABLE public.\"Samples\" ADD COLUMN tags text;";
    private static final String TIMESTAMP_COL = "timestamp";
    private static final String TAG_COL = "tags";
    private static final String BASE_INSERT_STATEMENT = "INSERT INTO %s (\"%s\",\"" + TIMESTAMP_COL + "\",\"" + TAG_COL + "\") VALUES (%s);";
    private static final String BASE_SELECT_STATEMENT = "SELECT * FROM %s;";
    private static final String BASE_CREATE_STATEMENT = "CREATE TABLE IF NOT EXIST %s (\"timestamp\" %s,\"tags\" %s)";
    private static final String BASE_ALTER_STATEMENT = "ALTER TABLE %s ADD (%s) ";
    private static final Logger logger = Logger.getLogger(JDBCConnectorImpl.class.getName());
    private static final char LINE_SEPERATOR = '\n';
    private int selectNumberOfColumns;

    private Sample lastWrittenSample = null;
    private String dbSchemaSelect;
    private String dbSchemaInsert;
    private Header header;
    private List<String> currentColumns;
    private ResultSetMetaData selectResultSetMetaData;
    private State state;
    private DB db;
    private String dbName;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private ResultSet selectResultSet;
    private PreparedStatement preparedInserStatement;
    private PreparedStatement preparedSelectStatement;
    private PreparedStatement preparedCreateStatement;
    private String sqlSelectStatement;
    private String dbTableSelect;
    private String dbTableInsert;
    private Connection connection;
    private Mode mode;

    public JDBCConnectorImpl(DB db, String dbName, String dbUrl, String dbUser, String dbPassword, String dbSchema, String dbTable) {
        this.dbSchemaSelect = dbSchema; //TODO add schema to other methods
        this.dbName = dbName;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.db = db;
        if (dbTable != null) this.dbTableSelect = dbTable;
        this.init();
    }

    public JDBCConnectorImpl() {
        this.init();
    }

    //####################################################
    //                 SELECT
    //####################################################

    @Override
    public Sample nextSample() throws SQLException {
        return processSelectionRow();
    }

    private Sample processSelectionRow() throws SQLException {
        return this.selectResultSet.next() == true ? parseSelectionRow() : null;
    }

    private Sample parseSelectionRow() throws SQLException {
        double[] values;
        Date timestamp = null;
        Map<String, String> tags = null;
        values = new double[selectNumberOfColumns - 2];
        timestamp = new Date(this.selectResultSet.getLong(TIMESTAMP_COL));
        String tagString = this.selectResultSet.getString(TAG_COL);
        tags = parseTagString(tagString);
        this.makeValues(values);
        Sample resultSample = new Sample(header, values, timestamp, tags);
        return resultSample;
    }

    private void makeValues(double[] values) throws SQLException {
        for (int i = 3; i <= selectNumberOfColumns; i++) {
            values[i - 1] = this.selectResultSet.getDouble(i);
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

    @Override
    public JDBCConnector prepareRead() throws SQLException {
        this.sqlSelectStatement = String.format(BASE_SELECT_STATEMENT, dbTableSelect);
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
            checkTableColumns(sample);
        }

        String valuesToInsert = buildValueString(sample);
        String columnsToInsert = buildColumnString(sample);
        String query = String.format(BASE_INSERT_STATEMENT, dbTableInsert, columnsToInsert, valuesToInsert);
        System.out.println("query String: " + query);
        ResultSet resultSet = executeQuery(query);
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

    private String buildColumnString(Sample sample) {
        //TODO fix illegal characters for column names
        StringBuilder resultBuilder = new StringBuilder();
        return String.join("\",\"", sample.getHeader().header);
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

    public JDBCConnectorImpl prepareInsert() throws SQLException {
        this.createTable();
        return this;
    }


    //####################################################
    //                 CREATE
    //####################################################

    private void createTable() throws SQLException {
        String query = String.format(BASE_CREATE_STATEMENT, this.dbTableInsert, db.longType(), db.stringType());
//        PreparedStatement preparedStatement = connection.prepareStatement();
        //TODO use correct execute and handle result: fix later
        ResultSet resultSet = executeQuery(query);
    }

    //####################################################
    //                 ALTER
    //####################################################

    private boolean checkTableColumns(Sample sample) throws SQLException {
        String[] oldCols;
        String[] newCols;
        ResultSet resultSet = connection.getMetaData().getColumns(null, this.dbSchemaInsert, this.dbTableInsert, null);
        int i = 1;
        List<String> columns = new ArrayList<>(resultSet.getFetchSize());
        List<String> sampleColumns = Arrays.asList(sample.getHeader().header);
        while (resultSet.next()) {
//            String columnName = resultSet.getString(i);
//            if(i == 1 && !columnName.equals(TIMESTAMP_COL) || i==2 && !columnName.equals(TAG_COL)) return false;
            columns.add(resultSet.getString(i++));
        }
        findNewColumns(columns, sampleColumns);
        //TODO current wip and change return value
        return false;
    }

    private void findNewColumns(List<String> oldCols, List<String> newCols) {
        newCols.removeAll(oldCols);
    }

    private void addColumns(List<String> columns) throws SQLException {
        String columnsToAdd = buildColumnString(columns);
        String query = String.format(BASE_ALTER_STATEMENT, dbTableInsert, columnsToAdd);
        //TODO change to update
        ResultSet resultSet = executeQuery(query);
    }

    private String buildColumnString(List<String> columns) {
        StringBuilder resultBuilder = new StringBuilder();
        String columnType = db.doubleType();
        for (String column :
            columns) {
            resultBuilder.append("column");
            resultBuilder.append(" ");
            resultBuilder.append(columnType);
            resultBuilder.append(",");
        }
        resultBuilder.deleteCharAt(resultBuilder.length() - 1);
        String result = resultBuilder.toString();
        System.out.println("buildColumns for alter, column string: " + result);
        return result; //resultBuilder.toString();
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

    public String getDbSchemaSelect() {
        return this.dbSchemaSelect;
    }

    @Override
    public String getDbPassword() {
        return dbPassword;
    }

    @Override
    public String getDbUser() {
        return dbUser;
    }

    @Override
    public String getDbUrl() {
        return dbUrl;
    }

    @Override
    public String getDbName() {
        return dbName;
    }

    public DB getDb() {
        return db;
    }

    public String getDbTableSelect() {
        return dbTableSelect;
    }

    //####################################################
    //                 SETTER
    //####################################################

    public void setDbTableSelect(String dbTableSelect) {
        if (this.state == State.CONNECTED || this.state == State.READY)
            throw new IllegalStateException("Cannot change dbTableSelect while connected to db. Disconnect first and then reconnect.");
        this.dbTableSelect = dbTableSelect;
    }

    @Override
    public JDBCConnector setDb(DB db) {
        this.db = db;
        return this;
    }

    @Override
    public JDBCConnector setDbSchema(String schema) {
        this.dbSchemaSelect = schema;
        return this;
    }

    @Override
    public JDBCConnector setDbName(String dbName) {
        if (this.state == State.CONNECTED || this.state == State.READY)
            throw new IllegalStateException("Cannot change dbName while connected to db. Disconnect first and then reconnect.");
        this.dbName = dbName;
        return this;
    }

    @Override
    public JDBCConnector setDbUrl(String dbUrl) {
        if (this.state == State.CONNECTED || this.state == State.READY)
            throw new IllegalStateException("Cannot change dbUrl while connected to db. Disconnect first and then reconnect.");
        this.dbUrl = dbUrl;
        return this;
    }

    @Override
    public JDBCConnector setDbUser(String dbUser) {
        if (this.state == State.CONNECTED || this.state == State.READY)
            throw new IllegalStateException("Cannot change dbUser while connected to db. Disconnect first and then reconnect.");
        this.dbUser = dbUser;
        return this;
    }

    @Override
    public JDBCConnector setDbPassword(String dbPassword) {
        if (this.state == State.CONNECTED || this.state == State.READY)
            throw new IllegalStateException("Cannot change dbPassword while connected to db. Disconnect first and then reconnect.");
        this.dbPassword = dbPassword;
        return this;
    }

    //####################################################
    //                 GENERAL
    //####################################################
    private void init() {
        if (this.dbName == null) this.dbName = "bitflow4j-sample-db";
        if (this.dbTableSelect == null) this.dbTableSelect = "Samples";
        if (this.dbUser == null) this.dbUser = "root";
        if (this.dbPassword == null) this.dbPassword = "";
        if (this.dbUrl == null) this.dbUrl = "jdbc:h2:~/bitflow4j-sample-db";
//        this.sqlInsertStatement = String.format(dbTableSelect, BASE_INSERT_STATEMENT);
        this.state = State.INITIALIZED;
    }

    private boolean canRead() {
        //TODO
        return false;
    }

    private boolean canWrite() {
        //TODO add 2nd dbtable
        return false;
    }

    @Override
    public JDBCConnector connect() throws SQLException, IllegalStateException {
        if (state != State.INITIALIZED) this.init();
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
        resultBuilder.append("database name: ");
        resultBuilder.append(this.dbName);
        resultBuilder.append(LINE_SEPERATOR);
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

}
