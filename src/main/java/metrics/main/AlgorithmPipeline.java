package metrics.main;

import metrics.BinaryMarshaller;
import metrics.CsvMarshaller;
import metrics.Marshaller;
import metrics.TextMarshaller;
import metrics.algorithms.Algorithm;
import metrics.algorithms.NoopAlgorithm;
import metrics.io.*;
import metrics.io.aggregate.*;
import metrics.io.file.FileMetricPrinter;
import metrics.io.file.FileMetricReader;
import metrics.io.fork.AbstractFork;
import metrics.io.net.TcpMetricsListener;
import metrics.main.data.DataSource;
import metrics.main.misc.ParameterHash;
import metrics.main.misc.ParameterHashImpl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
public class AlgorithmPipeline {

    private static final Logger logger = Logger.getLogger(AlgorithmPipeline.class.getName());

    private static final int PIPE_BUFFER = 128;

    private final List<Algorithm> algorithms = new ArrayList<>();
    private final List<InputStreamProducer> producers = new ArrayList<>();
    private final List<AlgorithmPipeline> forks = new ArrayList<>();
    private MetricInputAggregator aggregator;
    private MetricOutputStream outputStream;
    private Runnable postExecuteRunnable = null;

    private File cacheFolder = null;
    private boolean printParameterHashes = false;
    private boolean started = false;

