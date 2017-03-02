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


    private static final String BASE_INSERT_STATEMENT = "INSERT INTO %s (%s) VALUES (%s);";
    private static final String BASE_SELECT_STATEMENT = "SELECT * FROM %s;";
    //    private static final String BASE_ALTER_QUERY = "ALTER TABLE public.\"Samples\" ADD COLUMN tags text;";
    private static final String TIMESTAMP_COL = "timestamp";
    private static final String TAG_COL = "tags";
    private static final Logger logger = Logger.getLogger(JDBCConnectorImpl.class.getName());
    private static final char LINE_SEPERATOR = '\n';
    private State state;
    private DB db;
    private String dbName;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private ResultSet selectResultSet;
    private PreparedStatement stm;
    //todo h2
    private String sqlSelectStatement;
    private String dbTable;
    private Connection connection;
    private Collection<Mode> mode;

    public JDBCConnectorImpl(DB db, String dbName, String dbUrl, String dbUser, String dbPassword, String dbTable) {
        this.dbName = dbName;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.db = db;
        if (dbTable != null) this.dbTable = dbTable;
        this.init();
    }

    public JDBCConnectorImpl() {
        this.init();
    }

    private boolean canRead() {
        //TODO
        return false;
    }

    private boolean canWrite() {
        //TODO
        return false;
    }
    /**
     * Initializes all empty fields
     */
    private void init() {
        if (this.dbName == null) this.dbName = "bitflow4j-sample-db";
        if (this.dbTable == null) this.dbTable = "Samples";
        if (this.dbUser == null) this.dbUser = "root";
        if (this.dbPassword == null) this.dbPassword = "";
        if (this.dbUrl == null) this.dbUrl = "jdbc:h2:~/bitflow4j-sample-db";
//        this.sqlInsertStatement = String.format(dbTable, BASE_INSERT_STATEMENT);
        this.state = State.INITIALIZED;
    }

    @Override
    public JDBCConnector setDb(DB db) {
        this.db = db;
//        switch (db) {
//            case MYSQL:
//                prepareMSQLDb();
//                break;
//            case POSTGRES:
//                preparePostgresDb();
//                break;
//            case ORACLE:
//                prepareOracleDb();
//                break;
//            case H2:
//                prepareH2Db();
//            default:
//        }
//        try {
//            Class.forName(this.dbDriverName);
//        } catch (ClassNotFoundException e) {
//            logger.severe(e.getMessage());
//        }
        return this;
    }

