package bitflow4j.main;

import bitflow4j.algorithms.Algorithm;
import bitflow4j.algorithms.Filter;
import bitflow4j.algorithms.FilterImpl;
import bitflow4j.algorithms.NoopAlgorithm;
import bitflow4j.io.*;
import bitflow4j.io.aggregate.*;
import bitflow4j.io.file.FileMetricPrinter;
import bitflow4j.io.file.FileMetricReader;
import bitflow4j.io.fork.AbstractFork;
import bitflow4j.io.net.TcpMetricsListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
public class BaseAlgorithmPipeline implements AlgorithmPipeline {

    private static final Logger logger = Logger.getLogger(BaseAlgorithmPipeline.class.getName());

    private static final int PIPE_BUFFER = 128;

    private final List<Filter> algorithms = new ArrayList<>();
    private final List<InputStreamProducer> producers = new ArrayList<>();
    private final List<AlgorithmPipeline> forks = new ArrayList<>();
    private MetricInputAggregator aggregator;
    private MetricOutputStream outputStream;
    private Runnable postExecuteRunnable = null;

    private File cacheFolder = null;
    private boolean printParameterHashes = false;
    private boolean started = false;

    public BaseAlgorithmPipeline(MetricInputAggregator aggregator) {
        this.aggregator = aggregator;
    }

    public BaseAlgorithmPipeline(int port, String inputMarshaller) throws IOException {
        this(port, inputMarshaller, false);
    }

    public BaseAlgorithmPipeline(int port, String inputMarshaller, boolean assembleSamples) throws IOException {
        this(assembleSamples ? new ParallelAssemblingAggregator() : new ParallelAggregator());
        producer(new TcpMetricsListener(port, AlgorithmPipeline.getMarshaller(inputMarshaller)));
    }

    public BaseAlgorithmPipeline(FileMetricReader fileInput) {
        this(new SequentialAggregator());
        producer(fileInput);
    }

    public BaseAlgorithmPipeline(File csvFile, FileMetricReader.NameConverter conv) throws IOException {
        this(AlgorithmPipeline.csvFileReader(csvFile, conv));
    }

    @SuppressWarnings("ConstantConditions")
    public static MetricPipe newPipe() {
        return PIPE_BUFFER > 0 ? new MetricPipe(PIPE_BUFFER) : new MetricPipe();
    }

    // ===============================================
    // Inputs ========================================
    // ===============================================

    @Override
    public AlgorithmPipeline input(String name, MetricInputStream input) {
        aggregator.addInput(name, input);
        return this;
    }

    @Override
    public AlgorithmPipeline producer(InputStreamProducer producer) {
        producers.add(producer);
        return this;
    }

    @Override
    public AlgorithmPipeline cache(File cacheFolder) {
        this.cacheFolder = cacheFolder;
        return this;
    }

    @Override
    public AlgorithmPipeline cache(File cacheFolder, boolean printParameterHashes) {
        this.printParameterHashes = true;
        return cache(cacheFolder);
    }

    @Override
    public String getParameterHash() {
        return getParameterHash(null);
    }

    // ===============================================
    // Algorithms & Forks ============================
    // ===============================================

    @Override
    public AlgorithmPipeline step(Filter algo) {
        algorithms.add(algo);
        return this;
    }

    @Override
    public AlgorithmPipeline step(Algorithm algo) {
        algorithms.add(new FilterImpl<>(algo));
        return this;
    }

    @Override
    public <T> AlgorithmPipeline fork(AbstractFork<T> fork, ForkHandler<T> handler) {
        fork.setOutputFactory(key -> {
            MetricPipe pipe = newPipe();
            BaseAlgorithmPipeline subPipeline = new BaseAlgorithmPipeline(new SingleInputAggregator(pipe));
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

    @Override
    public AlgorithmPipeline postExecute(Runnable runnable) {
        postExecuteRunnable = runnable;
        return this;
    }

    @Override
    public AlgorithmPipeline csvOutput(String filename) throws IOException {
        return fileOutput(filename, "CSV");
    }

    // =========================================
    // Outputs =================================
    // =========================================

    @Override
    public AlgorithmPipeline output(MetricOutputStream outputStream) {
        if (this.outputStream != null)
            throw new IllegalStateException("outputStream was already configured");
        this.outputStream = outputStream;
        return this;
    }

    @Override
    public AlgorithmPipeline consoleOutput(String outputMarshaller) {
        return output(new MetricPrinter(AlgorithmPipeline.getMarshaller(outputMarshaller)));
    }

    @Override
    public AlgorithmPipeline fileOutput(String path, String outputMarshaller) throws IOException {
        return output(new FileMetricPrinter(path, AlgorithmPipeline.getMarshaller(outputMarshaller)));
    }

    @Override
    public AlgorithmPipeline fileOutput(File file, String outputMarshaller) throws IOException {
        return fileOutput(file.toString(), outputMarshaller);
    }

    @Override
    public AlgorithmPipeline emptyOutput() {
        return output(new EmptyOutputStream());
    }

    @Override
    public void waitForOutput() {
        outputStream.waitUntilClosed();
        if (postExecuteRunnable != null)
            postExecuteRunnable.run();
        forks.forEach(AlgorithmPipeline::waitForOutput);
    }

    // =========================================
    // Running =================================
    // =========================================

    @Override
    public void runAndWait() throws IOException {
        runApp();
        waitForOutput();
    }

    @Override
    public void runApp() throws IOException {
        runApp(null);
    }

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
            algorithms.add(new FilterImpl<>(new NoopAlgorithm()));
        }
        this.doRun();
    }

    // =========================================
    // Private =================================
    // =========================================

    private void doRun() throws IOException {
        for (InputStreamProducer producer : producers)
            producer.start(aggregator);

        MetricInputStream runningInput = aggregator;
        for (int i = 0; i < algorithms.size(); i++) {
            Filter algo = algorithms.get(i);
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
        //TODO: are hashParameters used or not ? If so, where?
        for (InputStreamProducer producer : producers)
            producer.hashParameters(hash);
        aggregator.hashParameters(hash);
        for (Filter algo : algorithms)
            algo.getAlgorithm().hashParameters(hash);
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
