package bitflow4j.main;

import bitflow4j.algorithms.Algorithm;
import bitflow4j.io.MetricPrinter;
import bitflow4j.io.file.FileMetricPrinter;
import bitflow4j.io.file.FileMetricReader;
import bitflow4j.io.marshall.*;
import bitflow4j.io.net.TcpMetricsDownloader;
import bitflow4j.io.net.TcpMetricsListener;
import bitflow4j.sample.EmptySink;
import bitflow4j.sample.EmptySource;
import bitflow4j.sample.SampleSink;
import bitflow4j.sample.SampleSource;
import bitflow4j.task.Task;
import bitflow4j.task.TaskPool;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings({"unused", "WeakerAccess"})
public class AlgorithmPipeline {

    private static final Logger logger = Logger.getLogger(AlgorithmPipeline.class.getName());

    public SampleSource source;
    public final List<Algorithm> steps = new ArrayList<>();
    public SampleSink sink;

    public static Marshaller getMarshaller(String format) {
        switch (format.toUpperCase()) {
            case "CSV":
                return new CsvMarshaller();
            case "BIN":
                return new BinaryMarshaller();
            case "BINOLD":
                return new OldBinaryMarshaller();
            case "TXT":
                return new TextMarshaller();
            default:
                throw new IllegalStateException("Unknown marshaller format: " + format);
        }
    }

    // ===============================================
    // Inputs ========================================
    // ===============================================

    public AlgorithmPipeline input(SampleSource input) {
        if (this.source != null)
            throw new IllegalStateException("sink was already configured");
        this.source = input;
        return this;
    }

    public AlgorithmPipeline inputFiles(String format, FileMetricReader.NameConverter conv, String... files) throws IOException {
        FileMetricReader reader = new FileMetricReader(getMarshaller(format), conv);
        for (String file : files)
            reader.addFile(new File(file));
        return input(reader);
    }

    public AlgorithmPipeline inputFiles(String format, String... files) throws IOException {
        return inputFiles(format, FileMetricReader.FILE_NAME, files);
    }

    public AlgorithmPipeline inputBinary(String... files) throws IOException {
        return inputFiles("BIN", files);
    }

    public AlgorithmPipeline inputCsv(String... files) throws IOException {
        return inputFiles("CSV", files);
    }

    public AlgorithmPipeline inputListen(String format, int port) throws IOException {
        return input(new TcpMetricsListener(port, getMarshaller(format)));
    }

    public AlgorithmPipeline inputDownload(String format, String... sources) throws URISyntaxException {
        return input(new TcpMetricsDownloader(sources, getMarshaller(format)));
    }

    public AlgorithmPipeline emptyInput() {
        return input(new EmptySource());
    }

    // ===============================================
    // Algorithms ====================================
    // ===============================================

    public AlgorithmPipeline step(Algorithm algo) {
        steps.add(algo);
        return this;
    }

    // =========================================
    // Outputs =================================
    // =========================================

    public AlgorithmPipeline output(SampleSink outputStream) {
        if (this.sink != null)
            throw new IllegalStateException("sink was already configured");
        this.sink = outputStream;
        return this;
    }

    public AlgorithmPipeline consoleOutput(String outputMarshaller) {
        return output(new MetricPrinter(getMarshaller(outputMarshaller)));
    }

    public AlgorithmPipeline consoleOutput() {
        return consoleOutput("CSV");
    }

    public AlgorithmPipeline fileOutput(String path, String outputMarshaller) throws IOException {
        return output(new FileMetricPrinter(path, getMarshaller(outputMarshaller)));
    }

    public AlgorithmPipeline fileOutput(File file, String outputMarshaller) throws IOException {
        return fileOutput(file.toString(), outputMarshaller);
    }

    public AlgorithmPipeline csvOutput(String filename) throws IOException {
        return fileOutput(filename, "CSV");
    }

    public AlgorithmPipeline emptyOutput() {
        return output(new EmptySink());
    }

    // =========================================
    // Running =================================
    // =========================================

    public void runAndWait() throws IOException {
        runAndWait(new TaskPool());
    }

    public void runAndWait(TaskPool pool) throws IOException {
        try {
            run(pool);
            sink.waitUntilClosed();
        } finally {
            pool.stop("Algorithms finished");
            pool.waitForTasks();
        }
    }

    public void run(TaskPool pool) throws IOException {
        if (source == null) {
            source = new EmptySource();
        }
        if (sink == null) {
            sink = new EmptySink();
        }

        // Connect all pipeline steps
        List<Task> tasks = new ArrayList<>(steps.size() + 2);
        tasks.add(source);
        SampleSource currentSource = source;
        for (Algorithm algo : steps) {
            currentSource.setOutgoingSink(algo);
            currentSource = algo;
            tasks.add(algo);
        }
        currentSource.setOutgoingSink(sink);
        tasks.add(sink);

        // Initialize and start all pipeline steps
        // Start in reverse order to make sure the sinks are initialized before the sources start pushing data into them
        for (Task task : Lists.reverse(tasks)) {
            task.start(pool);
        }
    }

}
