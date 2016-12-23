package bitflow4j.main;

import bitflow4j.BinaryMarshaller;
import bitflow4j.CsvMarshaller;
import bitflow4j.Marshaller;
import bitflow4j.TextMarshaller;
import bitflow4j.algorithms.Algorithm;
import bitflow4j.algorithms.Filter;
import bitflow4j.io.MetricInputStream;
import bitflow4j.io.MetricOutputStream;
import bitflow4j.io.aggregate.InputStreamProducer;
import bitflow4j.io.file.FileMetricReader;
import bitflow4j.io.fork.AbstractFork;

import java.io.File;
import java.io.IOException;

/**
 * Created by Malcolm-X on 14.12.2016.
 */
public interface AlgorithmPipeline {

    AlgorithmPipeline input(String name, MetricInputStream input);

    AlgorithmPipeline producer(InputStreamProducer producer);

    AlgorithmPipeline cache(File cacheFolder);

    AlgorithmPipeline cache(File cacheFolder, boolean printParameterHashes);

    AlgorithmPipeline step(Filter algo);

    AlgorithmPipeline step(Algorithm algo);

    <T> AlgorithmPipeline fork(AbstractFork<T> fork, ForkHandler<T> handler);

    AlgorithmPipeline postExecute(Runnable runnable);

    AlgorithmPipeline csvOutput(String filename) throws IOException;

    AlgorithmPipeline output(MetricOutputStream outputStream);

    AlgorithmPipeline consoleOutput(String outputMarshaller);

    AlgorithmPipeline fileOutput(String path, String outputMarshaller) throws IOException;

    AlgorithmPipeline fileOutput(File file, String outputMarshaller) throws IOException;

    AlgorithmPipeline emptyOutput();

    void waitForOutput();

    void runAndWait() throws IOException;

    void runApp() throws IOException;

    public interface ForkHandler<T> {
        void buildForkedPipeline(T key, AlgorithmPipeline subPipeline) throws IOException;
    }

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

    static FileMetricReader csvFileReader(File csvFile, FileMetricReader.NameConverter conv) throws IOException {
        FileMetricReader reader = new FileMetricReader(AlgorithmPipeline.getMarshaller("CSV"), conv);
        reader.addFile(csvFile);
        return reader;
    }

    static FileMetricReader binaryFileReader(File binFile, FileMetricReader.NameConverter conv) throws IOException {
        FileMetricReader reader = new FileMetricReader(AlgorithmPipeline.getMarshaller("BIN"), conv);
        reader.addFile(binFile);
        return reader;
    }

    static FileMetricReader fileReader(String file, String format, FileMetricReader.NameConverter conv) throws IOException {
        FileMetricReader reader = new FileMetricReader(AlgorithmPipeline.getMarshaller(format), conv);
        reader.addFile(new File(file));
        return reader;
    }

}
