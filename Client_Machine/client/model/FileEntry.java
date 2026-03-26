package client.model;

import java.nio.file.Path;

/**
 * FileEntry: Data class representing a file in the room
 * Contains file name and optional local path reference
 */
public class FileEntry {
    private final String filename;
    private final Path localPath;

    public FileEntry(String filename) {
        this.filename = filename;
        this.localPath = null;
    }

    public FileEntry(String filename, Path localPath) {
        this.filename = filename;
        this.localPath = localPath;
    }

    public String getFilename() {
        return filename;
    }

    public Path getLocalPath() {
        return localPath;
    }

    public boolean hasLocalCopy() {
        return localPath != null;
    }

    @Override
    public String toString() {
        return filename;
    }
}