    public static Marshaller getMarshaller(String format) {
        switch (format) {
            case "CSV":
                return new CsvMarshaller();
            case "BIN":
                return new BinaryMarshaller();
            case "TXT":
                return new TextMarshaller();
            default:
                throw new IllegalStateException("Unknown marshaller format: " + format);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static MetricPipe newPipe() {
        return PIPE_BUFFER > 0 ? new MetricPipe(PIPE_BUFFER) : new MetricPipe();
    }

    public static FileMetricReader csvFileReader(File csvFile, FileMetricReader.NameConverter conv) throws IOException {
        FileMetricReader reader = new FileMetricReader(getMarshaller("CSV"), conv);
        reader.addFile(csvFile);
        return reader;
    }

    public static FileMetricReader binaryFileReader(File binFile, FileMetricReader.NameConverter conv) throws IOException {
        FileMetricReader reader = new FileMetricReader(getMarshaller("BIN"), conv);
        reader.addFile(binFile);
        return reader;
    }

    public static FileMetricReader fileReader(String file, String format, FileMetricReader.NameConverter conv) throws IOException {
        FileMetricReader reader = new FileMetricReader(getMarshaller(format), conv);
        reader.addFile(new File(file));
        return reader;
    }

    // =========================================
    // Constructors ============================
    // =========================================

    public <T> AlgorithmPipeline(DataSource<T> data, T source) throws IOException {
        this(data.preferredAggregator());
        producer(data.createProducer(source));
    }

    public AlgorithmPipeline(MetricInputAggregator aggregator) {
        this.aggregator = aggregator;
    }

    public AlgorithmPipeline(int port, String inputMarshaller) throws IOException {
        this(port, inputMarshaller, false);
    }

    public AlgorithmPipeline(int port, String inputMarshaller, boolean assembleSamples) throws IOException {
        this(assembleSamples ? new ParallelAssemblingAggregator() : new ParallelAggregator());
        producer(new TcpMetricsListener(port, getMarshaller(inputMarshaller)));
    }

    public AlgorithmPipeline(FileMetricReader fileInput) {
        this(new SequentialAggregator());
        producer(fileInput);
    }

    public AlgorithmPipeline(File csvFile, FileMetricReader.NameConverter conv) throws IOException {
        this(csvFileReader(csvFile, conv));
    }

    // ===============================================
    // Inputs ========================================
    // ===============================================

    public AlgorithmPipeline input(String name, MetricInputStream input) {
        aggregator.addInput(name, input);
        return this;
    }

    public AlgorithmPipeline producer(InputStreamProducer producer) {
        producers.add(producer);
        return this;
    }

    public AlgorithmPipeline cache(File cacheFolder) {
        this.cacheFolder = cacheFolder;
        return this;
    }

    public AlgorithmPipeline cache(File cacheFolder, boolean printParameterHashes) {
        this.printParameterHashes = true;
        return cache(cacheFolder);
    }

    public String getParameterHash() {
        return getParameterHash(null);
    }

    // ===============================================
    // Algorithms & Forks ============================
    // ===============================================

    public AlgorithmPipeline step(Algorithm algo) {
        algorithms.add(algo);
        return this;
    }

    public interface ForkHandler<T> {
        void buildForkedPipeline(T key, AlgorithmPipeline subPipeline) throws IOException;
    }

    public <T> AlgorithmPipeline fork(AbstractFork<T> fork, ForkHandler<T> handler) {
        fork.setOutputFactory(key -> {
            MetricPipe pipe = newPipe();
            AlgorithmPipeline subPipeline = new AlgorithmPipeline(new SingleInputAggregator(pipe));
            handler.buildForkedPipeline(key, subPipeline);
            forks.add(subPipeline);
            byte inputHash[] = getForkHashBytes(fork, key);
            if (subPipeline.algorithms.isEmpty() && subPipeline.outputStream != null) {
                // Optimization: If there are no algorithms, skip the entire output and connect the output directly
                subPipeline.applyCache(inputHash);
                subPipeline.started = true;
                return subPipeline.outputStream;
            } else {
                subPipeline.runApp(inputHash);
                return pipe;
            }
        });
        return output(fork);
    }

    public AlgorithmPipeline postExecute(Runnable runnable) {
        postExecuteRunnable = runnable;
        return this;
    }

    // =========================================
    // Outputs =================================
    // =========================================

    public AlgorithmPipeline csvOutput(String filename) throws IOException {
        return fileOutput(filename, "CSV");
    }

    public AlgorithmPipeline output(MetricOutputStream outputStream) {
        if (this.outputStream != null)
            throw new IllegalStateException("outputStream was already configured");
        this.outputStream = outputStream;
        return this;
    }

    public AlgorithmPipeline consoleOutput(String outputMarshaller) {
        return output(new MetricPrinter(getMarshaller(outputMarshaller)));
    }

    public AlgorithmPipeline fileOutput(String path, String outputMarshaller) throws IOException {
        return output(new FileMetricPrinter(path, getMarshaller(outputMarshaller)));
    }

    public AlgorithmPipeline fileOutput(File file, String outputMarshaller) throws IOException {
        return fileOutput(file.toString(), outputMarshaller);
    }

    public AlgorithmPipeline emptyOutput() {
        return output(new EmptyOutputStream());
    }

    // =========================================
    // Running =================================
    // =========================================

    public void waitForOutput() {
        outputStream.waitUntilClosed();
        if (postExecuteRunnable != null)
            postExecuteRunnable.run();
        forks.forEach(AlgorithmPipeline::waitForOutput);
    }

    public void runAndWait() throws IOException {
        runApp();
        waitForOutput();
    }

    public void runApp() throws IOException {
        runApp(null);
    }

    // =========================================
    // Private =================================
    // =========================================

    private synchronized void runApp(byte inputHash[]) throws IOException {
        if (started) {
            return;
        }
        started = true;
        applyCache(inputHash);
        if (aggregator.size() == 0 && producers.isEmpty()) {
            throw new IllegalStateException("No inputs selected");
        }
        if (outputStream == null) {
            outputStream = new EmptyOutputStream();
        }
        if (algorithms.size() == 0) {
            algorithms.add(new NoopAlgorithm());
        }
        this.doRun();
    }

    private void doRun() throws IOException {
        for (InputStreamProducer producer : producers)
            producer.start(aggregator);

        MetricInputStream runningInput = aggregator;
        for (int i = 0; i < algorithms.size(); i++) {
            Algorithm algo = algorithms.get(i);
            MetricInputStream input = runningInput;
            MetricOutputStream output;

            if (i < algorithms.size() - 1) {
                MetricPipe pipe = newPipe();
                runningInput = pipe;
                output = pipe;
            } else {
                output = this.outputStream;
            }

            algo.start(input, output);
        }
    }

    private void applyCache(byte inputHash[]) throws IOException {
        if (cacheFolder == null)
            return;
        if (!cacheFolder.isDirectory() && !cacheFolder.mkdirs())
            throw new IOException("Failed to create cache folder " + cacheFolder);

        String filename = getParameterHash(inputHash) + ".csv";
        File cacheFile = new File(cacheFolder, filename);
        CachingMetricOutputStream cacheStream = new CachingMetricOutputStream(cacheFile, outputStream);
        outputStream = cacheStream;

        if (cacheStream.isCacheHit()) {
            logger.info("Reading from cache file: " + cacheFile);
            aggregator = new SequentialAggregator();
            producers.clear();
            algorithms.clear();
            producers.add(cacheStream.getCacheReader());
        } else {
            logger.info("Writing cache file: " + cacheFile);
        }
    }

    private void hashParameters(ParameterHash hash) {
        for (InputStreamProducer producer : producers)
            producer.hashParameters(hash);
        aggregator.hashParameters(hash);
        for (Algorithm algo : algorithms)
            algo.hashParameters(hash);
    }

    private String getParameterHash(byte inputHash[]) {
        ParameterHash hash = new ParameterHashImpl(printParameterHashes);
        if (inputHash != null)
            hash.write(inputHash);
        hashParameters(hash);
        return hash.toFilename();
    }

    private byte[] getForkHashBytes(AbstractFork<?> fork, Object forkKey) {
        ParameterHash hash = new ParameterHashImpl(printParameterHashes);
        hashParameters(hash);
        fork.hashParameters(hash);
        hash.writeChars(forkKey.toString());
        return hash.toByteArray();
    }

}
