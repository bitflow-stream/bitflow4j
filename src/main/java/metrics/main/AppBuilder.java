package metrics.main;

import metrics.BinaryMarshaller;
import metrics.CsvMarshaller;
import metrics.Marshaller;
import metrics.TextMarshaller;
import metrics.algorithms.Algorithm;
import metrics.io.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AppBuilder {

    public int pipeBuffer = 128;

    private final List<Algorithm> algorithms = new ArrayList<>();
    private final List<InputStreamProducer> producers = new ArrayList<>();
    private final MetricInputAggregator aggregator;
    MetricOutputStream output;

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

    public AppBuilder(MetricInputAggregator aggregator) {
        this.aggregator = aggregator;
    }

    public AppBuilder(int port, String inputMarshaller) throws IOException {
        this(new DecoupledMetricAggregator());
        addInputProducer(new TcpMetricsListener(port, getMarshaller(inputMarshaller)));
    }

    public AppBuilder(FileMetricReader fileInput) {
        this(new SequentialAggregator());
        addInputProducer(fileInput);
    }

    public AppBuilder(File csvFile, FileMetricReader.NameConverter conv) throws IOException {
        this(csvFileReader(csvFile, conv));
    }

    public String getName() {
        return "generic";
    }

    public static FileMetricReader csvFileReader(File csvFile, FileMetricReader.NameConverter conv) throws IOException {
        FileMetricReader reader = new FileMetricReader(getMarshaller("CSV"), conv);
        reader.addFile(csvFile);
        return reader;
    }

    public void setUnifiedSource(String source) {
        aggregator.setUnifiedSampleSource(source);
    }

    public void addAlgorithm(Algorithm algo) {
        algorithms.add(algo);
    }

    public void addInput(String name, MetricInputStream input) {
        aggregator.addInput(name, input);
    }

    public void addInputProducer(InputStreamProducer producer) {
        producers.add(producer);
    }

    public void setOutput(MetricOutputStream output) {
        this.output = output;
    }

    public void setConsoleOutput(String outputMarshaller) {
        setOutput(new MetricPrinter(getMarshaller(outputMarshaller)));
    }

    public void setFileOutput(String path, String outputMarshaller) throws FileNotFoundException {
        setOutput(new MetricPrinter(path, getMarshaller(outputMarshaller)));
    }

    public void setFileOutput(File file, String outputMarshaller) throws FileNotFoundException {
        setOutput(new MetricPrinter(file, getMarshaller(outputMarshaller)));
    }

    public void waitForOutput() {
        output.waitUntilClosed();
    }

    public void runAndWait() {
        runApp();
        waitForOutput();
    }

    public void runApp() {
        if (aggregator.size() == 0 && producers.isEmpty()) {
            throw new IllegalStateException("No inputs selected");
        }
        if (output == null) {
            throw new IllegalStateException("No output selected");
        }
        if (algorithms.size() == 0) {
            throw new IllegalStateException("No algorithms selected");
        }
        this.doRun();
    }

    private void doRun() {
        for (InputStreamProducer producer : producers)
            producer.start(aggregator);

        MetricInputStream runningInput = aggregator;
        for (int i = 0; i < algorithms.size(); i++) {
            Algorithm algo = algorithms.get(i);
            MetricInputStream input = runningInput;
            MetricOutputStream output;

            if (i < algorithms.size() - 1) {
                MetricPipe pipe = pipeBuffer > 0 ? new MetricPipe(pipeBuffer) : new MetricPipe();
                runningInput = pipe;
                output = pipe;
            } else {
                output = this.output;
            }

            algo.start(input, output);
        }
    }

}
