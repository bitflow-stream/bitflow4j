package bitflow4j.io.file;

import bitflow4j.io.MetricReader;
import bitflow4j.io.ThreadedSampleSource;
import bitflow4j.io.marshall.Marshaller;
import bitflow4j.task.TaskPool;

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
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

/**
 * Created by anton on 4/7/16.
 */
public class FileMetricReader extends ThreadedSampleSource {

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

    private final List<File> files = new ArrayList<>();
    private final List<String> sourceNames = new ArrayList<>();
    private final Marshaller marshaller;
    private final NameConverter converter;

    private TaskPool taskPool;
    private boolean keepAlive = false;

    public FileMetricReader(Marshaller marshaller, NameConverter converter) {
        this.marshaller = marshaller;
        if (converter == null)
            converter = FILE_NAME;
        this.converter = converter;
    }

    public FileMetricReader(Marshaller marshaller) {
        this(marshaller, FILE_NAME);
    }

    public FileMetricReader keepAlive() {
        this.keepAlive = true;
        return this;
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        this.taskPool = pool;
        FileSampleReader reader = new FileSampleReader(pool, marshaller);
        readSamples(pool, reader, true);
    }

    protected boolean readerException() {
        // When reading files, shut down on the first read error.
        if (!keepAlive) {
            stopTasks();
        }
        return false;
    }

    @Override
    public void run() throws IOException {
        // All readers have been added, so we can immediately start waiting for them to finish
        if (keepAlive) {
            taskPool.waitForShutdown();
        } else {
            shutDown();
        }
        super.run();
    }

    private class FileSampleReader extends MetricReader {

        private final Iterator<File> files;
        private final Iterator<String> sourceNames;

        public FileSampleReader(TaskPool pool, Marshaller marshaller) {
            super(pool, marshaller);
            files = FileMetricReader.this.files.iterator();
            sourceNames = FileMetricReader.this.sourceNames.iterator();
        }

        @Override
        public String toString() {
            return "Read " + FileMetricReader.this.files.size() + " files";
        }

        @Override
        protected NamedInputStream nextInput() throws IOException {
            if (!files.hasNext())
                return null;
            File inputFile = files.next();
            InputStream inputStream = new FileInputStream(inputFile);
            String sourceName = sourceNames.next();
            return new NamedInputStream(inputStream, sourceName);
        }
    }

    public int size() {
        return files.size();
    }

    public List<File> getFiles() {
        return Collections.unmodifiableList(files);
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

    public void addFile(String path) throws IOException {
        addFile(path, path);
    }

    public void addFile(File file) throws IOException {
        addFile(file.toString());
    }

    private void addFile(String inputNameBase, String path) throws IOException {
        File file = new File(path);
        if (!file.exists())
            throw new IOException("File not found: " + path);
        String source = converter == null ? null : converter.convert(new File(inputNameBase));
        files.add(file);
        sourceNames.add(source);
    }

}
