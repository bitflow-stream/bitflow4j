package metrics.io.file;

import com.google.common.io.ByteArrayDataOutput;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Created by anton on 4/16/16.
 * <p>
 * Represents a group of files within one folder named basename(-[num])?.ext
 */
public class FileGroup {

    public final Path folder;
    public final String fileStart;
    public final String fileEnd;

    public FileGroup(File file) {
        this(file.getAbsolutePath());
    }

    public FileGroup(String baseFileName) {
        Path path = new File(baseFileName).toPath();
        folder = path.getParent();
        String filename = path.getFileName().toString();
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            fileStart = filename.substring(0, dotIndex);
            fileEnd = filename.substring(dotIndex);
        } else {
            fileStart = filename;
            fileEnd = "";
        }
    }

    public Pattern getFilePattern(String suffix) {
        return Pattern.compile("^" + fileStart + "(\\-" + suffix + ")?" + fileEnd + "$");
    }

    public Pattern getFilePattern() {
        return getFilePattern(".*");
    }

    public Pattern getNumbersFilePattern() {
        return getFilePattern("[1-9]+");
    }

    void deleteFiles() throws IOException {
        walkFiles(
                (path) -> {
                    if (!path.toFile().delete())
                        throw new IOException("Could not delete file " + path.toString());
                }
        );
    }

    public String getFile(String indexString) {
        String filename;
        if (indexString == null || indexString.isEmpty()) {
            filename = fileStart + fileEnd;
        } else {
            filename = fileStart + "-" + indexString + fileEnd;
        }
        if (folder == null) {
            return filename;
        } else {
            return folder.resolve(filename).toString();
        }
    }

    public String getFile(int index) {
        return getFile(index <= 0 ? null : String.valueOf(index));
    }

    public interface FileVisitor {
        void visit(Path file) throws IOException;
    }

    public void walkFiles(FileVisitor visitor) throws IOException {
        Pattern pattern = getFilePattern();
        Path folder = this.folder != null ? this.folder : new File(".").toPath();
        Files.walkFileTree(folder, EnumSet.noneOf(FileVisitOption.class), 1,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attr) throws IOException {
                        if (attr.isSymbolicLink() || attr.isRegularFile()) {
                            String filename = path.getFileName().toString();
                            if (pattern.matcher(filename).matches()) {
                                visitor.visit(path);
                            }
                        }
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                });
    }

    public Collection<String> listFiles() throws IOException {
        Set<String> files = new TreeSet<>();
        walkFiles((path) -> files.add(path.toString()));
        return files;
    }

    public void hashParameters(ByteArrayDataOutput bytes) {
        bytes.writeChars(folder == null ? "" : folder.toString());
        bytes.writeChars(fileStart);
        bytes.writeChars(fileEnd);
    }

}
