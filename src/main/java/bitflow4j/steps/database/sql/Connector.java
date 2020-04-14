package bitflow4j.steps.database.sql;

import java.sql.*;

/**
 * Created by malcolmx on 21.03.17.
 */
public abstract class Connector {

    protected static final String TIMESTAMP_COL = "timestamp";
    protected static final String TAG_COL = "tags";

    protected final String table;
    protected final String schema;
    protected final DB db;
    protected final String tableQualifier;
    private final String user;
    private final String password;
    private final String url;
    protected State state;
    protected Connection connection;

    public Connector(String url, String schema, String table, String user, String password) {
        this.table = table;
        this.schema = schema;
        if (url.startsWith("jdbc:mysql")) {
//            url = url + "?dontTrackOpenResources=true";
            this.db = DB.MYSQL;
        } else if (url.startsWith("jdbc:postgresql")) this.db = DB.POSTGRES;
        else if (url.startsWith("jdbc:sqlite")) this.db = DB.SQLite;
        else
            throw new IllegalArgumentException("Database not supported. Only MYSQL, POSTGRESQL and SQLITE are supported.");
        this.user = user;
        this.password = password;
        this.url = url;
        this.tableQualifier = schema == null ? this.table : this.schema + "." + this.table;
        this.state = State.INITIALIZED;
    }

    @Override
    public String toString() {
        return String.format("%s", url);
    }

    public Connector connect() throws SQLException {
        if (state == State.CONNECTED) return this;
        if (this.user == null) this.connection = DriverManager.getConnection(this.url);
        else if (this.password == null) this.connection = DriverManager.getConnection(this.url, this.user, "");
        else this.connection = DriverManager.getConnection(this.url, this.user, this.password);
        this.state = State.CONNECTED;
        return this;
    }

    public Connector disconnect() throws SQLException {
        if (this.state != State.CONNECTED) return this;
        if (!this.connection.isClosed()) this.connection.close();
        this.state = State.INITIALIZED;
        return this;
    }

    public Connector reconnect() throws SQLException {
        if (state == State.CONNECTED && !this.connection.isValid(0)) {
            state = State.INITIALIZED;
            this.connection.close();
            this.connect();
        }
        return this;
    }

    protected ResultSet executeQuery(String sqlQuery) throws SQLException {
        Statement sqlStatement = connection.createStatement();
        return sqlStatement.executeQuery(sqlQuery);
    }

    protected int executeUpdate(String sqlQuery) throws SQLException {
        Statement sqlStatement = connection.createStatement();
        int result = sqlStatement.executeUpdate(sqlQuery);
        sqlStatement.close();
        return result;
    }

    protected enum State {
        INITIALIZED, CONNECTED
    }

}
