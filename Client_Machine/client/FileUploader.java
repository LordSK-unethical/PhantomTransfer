package client;

import java.io.IOException;
import java.nio.file.Path;

/**
 * FileUploader: helper to upload a file through CommandHandler.
 */
public class FileUploader {
    private final CommandHandler handler;

    public FileUploader(CommandHandler handler) {
        this.handler = handler;
    }

    public void upload(Path file) throws IOException {
        handler.uploadFile(file);
    }
}
