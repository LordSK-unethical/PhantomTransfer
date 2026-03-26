package client.network;

import java.io.IOException;
import java.nio.file.Path;

/**
 * FileDownloader: Helper class for file download operations
 * Wraps the download functionality of CommandHandler
 */
public class FileDownloader {
    private final CommandHandler handler;

    /**
     * Creates a new FileDownloader instance
     * @param handler CommandHandler for network communication
     */
    public FileDownloader(CommandHandler handler) {
        this.handler = handler;
    }

    /**
     * Downloads a file from the server
     * @param filename name of the file to download
     * @param destination destination path for the downloaded file
     * @throws IOException if download fails
     */
    public void download(String filename, Path destination) throws IOException {
        handler.downloadFile(filename, destination);
    }
}