package bitflow4j.main;

import bitflow4j.BinaryMarshaller;
import bitflow4j.CsvMarshaller;
import bitflow4j.Marshaller;
import bitflow4j.TextMarshaller;
import bitflow4j.algorithms.Algorithm;
import bitflow4j.io.*;
import bitflow4j.io.file.FileMetricPrinter;
import bitflow4j.io.file.FileMetricReader;
import bitflow4j.io.fork.AbstractFork;
import bitflow4j.io.net.TcpMetricsListener;

import java.io.File;
import java.io.IOException;

/**
 * Created by Malcolm-X on 14.12.2016.
 */
public interface AlgorithmPipeline {

    // ============== Input ==============

    AlgorithmPipeline input(MetricInputStream input);

    default AlgorithmPipeline inputFiles(String format, FileMetricReader.NameConverter conv, String... files) throws IOException {
        FileMetricReader reader = new FileMetricReader(AlgorithmPipeline.getMarshaller(format), conv);
        for (String file : files)
            reader.addFile(new File(file));
        return input(reader);
    }

    default AlgorithmPipeline inputFiles(String format, String... files) throws IOException {
        return inputFiles(format, FileMetricReader.FILE_NAME, files);
    }

    default AlgorithmPipeline inputBinary(String... files) throws IOException {
        return inputFiles("BIN", files);
    }

    default AlgorithmPipeline inputCsv(String... files) throws IOException {
        return inputFiles("CSV", files);
    }

    default AlgorithmPipeline inputListen(int port, String format) throws IOException {
        return input(new TcpMetricsListener(port, AlgorithmPipeline.getMarshaller(format)));
    }

    // ============== Steps ==============

    AlgorithmPipeline step(Algorithm algo);

    <T> AlgorithmPipeline fork(AbstractFork<T> fork, ForkHandler<T> handler);

    interface ForkHandler<T> {
        void buildForkedPipeline(T key, AlgorithmPipeline subPipeline) throws IOException;
    }

    // ============== Output ==============

    AlgorithmPipeline output(MetricOutputStream outputStream);

    default AlgorithmPipeline consoleOutput(String outputMarshaller) {
        return output(new MetricPrinter(AlgorithmPipeline.getMarshaller(outputMarshaller)));
    }

    default AlgorithmPipeline consoleOutput() {
        return consoleOutput("CSV");
    }

    default AlgorithmPipeline fileOutput(String path, String outputMarshaller) throws IOException {
        return output(new FileMetricPrinter(path, AlgorithmPipeline.getMarshaller(outputMarshaller)));
    }

    default AlgorithmPipeline fileOutput(File file, String outputMarshaller) throws IOException {
        return fileOutput(file.toString(), outputMarshaller);
    }

    default AlgorithmPipeline csvOutput(String filename) throws IOException {
        return fileOutput(filename, "CSV");
    }

    default AlgorithmPipeline emptyOutput() {
        return output(new EmptyOutputStream());
    }

    // ============== Execute ==============

    void waitForOutput();

    void runAndWait() throws IOException;

    void runApp() throws IOException;

    // ============== Helpers ==============

    static Marshaller getMarshaller(String format) {
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

    int PIPE_BUFFER = 128;

    @SuppressWarnings("ConstantConditions")
    static MetricPipe newPipe() {
        return PIPE_BUFFER > 0 ? new MetricPipe(PIPE_BUFFER) : new MetricPipe();
    }

}
