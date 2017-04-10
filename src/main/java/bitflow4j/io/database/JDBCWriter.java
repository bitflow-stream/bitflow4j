package bitflow4j.io.database;

import bitflow4j.sample.Sample;

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
    private static final Logger logger = Logger.getLogger(JDBCWriter.class.getName());

    private Sample lastWrittenSample;

    public JDBCWriter(DB db, String url, String schema, String table, String user, String password) {
        super(db, url, schema, table, user, password);
    }

    //TODO lock table
    public void writeSample(Sample sample) throws SQLException {
        if (lastWrittenSample == null || sample.headerChanged(lastWrittenSample.getHeader())) {
            List<String> newColumns = checkTableColumns(sample);
            if (!newColumns.isEmpty()) addColumns(newColumns);
        }

        String valuesToInsert = buildValueString(sample);
        String columnsToInsert = buildColumnStrings(sample);
        String query = String.format(BASE_INSERT_STATEMENT, tableQualifier, columnsToInsert, valuesToInsert);
//        System.out.println("query String: " + query);
        executeQuery(query);
        lastWrittenSample = sample;
        //TODO parse and handle result (e.g. any errors)
    }

    public JDBCWriter prepareInsert() throws SQLException {
        this.createTable();
        return this;
    }

    private String buildColumnStrings(Sample sample) {
        //TODO fix illegal characters for column names
//        StringBuilder resultBuilder = new StringBuilder();
        String[] currentHeader = sample.getHeader().header;
//        return (currentHeader == null || currentHeader.length == 0) ? "" :String.join(",", (CharSequence[]) (currentHeader)) + ",";
        return (currentHeader == null || currentHeader.length == 0) ? "" : db.escapeCharacter() + String.join(db.escapeCharacter() + "," + db.escapeCharacter(), (CharSequence[]) (currentHeader)) + db.escapeCharacter() + ",";
//        return (currentHeader == null || currentHeader.length == 0) ? "" : "`" + String.join("`,`", (CharSequence[]) (currentHeader)) + "`,";
//        return (currentHeader == null || currentHeader.length == 0) ? "" : "'" + String.join("','", (CharSequence[]) (currentHeader)) + "',";
//        return (currentHeader == null || currentHeader.length == 0) ? "" : "\"" + String.join("\",\"", (CharSequence[]) (currentHeader)) + "\",";
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
        if (lastIndexofSeparator >= 0) resultBuilder.delete(lastIndexofSeparator, lastIndexofSeparator + 1);
        //clean
        resultBuilder.append("\'");
        return resultBuilder.toString();
    }

    //####################################################
    //                 CREATE
    //####################################################

    private void createTable() throws SQLException {
        String query = String.format(BASE_CREATE_STATEMENT, this.tableQualifier, db.longType(), db.stringType());
//        System.out.println("sql create table query: " + query);
        //TODO use correct execute and handle result: fix later
//        ResultSet resultSet =
        executeQuery(query);
    }

    //####################################################
    //                 ALTER
    //####################################################

    private List<String> checkTableColumns(Sample sample) throws SQLException {
        ResultSet resultSet = connection.getMetaData().getColumns(null, null, this.tableQualifier, null);//this.dbTableInsert TODO: check if it works without schema and provided name
        List<String> columns = new ArrayList<>(resultSet.getFetchSize());
        List<String> sampleColumns = new ArrayList<>(Arrays.asList(sample.getHeader().header));
//        System.out.println("printing column result");
        while (resultSet.next()) {
            String currColumn = resultSet.getString("COLUMN_NAME");
//            System.out.println(currColumn);
            columns.add(currColumn);
        }
//        System.out.println("In check table columns ");
//        System.out.println("sample columns");
//        for (String s : sampleColumns) System.out.println(s);
//        System.out.println("table columns");
//        for (String s : columns) System.out.println(s);
        sampleColumns.removeAll(columns);
//        System.out.println("result columns");
//        for (String s : sampleColumns) System.out.println(s);
        return sampleColumns;
    }

    private void addColumns(List<String> columns) throws SQLException {
        buildColumnStrings(columns);
        for (String columnToAdd : columns) {
            String query = String.format(BASE_ALTER_STATEMENT, tableQualifier, columnToAdd);
//            System.out.println("add columns query: " + query);
            try {
                executeQuery(query);
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
//            String columnString = "ADD " + columns.get(i) + " " + columnType;
            String columnString = "ADD " + db.escapeCharacter() + columns.get(i) + db.escapeCharacter() + " " + columnType;
//            String columnString = "ADD `" + columns.get(i) + "` " + columnType;
//            String columnString = "ADD '" + columns.get(i) + "' " + columnType;
//            String columnString = "ADD \"" + columns.get(i) + "\" " + columnType;
//            System.out.println("buildColumns for alter, column string: " + columnString);
            columns.set(i, columnString);
//  resultBuilder.append(",");
        }
//        resultBuilder.deleteCharAt(resultBuilder.length() - 1);
//        int lastIndexofSeparator = resultBuilder.lastIndexOf(",");
//        resultBuilder.delete(lastIndexofSeparator, lastIndexofSeparator + 1);
//        return columns; //resultBuilder.toString();
    }
}
