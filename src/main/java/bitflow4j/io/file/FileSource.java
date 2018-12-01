package bitflow4j.io.file;

import bitflow4j.io.SampleInputStream;
import bitflow4j.io.ThreadedSource;
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
import java.util.regex.Pattern;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

/**
 * Created by anton on 4/7/16.
 */
public class FileSource extends ThreadedSource {

    public interface FileFilter {
        boolean shouldInclude(Path path);
    }

    public interface FileVisitor {
        // For directories: Skip directory if result is false.
        // For files: addFile() if result is true.
        boolean visitFile(Path path, BasicFileAttributes basicFileAttributes);
    }

    private final List<File> files = new ArrayList<>();
    private final Marshaller marshaller;
    private boolean robust = false;

    public FileSource(Marshaller marshaller, String... files) throws IOException {
        this.marshaller = marshaller;
        for (String file : files)
            addFile(new File(file));
    }

    public Marshaller getMarshaller() {
        return marshaller;
    }

    public FileSource beRobust() {
        this.robust = true;
        return this;
    }

    public List<File> getFiles() {
        return Collections.unmodifiableList(files);
    }

    @Override
    public String toString() {
        int numFiles = files.size();
        if (numFiles == 1) {
            return String.format("Reading file: %s (format %s)", files.get(0).toString(), getMarshaller());
        } else {
            return String.format("Reading %s files (format %s)", numFiles, getMarshaller());
        }
    }

    public void addFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists())
            throw new IOException("File not found: " + path);
        files.add(file);
    }

    public void addFile(File file) throws IOException {
        addFile(file.toString());
    }

    public void addFileGroup(String path) throws IOException {
        FileGroup group = new FileGroup(path);
        Collection<String> fileNames = group.listFiles();
        if (fileNames.isEmpty())
            throw new IOException("File not found: " + path);
        for (String filename : fileNames) {
            addFile(filename);
        }
    }

    public void addFileGroup(File file) throws IOException {
        addFileGroup(file.toString());
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
        List<File> newFiles = new ArrayList<>();
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
                            newFiles.add(path.toFile());
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                });
        File[] newFilesArray = newFiles.toArray(new File[0]);
        Arrays.sort(newFilesArray);
        for (File f : newFilesArray) {
            addFile(f.getPath());
        }
    }

    public void sortFiles() {
        Comparator<? super File> c = (Comparator<File>) (f1, f2) -> {
            if (f1 == f2) {
                return 0;
            }
            if (f1 == null) {
                return -1;
            }
            if (f2 == null) {
                return 1;
            }
            return String.CASE_INSENSITIVE_ORDER.compare(f1.getName(), f2.getName());
        };
        this.sortFiles(c);
    }

    public void sortFiles(Comparator<? super File> c) {
        files.sort(c);
    }

    @Override
    public void start(TaskPool pool) throws IOException {
        super.start(pool);
        FileSampleReader reader = new FileSampleReader(pool, marshaller);
        readSamples(reader);
        initFinished();
    }

    protected boolean fatalReaderExceptions() {
        // When reading files, shut down on the first read error, except if robust was configured.
        return !robust;
    }

    public class FileSampleReader extends SampleInputStream {

        private final Iterator<File> files;

        public FileSampleReader(TaskPool pool, Marshaller marshaller) {
            super(marshaller);
            this.files = FileSource.this.files.iterator();
        }

        @Override
        public String toString() {
            return "FileSampleReader for " + FileSource.this;
        }

        @Override
        protected NamedInputStream nextInput() throws IOException {
            if (!files.hasNext())
                return null;
            File inputFile = files.next();
            InputStream inputStream = new FileInputStream(inputFile);
            return new NamedInputStream(inputStream, inputFile.toString());
        }
    }

}
