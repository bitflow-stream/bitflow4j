package bitflow4j.io.sql;

import bitflow4j.Header;
import bitflow4j.Sample;

import java.io.IOException;
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
public class JDBCReader extends Connector {

    private static final String BASE_SELECT_STATEMENT = "SELECT * FROM %s;";
    private static final Logger logger = Logger.getLogger(JDBCReader.class.getName());
    private ResultSet selectResultSet;
    private ResultSetMetaData selectResultSetMetaData;
    private int selectNumberOfColumns;
    private Header header;

    public JDBCReader(String url, String schema, String table, String user, String password) {
        super(url, schema, table, user, password);
    }

    @Override
    public JDBCReader connect() throws SQLException {
        super.connect();
        return this;
    }

    @Override
    public JDBCReader disconnect() throws SQLException {
        super.disconnect();
        return this;
    }

    @Override
    public JDBCReader reconnect() throws SQLException {
        super.reconnect();
        return this;
    }

    public Sample nextSample() throws SQLException, IOException {
        return processSelectionRow();
    }

    public Connector prepareRead() throws SQLException {
        String sqlSelectQuery = String.format(BASE_SELECT_STATEMENT, tableQualifier);
        this.selectResultSet = executeQuery(sqlSelectQuery);
        this.selectResultSetMetaData = selectResultSet.getMetaData();
        this.selectNumberOfColumns = selectResultSetMetaData.getColumnCount();
        this.header = parseHeader();
        if (this.selectResultSet == null) logger.severe("ERROR while executing query: result set null");
        return this;
    }

    private Sample processSelectionRow() throws SQLException, IOException {
        if (this.selectResultSet.next()) {
            return parseSelectionRow();
        }
        this.selectResultSet.close();
        return null;
    }

    private Sample parseSelectionRow() throws SQLException, IOException {
        double[] values;
        Date timestamp = new Date(this.selectResultSet.getLong(TIMESTAMP_COL));
        String tagString = this.selectResultSet.getString(TAG_COL);
        Map<String, String> tags = Sample.parseTags(tagString);
        values = new double[selectNumberOfColumns - 2];
        this.makeValues(values);
        return new Sample(header, values, timestamp, tags);
    }

    private void makeValues(double[] values) throws SQLException {
        for (int i = 3; i <= selectNumberOfColumns; i++) {
            values[i - 3] = this.selectResultSet.getDouble(i);
        }
    }

    private Map<String, String> parseTagString(String encodedTags) {
        String[] tagTokens = encodedTags.split("(,)|(=)");
        // TODO unsafe for tagStrings with wrong format
        Map<String, String> result = new HashMap<>(tagTokens.length / 2);
        for (int i = 0; i < tagTokens.length / 2; i++) {
            result.put(tagTokens[i * 2], tagTokens[(i * 2) + 1]);
        }
        return result;
    }

    private Header parseHeader() throws SQLException {
        String[] header = new String[selectNumberOfColumns - 2];
        for (int i = 3; i <= selectNumberOfColumns; i++) {
            String columnName = selectResultSetMetaData.getColumnName(i);
            header[i - 3] = columnName;
        }
        return new Header(header);
    }

}
