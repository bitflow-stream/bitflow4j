package metrics.main;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by anton on 4/14/16.
 * <p>
 * Static configuration loaded from a resource file.
 */
public class Config {

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

    public String getExperimentSubfolder(String subfolder) {
        return getExperimentFolder() + "/" + subfolder;
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

}
