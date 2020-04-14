package bitflow4j.misc;

import bitflow4j.LoggingConfig;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by anton on 6/26/16.
 */
public class FeatureStatistics implements Serializable {

    private static final Logger logger = Logger.getLogger(FeatureStatistics.class.getName());

    public static class Feature implements Serializable {
        private final String name;
        private double min;
        private double max;
        private final double avg;
        private final double stddev;
        private final int count;

        public Feature() {
            // Empty constructor required for de-serialization
            this("", 0, 0, 0, 0, 0);
        }

        public Feature(String name, double min, double max, double avg, double stddev, int count) {
            this.name = name;
            this.min = min;
            this.max = max;
            this.avg = avg;
            this.stddev = stddev;
            this.count = count;
        }

        public Feature(Feature copyFrom) {
            this(copyFrom.name, copyFrom.min, copyFrom.max, copyFrom.avg, copyFrom.stddev, copyFrom.count);
        }

        public Feature(String name, Map<String, String> values) throws NumberFormatException {
            this.name = name;
            this.min = parseDouble(values, "min");
            this.max = parseDouble(values, "max");
            this.avg = parseDouble(values, "avg");
            this.stddev = parseDouble(values, "stddev");
            this.count = parseInt(values, "count");
        }

        private double parseDouble(Map<String, String> values, String key) throws NumberFormatException {
            String val = values.get(key);
            if (val == null || val.isEmpty()) {
                throw new NumberFormatException("Empty or missing key '" + key + "' for feature " + name +
                        ". Available keys: " + values);
            }
            try {
                return Double.parseDouble(val);
            } catch (NumberFormatException exc) {
                throw new NumberFormatException("Failed to parse key '" + key + "' for feature " + name + ": " + exc.toString());
            }
        }

        private int parseInt(Map<String, String> values, String key) throws NumberFormatException {
            String val = values.get(key);
            if (val == null || val.isEmpty()) {
                throw new NumberFormatException("Empty or missing key '" + key + "' for feature " + name +
                        ". Available keys: " + values);
            }
            try {
                return Integer.parseInt(val);
            } catch (NumberFormatException exc) {
                throw new NumberFormatException("Failed to parse key '" + key + "' for feature " + name + ": " + exc.toString());
            }
        }

        public void fillMap(Map<String, String> target) {
            target.put("min", String.valueOf(min));
            target.put("max", String.valueOf(max));
            target.put("avg", String.valueOf(avg));
            target.put("stddev", String.valueOf(stddev));
            target.put("count", String.valueOf(count));
        }

        public String toString() {
            return name + " (min: " + min + ", max: " + max + ", avg: " + avg +
                    ", stddev: " + stddev + ", count: " + count + ")";
        }

        public void setMin(double min) {
            this.min = min;
        }

        public void setMax(double max) {
            this.max = max;
        }

        public String getName() {
            return name;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }

        public double getAvg() {
            return avg;
        }

        public double getStddev() {
            return stddev;
        }

        public int getCount() {
            return count;
        }
    }

    private final Map<String, Feature> features = new HashMap<>();

    /**
     * Create an empty instance without loading from file.
     */
    public FeatureStatistics() {
    }

    public FeatureStatistics(FeatureStatistics copyFrom) {
        for (Feature feature : copyFrom.allFeatures()) {
            features.put(feature.name, new Feature(feature));
        }
    }

    public FeatureStatistics(String iniFile) {
        loadIniFile(iniFile);
    }

    public Feature getFeature(String name) {
        Feature ft = features.get(name);
        if (ft == null) {
            ft = new Feature(name, 0, 0, 0, 0, 0);
            features.put(name, ft);
        }
        return ft;
    }

    public Collection<String> allFeatureNames() {
        return features.keySet();
    }

    public Collection<Feature> allFeatures() {
        return features.values();
    }

    public void writeFile(String iniFile) throws IOException {
        Ini ini = new Ini();
        ini.getConfig().setTree(false);
        File file = new File(iniFile);
        if (file.canRead()) {
            ini.load(file);
        }
        fillIni(ini);
        ini.store(file);
    }

    public Ini loadIniFile(String iniFile) {
        Ini ini = new Ini();
        ini.getConfig().setTree(false);
        try {
            ini.load(new File(iniFile));
        } catch (IOException io) {
            logger.warning("Error loading features.ini file: " + io);
            return null;
        }
        logger.info("Loading feature stats from " + iniFile);
        fillFromIni(ini);
        return ini;
    }

    public Ini createIniFile(String iniFile) {
        Ini ini = new Ini();
        ini.getConfig().setTree(false);
        try {
            ini.load(new File(iniFile));
        } catch (IOException io) {
            logger.warning("Error loading features.ini file: " + io);
            return null;
        }
        logger.info("Loading feature stats from " + iniFile);
        fillFromIni(ini);
        return ini;
    }

    private void fillFromIni(Ini ini) {
        features.clear();
        for (Map.Entry<String, Profile.Section> entry : ini.entrySet()) {
            String featureName = entry.getKey();
            Feature feature = new Feature(featureName, entry.getValue());
            features.put(featureName, feature);
        }
    }

    private void fillIni(Ini ini) {
        for (Feature ft : features.values()) {
            Profile.Section sec = ini.get(ft.name);
            if (sec == null) {
                sec = ini.add(ft.name);
            }
            ft.fillMap(sec);
        }
    }

    public static void main(String[] args) throws IOException {
        LoggingConfig.initializeLogger();
        if (args.length != 1) {
            logger.severe("Parameters: <ini-file>");
            return;
        }
        String file = args[0];
        FeatureStatistics stats = new FeatureStatistics(file);
        logger.info("Number of features: " + stats.allFeatures().size());
        for (Feature feature : stats.allFeatures()) {
            logger.info(feature.toString());
        }
    }

}
