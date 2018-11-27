package bitflow4j.io.net;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpErrorLogger {

    private static final Logger logger = Logger.getLogger(TcpErrorLogger.class.getName());

    public static Level connectionErrorLevel = Level.WARNING;

    public static void setLevel(Level level) {
        connectionErrorLevel = level;
    }

    public static void log(String message, Exception exc) {
        logger.log(connectionErrorLevel, message, exc);
    }

}
