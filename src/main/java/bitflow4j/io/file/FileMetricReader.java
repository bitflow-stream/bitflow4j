package bitflow4j.io.file;

import bitflow4j.Header;
import bitflow4j.Marshaller;
import bitflow4j.Sample;
import bitflow4j.io.InputStreamClosedException;
import bitflow4j.io.MetricInputStream;
import bitflow4j.io.MetricReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

/**
 * Created by anton on 4/7/16.
 */
public class FileMetricReader implements MetricInputStream {

    private static final Logger logger = Logger.getLogger(FileMetricReader.class.getName());

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
    public static final NameConverter FILE_DIRECTORY_NAME = (File file) -> file.getParentFile().getName();

    private final List<MetricReader> inputs = new ArrayList<>();
    private final List<File> files = new ArrayList<>();

    private Iterator<MetricReader> inputIterator;
    private MetricReader currentInput;

    private final Marshaller marshaller;
    private final NameConverter converter;

    private Header previousHeader = null;

    public FileMetricReader(Marshaller marshaller, NameConverter converter) {
        this.marshaller = marshaller;
        if (converter == null)
            converter = FILE_NAME;
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

    public void addFileGroup(String path) throws IOException {
        FileGroup group = new FileGroup(path);
        Collection<String> fileNames = group.listFiles();
        if (fileNames.isEmpty())
            throw new IOException("File not found: " + path);
        for (String filename : fileNames) {
            addFile(path, filename);
        }
    }

    public void addFileGroup(File file) throws IOException {
        addFileGroup(file.toString());
    }

    private void addFile(String inputNameBase, String path) throws IOException {
        File file = new File(path);
        if (!file.exists())
            throw new IOException("File not found: " + path);
        files.add(file);
        InputStream fileInput = new FileInputStream(file);
        String source = converter == null ? null : converter.convert(new File(inputNameBase));
        MetricReader reader = new MetricReader(fileInput, source, marshaller);
        inputs.add(reader);
    }

    public void addFile(String path) throws IOException {
        addFile(path, path);
    }

    public void addFile(File file) throws IOException {
        addFile(file.toString());
    }

    public List<File> getFiles() {
        return Collections.unmodifiableList(files);
    }

    @Override
    public synchronized Sample readSample() throws IOException {
        if (currentInput == null) {
            closeInput();
            currentInput = nextInput();
        }
        if (currentInput == null)
            throw new InputStreamClosedException();
        try {
            return currentInput.readSample();
        } catch (InputStreamClosedException e) {
            // One file finished, start reading the next.
            closeInput();
            return readSample();
        }
    }

    private void closeInput() {
        MetricReader input = currentInput;
        currentInput = null;
        if (input != null) {
            previousHeader = input.currentHeader();
            try {
                input.close();
                logger.info("Closed file " + input.sourceName);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error closing file", e);
            }
        }
    }

    private MetricReader nextInput() {
        if (inputIterator == null)
            inputIterator = inputs.iterator();
        if (!inputIterator.hasNext()) {
            return null;
        }
        MetricReader next = inputIterator.next();
        if (previousHeader != null) {
            next.setCurrentHeader(previousHeader);
        }
        return next;
    }

}