//    private void prepareH2Db() {
//        this.dbDriverName = H2_DRIVER;
//
//    }
//
//    private void prepareOracleDb() {
//        this.dbDriverName = ORACLE_DRIVER;
//    }
//
//    private void preparePostgresDb() {
//        this.dbDriverName = POSTGRES_DRIVER;
//    }
//
//    private void prepareMSQLDb() {
//        this.dbDriverName = MYSQL_DRIVER;
//    }

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

    @Override
    public JDBCConnector executeReadQuery() throws SQLException {
        System.out.println("executing query");
        this.sqlSelectStatement = String.format(BASE_SELECT_STATEMENT, dbTable);
        this.selectResultSet = executeQuery(sqlSelectStatement);
        if (this.selectResultSet == null) System.out.println("ERROR: result set null");
        return this;
    }

    private synchronized ResultSet executeQuery(String sqlQuery) throws SQLException {
        Statement sqlStatement = null;
        sqlStatement = connection.createStatement();
        sqlStatement.execute(sqlQuery);
        return sqlStatement.getResultSet();
    }

    public void writeSample(Sample sample) throws SQLException {
        String valuesToInsert = buildValueString(sample);
        String columnsToInsert = buildColumnString(sample);
        String query = String.format(dbTable, valuesToInsert, BASE_INSERT_STATEMENT);
        ResultSet resultSet = executeQuery(query);
        //TODO parse and handle result (e.g. any errors)
    }

    private String buildColumnString(Sample sample) {
        //TODO fix illegal characters for column names
        StringBuilder resultBuilder = new StringBuilder();
        return String.join(",", sample.getHeader().header);
    }

    private String buildValueString(Sample sample) {
        StringBuilder resultBuilder = new StringBuilder();
        String parsedTags = buildTagString(sample.getTags());
//        for (double metric : sample.getMetrics()) {
//            resultBuilder.append(metric);
//            resultBuilder.append(",");
//        }
        resultBuilder.append(sample.getTimestamp().getTime());
        resultBuilder.append(",");
        resultBuilder.append(parsedTags);
        return resultBuilder.toString();
    }

    public Collection<Sample> readSamples() throws SQLException {
//        String query = String.format(dbTable, BASE_SELECT_STATEMENT);
//        ResultSet resultSet = executeQuery(this.sqlSelectStatement);
        System.out.println("reading samples");
        System.out.println("SQL statement: " + sqlSelectStatement);
        return parseSelectionResult(this.selectResultSet);
    }

    private Collection<Sample> parseSelectionResult(ResultSet resultSet) throws SQLException {
        System.out.println("parsing selection result");
        if (resultSet == null) {
            System.out.println("ERROR: empty resultset in parseSelectionResult()");
            return null;
        }
        List<Sample> result = new ArrayList<>(resultSet.getFetchSize());
        while (resultSet.next()) {
            Sample sampleFromRow = parseSelectionRow(resultSet);
            result.add(sampleFromRow);
        }
        return result;
    }

    private Sample parseSelectionRow(ResultSet resultSet) throws SQLException {
//        System.out.println("parsing selection row");
        Header header;
        String[] headerStrings;
        double[] values;
        Date timestamp = null;
        Map<String, String> tags = null;

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int numberOfColumns = resultSetMetaData.getColumnCount();
        headerStrings = new String[numberOfColumns - 2];
        values = new double[numberOfColumns - 2];
//        System.out.println("number of columns: " + numberOfColumns);
        int offset = 0;
        //TODO timestamp and tags will get fixed position at end
        for (int i = 1; i <= numberOfColumns; i++) {
            String columnName = resultSetMetaData.getColumnName(i);
            if (columnName.equals(TIMESTAMP_COL)) {
//                System.out.println("processing timestamp");
                timestamp = new Date(resultSet.getLong(i)); //TODO make sure to save Timestamp as Date?
//                System.out.println("timestamp: " + timestamp);
                offset += 1;
            } else if (columnName.equals(TAG_COL)) {
//                System.out.println("processing tag col");
                String tagString = resultSet.getString(i);
                tags = parseTagString(tagString);
//                System.out.println("tagstring: " + tagString);
//                System.out.println("#(tags): " + tags.size() + 1);
                offset += 1;
            } else {
//                System.out.println("processing col: " + columnName);
                headerStrings[i - 1 - offset] = columnName;
                values[i - 1 - offset] = resultSet.getDouble(i);

            }
        }
        header = new Header(headerStrings);
        Sample resultSample = new Sample(header, values, timestamp, tags);
//        System.out.println("Resulting sample: " + resultSample);
        return resultSample;
    }

    private Map<String, String> parseTagString(String encodedTags) {
        //TODO fix split
        String[] tagTokens = encodedTags.split("(,)|(=)");
        for (String s : tagTokens
            ) {
            System.out.println(s);
        }
        //unsafe for malformatted tagStrings
        Map<String, String> result = new HashMap<>(tagTokens.length / 2);
        for (int i = 0; i < tagTokens.length / 2; i++) {
            result.put(tagTokens[i * 2], tagTokens[(i * 2) + 1]);
        }
        return result;
    }

    private String buildTagString(Map<String, String> tags) {
        StringBuilder resultBuilder = new StringBuilder();
        tags.entrySet().forEach(entry -> {
            resultBuilder.append(entry.getKey());
            resultBuilder.append("=");
            resultBuilder.append(entry.getValue());
            resultBuilder.append(",");
        });
        //not clean
        int lastIndexofSeparator = resultBuilder.lastIndexOf(";");
        resultBuilder.delete(lastIndexofSeparator, lastIndexofSeparator + 1);
        return resultBuilder.toString();
    }

    @Override
    public JDBCConnector setDbName(String dbName) {
        if (this.state == State.CONNECTED)
            throw new IllegalStateException("Cannot change dbName while connected to db. Disconnect first and then reconnect.");
        this.dbName = dbName;
        return this;
    }

    @Override
    public JDBCConnector setDbUrl(String dbUrl) {
        if (this.state == State.CONNECTED)
            throw new IllegalStateException("Cannot change dbUrl while connected to db. Disconnect first and then reconnect.");
        this.dbUrl = dbUrl;
        return this;
    }

    @Override
    public JDBCConnector setDbUser(String dbUser) {
        if (this.state == State.CONNECTED)
            throw new IllegalStateException("Cannot change dbUser while connected to db. Disconnect first and then reconnect.");
        this.dbUser = dbUser;
        return this;
    }

    @Override
    public JDBCConnector setDbPassword(String dbPassword) {
        if (this.state == State.CONNECTED)
            throw new IllegalStateException("Cannot change dbPassword while connected to db. Disconnect first and then reconnect.");
        this.dbPassword = dbPassword;
        return this;
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

    @Override
    public String getDbTable() {
        return dbTable;
    }

    @Override
    public void setDbTable(String dbTable) {
        if (this.state == State.CONNECTED)
            throw new IllegalStateException("Cannot change dbTable while connected to db. Disconnect first and then reconnect.");
        this.dbTable = dbTable;
    }

    @Override
    public Sample nextSample() {
        //TODO
        return null;
    }

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
        resultBuilder.append(dbTable);
        resultBuilder.append(LINE_SEPERATOR);
        resultBuilder.append("driver: ");
        resultBuilder.append(db.getDriver());
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
        R, W
    }

}
