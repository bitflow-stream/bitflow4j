package bitflow4j.main;

import bitflow4j.algorithms.PipelineStep;
import bitflow4j.io.SampleWriter;
import bitflow4j.io.file.FileSink;
import bitflow4j.io.file.FileSource;
import bitflow4j.io.marshall.*;
import bitflow4j.io.net.TcpListenerSource;
import bitflow4j.io.net.TcpSource;
import bitflow4j.sample.EmptySink;
import bitflow4j.sample.EmptySource;
import bitflow4j.sample.Sink;
import bitflow4j.sample.Source;
import bitflow4j.task.Task;
import bitflow4j.task.TaskPool;
import bitflow4j.task.UserSignalTask;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Pipeline {

    protected static final Logger logger = Logger.getLogger(Pipeline.class.getName());

    public Source source;
    public final List<PipelineStep> steps = new ArrayList<>();
    public Sink sink;

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

    public Pipeline input(Source input) {
        if (this.source != null)
            throw new IllegalStateException("sink was already configured");
        this.source = input;
        return this;
    }

    public Pipeline inputFiles(String format, FileSource.NameConverter converter, String... files) throws IOException {
        FileSource reader = new FileSource(getMarshaller(format), converter);
        for (String file : files)
            reader.addFile(new File(file));
        return input(reader);
    }

    public Pipeline inputFiles(String format, String... files) throws IOException {
        return inputFiles(format, FileSource.FILE_NAME, files);
    }

    public Pipeline inputBinary(String... files) throws IOException {
        return inputFiles("BIN", files);
    }

    public Pipeline inputCsv(String... files) throws IOException {
        return inputFiles("CSV", files);
    }

    public Pipeline inputListen(String format, int port) {
        return input(new TcpListenerSource(port, getMarshaller(format)));
    }

    public Pipeline inputDownload(String format, String... sources) {
        return input(new TcpSource(sources, getMarshaller(format)));
    }

    public Pipeline emptyInput() {
        return input(new EmptySource());
    }

    // ===============================================
    // Algorithms ====================================
    // ===============================================

    public Pipeline step(PipelineStep algo) {
        steps.add(algo);
        return this;
    }

    // =========================================
    // Outputs =================================
    // =========================================

    public Pipeline output(Sink outputStream) {
        if (this.sink != null)
            throw new IllegalStateException("sink was already configured");
        this.sink = outputStream;
        return this;
    }

    public Pipeline consoleOutput(String outputMarshaller) {
        return output(new SampleWriter(getMarshaller(outputMarshaller)));
    }

    public Pipeline consoleOutput() {
        return consoleOutput("CSV");
    }

    public Pipeline fileOutput(String path, String outputMarshaller) throws IOException {
        return output(new FileSink(path, getMarshaller(outputMarshaller)));
    }

    public Pipeline fileOutput(File file, String outputMarshaller) throws IOException {
        return fileOutput(file.toString(), outputMarshaller);
    }

    public Pipeline csvOutput(String filename) throws IOException {
        return fileOutput(filename, "CSV");
    }

    public Pipeline emptyOutput() {
        return output(new EmptySink());
    }

    // =========================================
    // Running =================================
    // =========================================

    public void runAndWait(boolean handleSignals) {
        TaskPool pool = new TaskPool();
        if (handleSignals)
            try {
                pool.start(new UserSignalTask());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to listen for User Signals", e);
                return;
            }
        runAndWait(pool);
    }

    public void runAndWait() {
        runAndWait(new TaskPool());
    }

    public void runAndWait(TaskPool pool) {
        try {
            run(pool);
            sink.waitUntilClosed();
            pool.stop("Algorithms finished");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error starting pipeline", e);
            pool.stop("Error starting pipeline");
        }
        pool.waitForTasks();
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
        Source currentSource = source;
        for (PipelineStep algo : steps) {
            currentSource.setOutgoingSink(algo);
            currentSource = algo;
            tasks.add(algo);
        }
        currentSource.setOutgoingSink(sink);
        tasks.add(sink);

        // Initialize and start all pipeline steps
        // Start in reverse order to make sure the sinks are initialized before the sources start pushing data into them
        for (Task task : Lists.reverse(tasks)) {
            pool.start(task);
        }
    }

}
