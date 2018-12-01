package bitflow4j.misc;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by anton on 4/14/16.
 * <p>
 * Static configuration loaded from a resource file.
 */
public class Config {

    static final String logging_properties = "logging.properties";

    public static void setDefaultLogLevel(Level level) {
        Logger root = Logger.getLogger("");
        root.setLevel(level);
        for (Handler h : root.getHandlers()) {
            h.setLevel(level);
        }
    }

    public static void initializeLogger() {
        try {
            InputStream config = ClassLoader.getSystemResourceAsStream(logging_properties);
            if (config != null) {
                LogManager.getLogManager().readConfiguration(config);
                config.close();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.err.println("Failed to initialize logger from resource " + logging_properties);
    }

}
