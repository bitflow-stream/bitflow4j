package bitflow4j.io.database;

import bitflow4j.sample.Sample;

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
public class JDBCWriter extends Connector<JDBCWriter> {
    private static final String BASE_INSERT_STATEMENT = "INSERT INTO %s (%s" + TIMESTAMP_COL + "," + TAG_COL + ") VALUES (%s);";
    private static final String BASE_CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS %s (" + TIMESTAMP_COL + " %s," + TAG_COL + " %s);";
    private static final String BASE_ALTER_STATEMENT = "ALTER TABLE %s %s;";
    private static final String BASE_ALTER_STATEMENT2 = "ALTER TABLE %s;";
    private static final Logger logger = Logger.getLogger(JDBCWriter.class.getName());

    private Sample lastWrittenSample;
    private boolean readyToInsert = false;

    public JDBCWriter(String url, String schema, String table, String user, String password) {
        super(url, schema, table, user, password);
    }

    public void writeSample(Sample sample) throws SQLException {
        if (lastWrittenSample == null || sample.headerChanged(lastWrittenSample.getHeader())) {
            List<String> newColumns = checkTableColumns(sample);
            if (!newColumns.isEmpty()) {
                if(db.equals(DB.SQLite)) addColumns(newColumns);
                else addColumns2(newColumns);
            }
        }

        String valuesToInsert = buildValueString(sample);
        String columnsToInsert = buildColumnStrings(sample);
        String query = String.format(BASE_INSERT_STATEMENT, tableQualifier, columnsToInsert, valuesToInsert);
        PreparedStatement preparedStatement = connection.prepareStatement(query);
//        executeUpdate(query);
        preparedStatement.setString(1,sample.tagString());
//        preparedStatement.setString(1,buildTagString(sample.getTags()));
        preparedStatement.executeUpdate();
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
//        resultBuilder.append("\'");
        tags.entrySet().forEach(entry -> {
            resultBuilder.append(entry.getKey());
            resultBuilder.append("=");
            resultBuilder.append(entry.getValue());
            resultBuilder.append(",");
        });
        //TODO not "clean"
        int lastIndexofSeparator = resultBuilder.lastIndexOf(",");
        if (lastIndexofSeparator >= 0) resultBuilder.delete(lastIndexofSeparator, lastIndexofSeparator + 1);
        //clean
//        resultBuilder.append("\'");
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
        ResultSet resultSet = connection.getMetaData().getColumns(null, this.schema, this.table, null);//this.dbTableInsert TODO: replace with manual query
        List<String> columns = new ArrayList<>(resultSet.getFetchSize());
        List<String> sampleColumns = new ArrayList<>(Arrays.asList(sample.getHeader().header));
        while (resultSet.next()) {
            String currColumn = resultSet.getString("COLUMN_NAME");
            columns.add(currColumn);
        }
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
                logger.severe(e.getMessage());//TODO replace after manual table query has been added
            }
        }
    }

    private void addColumns2(List<String> columns) throws SQLException {
        System.out.println(columns);
        buildColumnStrings2(columns);
        System.out.println(columns);
        String alterColumnsString;
        String delimiter = " " + db.doubleType() + ", ADD COLUMN ";
        alterColumnsString = tableQualifier + " ADD COLUMN " + String.join(delimiter, columns) + " " + db.doubleType();
//        System.out.println(String.format(BASE_ALTER_STATEMENT2, alterColumnsString));
     try {
            executeUpdate(String.format(BASE_ALTER_STATEMENT2, alterColumnsString));
        } catch (SQLException e) {
            logger.severe(e.getMessage());//TODO replace after manual table query has been added
        }
    }

//    public static void main(String[] args) throws SQLException {
//        JDBCWriter writer = new JDBCWriter("jdbc:mysql:bla", null, "data", null, null);
//        List<String> columns = new ArrayList<>();
//        columns.add("bla");
//        columns.add("blub");
//        columns.add("yada");
//        writer.addColumns2(columns);
//    }

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
