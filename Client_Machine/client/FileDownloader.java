package client;

import java.io.IOException;
import java.nio.file.Path;

/**
 * FileDownloader: helper to download a file through CommandHandler.
 */
public class FileDownloader {
    private final CommandHandler handler;

    public FileDownloader(CommandHandler handler) {
        this.handler = handler;
    }

    public void download(String filename, Path destination) throws IOException {
        handler.downloadFile(filename, destination);
    }
}
