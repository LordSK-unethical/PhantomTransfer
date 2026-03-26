package client;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * ClientModel: The Model in MVC architecture.
 * Manages the state of the client application and communication with the server.
 */
public class ClientModel {
    private CommandHandler handler;
    private FileUploader uploader;
    private FileDownloader downloader;
    private String currentRoomCode;
    private List<String> roomFiles;

    public void connect(String host, int port) throws IOException {
        handler = new CommandHandler(host, port);
        uploader = new FileUploader(handler);
        downloader = new FileDownloader(handler);
    }

    public String createRoom() throws IOException {
        currentRoomCode = handler.createRoom();
        return currentRoomCode;
    }

    public boolean joinRoom(String code) throws IOException {
        if (handler.joinRoom(code)) {
            currentRoomCode = code;
            return true;
        }
        return false;
    }

    public void leaveRoom() throws IOException {
        handler.leaveRoom();
        currentRoomCode = null;
        roomFiles = null;
    }

    public void destroyRoom() throws IOException {
        handler.destroyRoom();
        currentRoomCode = null;
        roomFiles = null;
    }

    public List<String> refreshFileList() throws IOException {
        roomFiles = handler.listFiles();
        return roomFiles;
    }

    public void uploadFile(Path path) throws IOException {
        uploader.upload(path);
        refreshFileList();
    }

    public void downloadFile(String filename, Path dest) throws IOException {
        downloader.download(filename, dest);
    }

    public void disconnect() {
        if (handler != null) {
            handler.exit();
        }
    }

    public String getCurrentRoomCode() {
        return currentRoomCode;
    }

    public List<String> getRoomFiles() {
        return roomFiles;
    }

    public boolean isConnected() {
        return handler != null;
    }
}
