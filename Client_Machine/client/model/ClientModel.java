package client.model;

import client.network.CommandHandler;
import client.network.FileDownloader;
import client.network.FileUploader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * ClientModel: The Model in MVC architecture.
 * Manages the state of the client application and communication with the server.
 * Contains connection state, room code, host/participant flag, and file list.
 * Provides clean methods for server operations without UI dependencies.
 */
public class ClientModel {
    private CommandHandler handler;
    private FileUploader uploader;
    private FileDownloader downloader;
    private String currentRoomCode;
    private List<String> roomFiles;
    private boolean isHost = false;

    /**
     * Establishes connection to the server
     * @param host server hostname or IP address
     * @param port server port number
     * @throws IOException if connection fails
     */
    public void connect(String host, int port) throws IOException {
        handler = new CommandHandler(host, port);
        uploader = new FileUploader(handler);
        downloader = new FileDownloader(handler);
    }

    /**
     * Creates a new room on the server
     * @return room code for the created room
     * @throws IOException if room creation fails
     */
    public String createRoom() throws IOException {
        currentRoomCode = handler.createRoom();
        isHost = true;
        return currentRoomCode;
    }

    /**
     * Joins an existing room
     * @param code room code to join
     * @return true if join successful, false otherwise
     * @throws IOException if join operation fails
     */
    public boolean joinRoom(String code) throws IOException {
        if (handler.joinRoom(code)) {
            currentRoomCode = code;
            isHost = false;
            return true;
        }
        return false;
    }

    /**
     * Leaves the current room
     * @throws IOException if leave operation fails
     */
    public void leaveRoom() throws IOException {
        handler.leaveRoom();
        currentRoomCode = null;
        roomFiles = null;
        isHost = false;
    }

    /**
     * Destroys the current room (host only)
     * @throws IOException if destroy operation fails
     */
    public void destroyRoom() throws IOException {
        handler.destroyRoom();
        currentRoomCode = null;
        roomFiles = null;
        isHost = false;
    }

    /**
     * Refreshes the list of files in the current room
     * @return list of file names in the room
     * @throws IOException if file listing fails
     */
    public List<String> refreshFileList() throws IOException {
        roomFiles = handler.listFiles();
        return roomFiles;
    }

    /**
     * Uploads a file to the current room
     * @param path path to the file to upload
     * @throws IOException if upload fails
     */
    public void uploadFile(Path path) throws IOException {
        uploader.upload(path);
        refreshFileList();
    }

    /**
     * Downloads a file from the current room
     * @param filename name of the file to download
     * @param dest destination path for the downloaded file
     * @throws IOException if download fails
     */
    public void downloadFile(String filename, Path dest) throws IOException {
        downloader.download(filename, dest);
    }

    /**
     * Disconnects from the server
     */
    public void disconnect() {
        if (handler != null) {
            handler.exit();
        }
    }

    // Getters for state information
    public String getCurrentRoomCode() {
        return currentRoomCode;
    }

    public List<String> getRoomFiles() {
        return roomFiles;
    }

    public boolean isConnected() {
        return handler != null;
    }

    public boolean isHost() {
        return isHost;
    }
}