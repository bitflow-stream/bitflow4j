package bitflow4j.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.LogManager;

/**
 * Created by anton on 4/14/16.
 * <p>
 * Static configuration loaded from a resource file.
 */
public class Config {

    static final String logging_properties = "logging.properties";

    private String experimentFolder;
    private String outputFolder;
    private String dotPath;
    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    private Config() {
        this.loadConfig("config");
    }

    private void loadConfig(String name) throws MissingResourceException {
        ResourceBundle resources = ResourceBundle.getBundle(name);
        experimentFolder = resources.getString("experiment_dir");
        outputFolder = resources.getString("output_dir");
        dotPath = resources.getString("dot_path");
    }

    public String getExperimentSubFolder(String subFolder) {
        return getExperimentFolder() + "/" + subFolder;
    }

    private String getExperimentFolder() {
        return experimentFolder;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public String getDotPath() {
        return dotPath;
    }

    public static void initializeLogger() {
        try {
            InputStream config = ClassLoader.getSystemResourceAsStream(logging_properties);
            LogManager.getLogManager().readConfiguration(config);
            config.close();
        } catch (IOException e) {
            System.err.println("Failed to initialize logger from resource " + logging_properties);
            e.printStackTrace();
            System.exit(1);
        }
    }

}
