package bitflow4j.sample;

import bitflow4j.task.TaskPool;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * JDBC Sample Source
 */
public class JDBCSampleSource extends AbstractSampleSource {

    private static final Logger logger = Logger.getLogger(JDBCSampleSource.class.getName());
    private static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String POSTGRES_DRIVER = "org.postgresql.Driver";
    private static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private String dbDriver;
    private String dbName;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public JDBCSampleSource setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
        return this;
    }

    public JDBCSampleSource setDB(DB db) {
        switch (db) {
            case MYSQL:
                this.dbDriver = MYSQL_DRIVER;
                break;
            case POSTGRES:
                this.dbDriver = POSTGRES_DRIVER;
                break;
            case ORACLE:
                this.dbDriver = ORACLE_DRIVER;
                break;
            default:

        }
        return this;
    }

    public JDBCSampleSource setDbName(String dbName) {
        this.dbName = dbName;
        return this;
    }

    public JDBCSampleSource setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
        return this;
    }

    public JDBCSampleSource setDbUser(String dbUser) {
        this.dbUser = dbUser;
        return this;
    }

    public JDBCSampleSource setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
        return this;
    }

    @Override
    public void start(TaskPool pool) throws IOException {

    }

    enum DB {
        MYSQL, POSTGRES, ORACLE;
    }

}
