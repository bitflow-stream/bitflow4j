package bitflow4j.io.database;

/**
 * Created by malcolmx on 21.03.17.
 */
public enum DB {
    MYSQL("DOUBLE", "BIGINT", "TEXT", '`'),
    POSTGRES("double precision", "bigint", "text", '"'),
    H2("DOUBLE", "BIGINT", "VARCHAR", '"'), SQLite("REAL", "INTEGER", "TEXT", '"');
    private String doubleTypeString;


    private String stringTypeString;
    private String longTypeString;
    private char escapeCharacter;

    DB(String doubleTypeString, String longTypeString, String stringTypeString, char escapeCharacter) {
        this.doubleTypeString = doubleTypeString;
        this.longTypeString = longTypeString;
        this.stringTypeString = stringTypeString;
        this.escapeCharacter = escapeCharacter;
    }


    public String doubleType() {
        return this.doubleTypeString;
    }

    public String longType() {
        return this.longTypeString;
    }

    public String stringType() {
        return this.stringTypeString;
    }

    public char escapeCharacter() {
        return this.escapeCharacter;
    }
}