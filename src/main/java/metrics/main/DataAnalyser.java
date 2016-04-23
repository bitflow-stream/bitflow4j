package metrics.main;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import metrics.io.file.FileGroup;
import metrics.io.file.FileMetricReader;
import org.apache.commons.codec.binary.Base32;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by anton on 4/17/16.
 */
public abstract class DataAnalyser {

    private final Config config;
    protected final ExperimentData data;

    public DataAnalyser(Config config, ExperimentData data) throws IOException {
        this.config = config;
        this.data = data;
    }

    public abstract String toString();

    private File makeOutputDir(ExperimentData.Host host) throws IOException {
        String filename = config.outputFolder + "/" + toString();
        filename += "-" + data.toString();
        filename += "/" + host.name;
        File result = new File(filename);
        if (result.exists() && !result.isDirectory())
            throw new IOException("Not a directory: " + filename);
        if (!result.exists() && !result.mkdirs())
            throw new IOException("Failed to create output directory " + filename);
        return result;
    }

    protected abstract class AnalysisStep {

        private static final String CONSOLE_FORMAT = "CSV";
        private static final String FILE_FORMAT = "CSV";

        private final AnalysisStep inputStep;
        private final String baseOutputFilename;

        protected AnalysisStep(String outputFilename, AnalysisStep inputStep) {
            this.inputStep = inputStep;
            this.baseOutputFilename = outputFilename;
        }

        public abstract String toString();

        protected abstract void addAlgorithms(AppBuilder builder);

        protected void hashParameters(ByteArrayDataOutput bytes) {
            // Must be overridden if analysis depends on parameters
        }

        public void execute() throws IOException {
            List<ExperimentData.Host> hosts = data.getAllHosts();
            message("Running " + toString() + " for " + hosts.size() + " hosts");
            for (ExperimentData.Host host : hosts) {
                execute(host);
            }
        }

        public void execute(ExperimentData.Host host) throws IOException {
            execute(host, makeOutputDir(host));
        }

        public void execute(ExperimentData.Host host, File outputDir) throws IOException {
            if (inputStep != null) {
                inputStep.execute(host, outputDir);
            }
            File output = getOutputFile(outputDir);
            if (output.exists()) {
                message("Output for " + toString() + " for " + host + " already exists: " + output.toString());
            } else {
                doExecute(host, outputDir);
            }
        }

        public void reexecute() throws IOException {
            List<ExperimentData.Host> hosts = data.getAllHosts();
            message("Re-running " + toString() + " for " + hosts.size() + " hosts");
            for (ExperimentData.Host host : hosts) {
                reexecute(host);
            }
        }

        public void reexecute(ExperimentData.Host host) throws IOException {
            reexecute(host, makeOutputDir(host));
        }

        public void reexecute(ExperimentData.Host host, File outputDir) throws IOException {
            if (inputStep != null) {
                inputStep.reexecute(host, outputDir);
            }
            doExecute(host, outputDir);
        }

        public void executeInMemory() throws IOException {
            List<ExperimentData.Host> hosts = data.getAllHosts();
            message("Running " + toString() + " in-memory for " + hosts.size() + " hosts");
            for (ExperimentData.Host host : hosts) {
                executeInMemory(host);
            }
        }

        public void executeInMemory(ExperimentData.Host host) throws IOException {
            executeInMemory(host, makeOutputDir(host));
        }

        public void executeInMemory(ExperimentData.Host host, File outputDir) throws IOException {
            if (inputStep != null) {
                inputStep.execute(host, outputDir);
            }
            AppBuilder builder = makeBuilder(host, outputDir);
            addAlgorithms(builder);
            setInMemoryOutput(builder);
            message("Executing " + toString() + " in-memory for " + host + "...");
            builder.runAndWait();
            postExecute(null);
        }

        public void executeInMemoryUncached(ExperimentData.Host host) throws IOException {
            AppBuilder builder = data.makeBuilder(host);
            addAllAlgorithms(builder);
            setInMemoryOutput(builder);
            message("Executing " + toString() + " uncached in-memory for " + host + "...");
            builder.runAndWait();
            postExecute(null);
        }

         void doExecute(ExperimentData.Host host, File outputDir) throws IOException {
            AppBuilder builder = makeBuilder(host, outputDir);
            addAlgorithms(builder);
            File output = getOutputFile(outputDir);
            setFileOutput(builder, output);
            message("Writing " + toString() + " for " + host + " to " + output.toString());
            builder.runAndWait();
            postExecute(output);
        }

        protected void postExecute(File outputFile) throws IOException {
            // Hook for subclasses
        }

        protected void setInMemoryOutput(AppBuilder builder) {
            builder.setConsoleOutput(CONSOLE_FORMAT);
        }

        protected void setFileOutput(AppBuilder builder, File output) throws IOException {
            builder.setFileOutput(output, FILE_FORMAT);
        }

        private void addAllAlgorithms(AppBuilder builder) {
            if (inputStep != null) {
                inputStep.addAllAlgorithms(builder);
            }
            addAlgorithms(builder);
        }

        protected AppBuilder makeBuilder(ExperimentData.Host host, File outputDir) throws IOException {
            if (inputStep == null) {
                return data.makeBuilder(host);
            } else {
                return new AppBuilder(inputStep.getOutputFile(outputDir), FileMetricReader.FILE_NAME);
            }
        }

        protected File getOutputFile(File outputDir) {
            String hash = getParameterHash();
            String filename = new FileGroup(baseOutputFilename).getFile(hash);
            return new File(outputDir, filename);
        }

        protected void message(String msg) {
            System.err.println("===================== " + msg);
        }

        private void recursiveParameterDigest(ByteArrayDataOutput bytes) {
            bytes.writeChars(toString());
            hashParameters(bytes);
            if (inputStep != null)
                inputStep.recursiveParameterDigest(bytes);
        }

        private String getParameterHash() {
            ByteArrayDataOutput bytes = ByteStreams.newDataOutput();
            recursiveParameterDigest(bytes);
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] rawHash = digest.digest(bytes.toByteArray());
                byte[] name = new Base32().encode(rawHash);
                return new String(name).replace("=", "");
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("Failed to get MD5 hash instance");
            }
        }

    }

}
