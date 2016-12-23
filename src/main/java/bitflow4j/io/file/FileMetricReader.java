package bitflow4j.io.file;

import bitflow4j.Marshaller;
import bitflow4j.io.MetricInputStream;
import bitflow4j.io.MetricReader;
import bitflow4j.io.aggregate.InputStreamProducer;
import bitflow4j.io.aggregate.MetricInputAggregator;
import bitflow4j.main.ParameterHash;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

/**
 * Created by anton on 4/7/16.
 */
public class FileMetricReader implements InputStreamProducer {

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

    public static final NameConverter FILE_PATH = File::getPath;
    public static final NameConverter FILE_NAME = File::getName;

    private final List<MetricInputStream> inputs = new ArrayList<>();
    private final List<File> files = new ArrayList<>();

    private final Marshaller marshaller;
    private final NameConverter converter;

    public FileMetricReader(Marshaller marshaller, NameConverter converter) {
        this.marshaller = marshaller;
        this.converter = converter;
    }

    public FileMetricReader(Marshaller marshaller) throws IOException {
        this(marshaller, FILE_NAME);
    }

    public int size() {
        return files.size();
    }

    public void addFiles(String root, Pattern pattern) throws IOException {
        addFiles(root, (path) -> pattern.matcher(path.toString()).matches());
    }

    public void addFiles(String directory, String extension) throws IOException {
        addFiles(directory, (path) -> path.getFileName().toString().endsWith(extension));
    }

    public void addFiles(String directory, FileFilter filter) throws IOException {
        addFiles(directory,
                (Path path, BasicFileAttributes attr) ->
                        attr.isDirectory() || (attr.isRegularFile() && filter.shouldInclude(path)));
    }

    public void addFiles(String directory, FileVisitor visitor) throws IOException {
        Files.walkFileTree(new File(directory).toPath(),
                new HashSet<>(Collections.singletonList(FOLLOW_LINKS)),
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
        String source = converter == null ? null : converter.convert(new File(path));
        FileGroup group = new FileGroup(path);
        Collection<String> filenames = group.listFiles();
        if (filenames.isEmpty())
            throw new IOException("File not found: " + path);
        for (String filename : filenames) {
            File file = new File(filename);
            files.add(file);
            FileInputStream fileInput = new FileInputStream(file);
            MetricInputStream input = new MetricReader(new BufferedInputStream(fileInput), source, marshaller);
            inputs.add(input);
        }
    }

    public void addFile(File file) throws IOException {
        addFile(file.toString());
    }

    public List<File> getFiles() {
        return Collections.unmodifiableList(files);
    }

    // Must be called after all add* invocations.
    public void start(MetricInputAggregator aggregator) {
        aggregator.producerStarting(this);
        for (int i = 0; i < inputs.size(); i++) {
            String name = converter == null ? null : converter.convert(files.get(i));
            MetricInputStream input = inputs.get(i);
            aggregator.addInput(name, input);
        }
        aggregator.producerFinished(this);
    }

}
