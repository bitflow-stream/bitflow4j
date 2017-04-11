package bitflow4j.io.database;

import java.sql.*;

/**
 * Created by malcolmx on 21.03.17.
 */
public abstract class Connector<T extends Connector<T>> {
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

    public Connector(DB db, String url, String schema, String table, String user, String password) {
        this.table = table;
        this.schema = schema;
        this.db = db;
        this.user = user;
        this.password = password;
        this.url = url;
        this.tableQualifier = schema == null ? this.table : this.schema + "." + this.table;
        this.state = State.INITIALIZED;
    }

    public T connect() throws SQLException {
        if (state == State.CONNECTED) return (T) this;
        this.connection = DriverManager.getConnection(this.url, this.user, this.password);
        this.state = State.CONNECTED;
        return (T) this;
    }

    public T disconnect() throws SQLException {
        if (this.state != State.CONNECTED) return (T) this;
        if (!this.connection.isClosed()) this.connection.close();
        this.state = State.INITIALIZED;
        return (T) this;
    }

    public T reconnect() throws SQLException {
        if (state == State.CONNECTED && !this.connection.isValid(0)) {
            state = State.INITIALIZED;
            try {
                this.connection.close();
            } catch (SQLException e) {
            }
            this.connect();
        }
    }

    protected ResultSet executeQuery(String sqlQuery) throws SQLException {
        Statement sqlStatement = connection.createStatement();
        return sqlStatement.executeQuery(sqlQuery);
    }

    protected int executeUpdate(String sqlQuery) throws SQLException {
        Statement sqlStatement = connection.createStatement();
        return sqlStatement.executeUpdate(sqlQuery);
    }

    protected enum State {
        INITIALIZED, CONNECTED
    }

}
