package bitflow4j.io.sql;

import bitflow4j.Sample;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by malcolmx on 21.03.17.
 */
public class JDBCWriter extends Connector {

    private static final Logger logger = Logger.getLogger(JDBCWriter.class.getName());

    private static final String BASE_INSERT_STATEMENT = "INSERT INTO %s (%s" + TIMESTAMP_COL + "," + TAG_COL + ") VALUES (%s);";
    private static final String BASE_CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS %s (" + TIMESTAMP_COL + " %s," + TAG_COL + " %s);";
    private static final String BASE_ALTER_STATEMENT = "ALTER TABLE %s %s;";
    private static final String BASE_ALTER_STATEMENT2 = "ALTER TABLE %s;";

    private Sample lastWrittenSample;
    private boolean readyToInsert = false;

    public JDBCWriter(String url, String schema, String table, String user, String password) {
        super(url, schema, table, user, password);
    }

    @Override
    public JDBCWriter connect() throws SQLException {
        super.connect();
        return this;
    }

    @Override
    public JDBCWriter disconnect() throws SQLException {
        super.disconnect();
        return this;
    }

    @Override
    public JDBCWriter reconnect() throws SQLException {
        super.reconnect();
        return this;
    }

    public void writeSample(Sample sample) throws SQLException {
        if (lastWrittenSample == null || sample.headerChanged(lastWrittenSample.getHeader())) {
            List<String> newColumns = checkTableColumns(sample);
            if (!newColumns.isEmpty()) {
                if (db.equals(DB.SQLite)) addColumns(newColumns);
                else addColumns2(newColumns);
            }
        }

        String valuesToInsert = buildValueString(sample);
        String columnsToInsert = buildColumnStrings(sample);
        String query = String.format(BASE_INSERT_STATEMENT, tableQualifier, columnsToInsert, valuesToInsert);
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, sample.tagString());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        lastWrittenSample = sample;
    }

    public JDBCWriter prepareInsert() throws SQLException {
        if (readyToInsert) return this;
        this.createTable();
        readyToInsert = true;
        return this;
    }

    private String buildColumnStrings(Sample sample) {
        String[] currentHeader = sample.getHeader().header;
        return (currentHeader == null || currentHeader.length == 0) ? "" : db.escapeCharacter() + String.join(db.escapeCharacter() + "," + db.escapeCharacter(), (CharSequence[]) (currentHeader)) + db.escapeCharacter() + ",";
    }

    private String buildValueString(Sample sample) {
        StringBuilder resultBuilder = new StringBuilder();
        for (double metric : sample.getMetrics()) {
            resultBuilder.append(metric);
            resultBuilder.append(",");
        }
        resultBuilder.append(sample.getTimestamp().getTime());
        resultBuilder.append(",");
        resultBuilder.append("?");
        return resultBuilder.toString();
    }

    private String buildTagString(Map<String, String> tags) {
        StringBuilder resultBuilder = new StringBuilder();
        tags.forEach((key, value) -> {
            resultBuilder.append(key);
            resultBuilder.append("=");
            resultBuilder.append(value);
            resultBuilder.append(",");
        });
        // TODO not "clean"
        int lastIndexOfSeparator = resultBuilder.lastIndexOf(",");
        if (lastIndexOfSeparator >= 0) resultBuilder.delete(lastIndexOfSeparator, lastIndexOfSeparator + 1);
        return resultBuilder.toString();
    }

    //####################################################
    //                 CREATE
    //####################################################

    private void createTable() throws SQLException {
        String query = String.format(BASE_CREATE_STATEMENT, this.tableQualifier, db.longType(), db.stringType());
        System.out.println("create table query" + query);
        executeUpdate(query);
    }

    //####################################################
    //                 ALTER
    //####################################################

    private List<String> checkTableColumns(Sample sample) throws SQLException {
        ResultSet resultSet = connection.getMetaData().getColumns(null, this.schema, this.table, null);
        // this.dbTableInsert TODO: replace with manual query
        List<String> columns = new ArrayList<>(resultSet.getFetchSize());
        List<String> sampleColumns = new ArrayList<>(Arrays.asList(sample.getHeader().header));
        while (resultSet.next()) {
            String currColumn = resultSet.getString("COLUMN_NAME");
            columns.add(currColumn);
        }
        resultSet.close();
        sampleColumns.removeAll(columns);
        return sampleColumns;
    }

    private void addColumns(List<String> columns) throws SQLException {
        buildColumnStrings(columns);
        for (String columnToAdd : columns) {
            String query = String.format(BASE_ALTER_STATEMENT, tableQualifier, columnToAdd);
            try {
                executeUpdate(query);
            } catch (SQLException e) {
                logger.severe(e.getMessage()); // TODO replace after manual table query has been added
            }
        }
    }

    private void addColumns2(List<String> columns) throws SQLException {
        buildColumnStrings2(columns);
        String alterColumnsString;
        String delimiter = " " + db.doubleType() + ", ADD COLUMN ";
        alterColumnsString = tableQualifier + " ADD COLUMN " + String.join(delimiter, columns) + " " + db.doubleType();
        try {
            executeUpdate(String.format(BASE_ALTER_STATEMENT2, alterColumnsString));
        } catch (SQLException e) {
            logger.severe(e.getMessage()); // TODO replace after manual table query has been added
        }
    }

    private void buildColumnStrings(List<String> columns) {
        String columnType = db.doubleType();
        for (int i = 0; i < columns.size(); i++) {
            String columnString = "ADD " + db.escapeCharacter() + columns.get(i) + db.escapeCharacter() + " " + columnType;
            columns.set(i, columnString);
        }
    }

    private void buildColumnStrings2(List<String> columns) {
        for (int i = 0; i < columns.size(); i++) {
            String columnString = db.escapeCharacter() + columns.get(i) + db.escapeCharacter();
            columns.set(i, columnString);
        }
    }
}
