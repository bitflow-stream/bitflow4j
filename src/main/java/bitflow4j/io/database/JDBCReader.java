package bitflow4j.io.database;

import bitflow4j.sample.Header;
import bitflow4j.sample.Sample;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by malcolmx on 21.03.17.
 */
public class JDBCReader extends Connector<JDBCReader> {
    private static final String BASE_SELECT_STATEMENT = "SELECT * FROM %s;";
    private static final Logger logger = Logger.getLogger(JDBCReader.class.getName());
    private ResultSet selectResultSet;
    private String sqlSelectQuery;
    private ResultSetMetaData selectResultSetMetaData;
    private int selectNumberOfColumns;
    private Header header;

    public JDBCReader(DB db, String url, String schema, String table, String user, String password) {
        super(db, url, schema, table, user, password);
    }

    public Sample nextSample() throws SQLException {
        return processSelectionRow();
    }

    public Connector prepareRead() throws SQLException {
        this.sqlSelectQuery = String.format(BASE_SELECT_STATEMENT, tableQualifier);
//        System.out.println("SQL Select Statement: " + sqlSelectQuery);
        this.selectResultSet = executeQuery(sqlSelectQuery);
        this.selectResultSetMetaData = selectResultSet.getMetaData();
        this.selectNumberOfColumns = selectResultSetMetaData.getColumnCount();
        this.header = parseHeader();
        if (this.selectResultSet == null) logger.severe("ERROR while executing query: result set null");
        return this;
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
//        Sample resultSample = new Sample(header, values, timestamp, tags);
        return new Sample(header, values, timestamp, tags); //resultSample;
    }

    private void makeValues(double[] values) throws SQLException {
        for (int i = 3; i <= selectNumberOfColumns; i++) {
            values[i - 3] = this.selectResultSet.getDouble(i);
        }
    }

    private Map<String, String> parseTagString(String encodedTags) { //TODO check if it is ok to return empty map or if null must be given
        String[] tagTokens = encodedTags.split("(,)|(=)");
        //unsafe for malformatted tagStrings
        Map<String, String> result = new HashMap<>(tagTokens.length / 2);
        for (int i = 0; i < tagTokens.length / 2; i++) {
            result.put(tagTokens[i * 2], tagTokens[(i * 2) + 1]);
        }
        return result;
    }

    private Header parseHeader() throws SQLException {
        String[] header = new String[selectNumberOfColumns - 2];
        for (int i = 1; i <= selectNumberOfColumns - 2; i++) {
            String columnName = selectResultSetMetaData.getColumnName(i);
            header[i - 1] = columnName;
        }
        return new Header(header);
    }

}
