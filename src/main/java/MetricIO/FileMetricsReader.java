package MetricIO;

import Marshaller.Marshaller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

/**
 * Created by anton on 4/7/16.
 */
public class FileMetricsReader implements InputStreamProducer {

    public interface NameConverter {
        String convert(File file);
    }

    public interface FileFilter {
        boolean shouldInclude(Path path);
    }

    public interface FileVisitor {
        // For directories: Skip directory if result is false.
        // For files: addFile() if result is true.
        boolean visitFile(Path path, BasicFileAttributes basicFileAttributes);
    }

    public static final NameConverter FILE_PATH = (File file) -> file.getPath();
    public static final NameConverter FILE_NAME = (File file) -> file.getName();

    private List<MetricInputStream> inputs = new ArrayList<>();
    private List<File> files = new ArrayList<>();

    private final Marshaller marshaller;
    private final NameConverter converter;

    // Must be set before calling start()
    public boolean parallelRead = false;

    public FileMetricsReader(Marshaller marshaller, NameConverter converter) {
        this.marshaller = marshaller;
        this.converter = converter;
    }

    public FileMetricsReader(Marshaller marshaller) throws IOException {
        this(marshaller, FILE_NAME);
    }

    public int size() {
        return files.size();
    }

    public void addFiles(String directory, String extension) throws IOException {
        addFiles(directory, (Path path) -> path.getFileName().toString().endsWith(extension));
    }

    public void addFiles(String directory, FileFilter filter) throws IOException {
        addFiles(directory,
            (Path path, BasicFileAttributes attr) ->
                attr.isDirectory() || (attr.isRegularFile() && filter.shouldInclude(path)));
    }

    public void addFiles(String directory, FileVisitor visitor) throws IOException {
        Files.walkFileTree(new File(directory).toPath(),
            new HashSet<>(Arrays.asList(FOLLOW_LINKS)),
            Integer.MAX_VALUE,
            new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attr) throws IOException {
                    boolean result = visitor.visitFile(path, attr);
                    return result ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException {
                    boolean result = visitor.visitFile(path, attr);
                    if (result)
                        addFile(path.toFile().getPath());
                    return FileVisitResult.SKIP_SUBTREE;
                }
            });
    }

    public void addFile(String path) throws IOException {
        addFile(new File(path));
    }

    public void addFile(File file) throws IOException {
        files.add(file);
        FileInputStream fileInput = new FileInputStream(file);
        MetricInputStream input = new MetricReader(fileInput, marshaller);
        inputs.add(input);
    }

    // Must be called after all add* invocations.
    public void start(MetricInputAggregator aggregator) {
        aggregator.producerStarting(this);
        for (int i = 0; i < inputs.size(); i++) {
            String name = converter.convert(files.get(i));
            MetricInputStream input = inputs.get(i);
            aggregator.addInput(name, input);
        }
        aggregator.producerFinished(this);
    }

}
