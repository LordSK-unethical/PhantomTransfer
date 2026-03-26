package client.network;

import java.io.IOException;
import java.nio.file.Path;

/**
 * FileUploader: Helper class for file upload operations
 * Wraps the upload functionality of CommandHandler
 */
public class FileUploader {
    private final CommandHandler handler;

    /**
     * Creates a new FileUploader instance
     * @param handler CommandHandler for network communication
     */
    public FileUploader(CommandHandler handler) {
        this.handler = handler;
    }

    /**
     * Uploads a file to the server
     * @param file path to the file to upload
     * @throws IOException if upload fails
     */
    public void upload(Path file) throws IOException {
        handler.uploadFile(file);
    }
}