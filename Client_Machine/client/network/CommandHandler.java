package client.network;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CommandHandler: Handles client-server protocol commands over a single TCP connection.
 * Implements the file transfer protocol and manages socket communication.
 * This class should only handle network operations and protocol implementation.
 */
public class CommandHandler implements Closeable {
    private final Socket socket;
    private final DataInputStream in;
    private final DataOutputStream out;
    private boolean connected = true;

    /**
     * Establishes connection to the server
     * @param host server hostname or IP address
     * @param port server port number
     * @throws IOException if connection fails
     */
    public CommandHandler(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        // Set socket timeout to prevent blocking forever
        this.socket.setSoTimeout(30000); // 30 second timeout
        this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        this.out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    /**
     * Creates a new room on the server
     * @return room code for the created room
     * @throws IOException if room creation fails
     */
    public synchronized String createRoom() throws IOException {
        ensureConnected();
        out.writeUTF("CREATE_ROOM");
        out.flush();
        String resp = in.readUTF();
        if (resp.startsWith("ROOM_CODE ")) {
            return resp.substring("ROOM_CODE ".length()).trim();
        }
        throw new IOException(resp);
    }

    /**
     * Joins an existing room
     * @param code room code to join
     * @return true if join successful, false otherwise
     * @throws IOException if join operation fails
     */
    public synchronized boolean joinRoom(String code) throws IOException {
        ensureConnected();
        out.writeUTF("JOIN_ROOM " + code);
        out.flush();
        String resp = in.readUTF();
        if (resp.equals("JOINED")) return true;
        throw new IOException(resp);
    }

    /**
     * Lists files in the current room
     * @return list of file names in the room
     * @throws IOException if file listing fails
     */
    public synchronized List<String> listFiles() throws IOException {
        ensureConnected();
        out.writeUTF("LIST_FILES");
        out.flush();
        String header = in.readUTF();
        if (!header.startsWith("FILE_LIST ")) {
            throw new IOException(header);
        }
        int count = Integer.parseInt(header.substring("FILE_LIST ".length()).trim());
        List<String> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(in.readUTF());
        }
        return result;
    }

    /**
     * Uploads a file to the server
     * @param file path to the file to upload
     * @throws IOException if upload fails
     */
    public synchronized void uploadFile(Path file) throws IOException {
        ensureConnected();
        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            throw new FileNotFoundException("File not found: " + file);
        }
        
        String name = file.getFileName().toString();
        long size = Files.size(file);
        
        // Send upload command and metadata
        out.writeUTF("UPLOAD");
        out.writeUTF(name);
        out.writeLong(size);
        out.flush();
        
        // Wait for server readiness
        String ready = in.readUTF();
        if (!"READY".equals(ready)) {
            throw new IOException("Server not ready: " + ready);
        }
        
        // Stream file data
        try (InputStream is = new BufferedInputStream(Files.newInputStream(file))) {
            byte[] buffer = new byte[8192];
            long remaining = size;
            while (remaining > 0) {
                int toRead = (int) Math.min(buffer.length, remaining);
                int read = is.read(buffer, 0, toRead);
                if (read == -1) {
                    throw new EOFException("Unexpected end of file during upload");
                }
                out.write(buffer, 0, read);
                remaining -= read;
            }
            out.flush();
        }
        
        // Verify upload completion
        String done = in.readUTF();
        if (!"UPLOAD_COMPLETE".equals(done)) {
            throw new IOException("Upload failed: " + done);
        }
    }

    /**
     * Downloads a file from the server
     * @param filename name of the file to download
     * @param destination destination path for the downloaded file
     * @throws IOException if download fails
     */
    public synchronized void downloadFile(String filename, Path destination) throws IOException {
        ensureConnected();
        
        // Send download command with filename
        out.writeUTF("DOWNLOAD " + filename);
        out.flush();

        // Read server response
        String response = in.readUTF();
        if (response.startsWith("ERROR")) {
            throw new IOException(response);
        }
        
        // Handle both old and new protocol formats
        long size;
        if (response.startsWith("DOWNLOAD_PORT")) {
            // Old protocol - separate data connection
            int dataPort = Integer.parseInt(response.substring("DOWNLOAD_PORT ".length()).trim());
            String host = socket.getInetAddress().getHostAddress();
            
            try (Socket dataSocket = new Socket(host, dataPort);
                 DataInputStream dataIn = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()))) {
                size = dataIn.readLong();
                
                Files.createDirectories(destination.getParent() == null ? Paths.get(".") : destination.getParent());
                try (OutputStream fileOut = new BufferedOutputStream(Files.newOutputStream(destination, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
                    byte[] buffer = new byte[8192];
                    long remaining = size;
                    while (remaining > 0) {
                        int toRead = (int) Math.min(buffer.length, remaining);
                        int read = dataIn.read(buffer, 0, toRead);
                        if (read == -1) throw new EOFException("Unexpected end of stream during download");
                        fileOut.write(buffer, 0, read);
                        remaining -= read;
                    }
                }
            }
            return; // Old protocol completed
        } else if ("FILE".equals(response)) {
            // New protocol - single connection
            size = in.readLong();
        } else {
            throw new IOException("Protocol error: Expected FILE or DOWNLOAD_PORT, but got: " + response);
        }
    
        // Prepare destination directory
        Files.createDirectories(destination.getParent() == null ? Paths.get(".") : destination.getParent());
        
        // Stream file data directly from command connection
        try (OutputStream fileOut = new BufferedOutputStream(Files.newOutputStream(destination, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
            byte[] buffer = new byte[8192];
            long remaining = size;
            long totalReceived = 0;
            
            while (remaining > 0) {
                int toRead = (int) Math.min(buffer.length, remaining);
                int read = in.read(buffer, 0, toRead);
                if (read == -1) {
                    throw new EOFException("Unexpected end of stream during download");
                }
                fileOut.write(buffer, 0, read);
                totalReceived += read;
                remaining -= read;
            }
            fileOut.flush();
            
            // Verify complete transfer
            if (totalReceived != size) {
                throw new IOException("Download incomplete: expected " + size + " bytes, received " + totalReceived);
            }
        }
        
        // Verify completion signal
        String complete = in.readUTF();
        if (!"COMPLETE".equals(complete)) {
            throw new IOException("Download failed: " + complete);
        }
    }

    /**
     * Leaves the current room
     * @throws IOException if leave operation fails
     */
    public synchronized void leaveRoom() throws IOException {
        ensureConnected();
        out.writeUTF("LEAVE_ROOM");
        out.flush();
        String resp = in.readUTF();
        if (!"LEFT".equals(resp)) throw new IOException(resp);
    }

    /**
     * Destroys the current room (host only)
     * @throws IOException if destroy operation fails
     */
    public synchronized void destroyRoom() throws IOException {
        ensureConnected();
        out.writeUTF("DESTROY_ROOM");
        out.flush();
        String resp = in.readUTF();
        if (!"DESTROYED".equals(resp)) throw new IOException(resp);
    }

    /**
     * Gracefully exits the connection
     */
    public synchronized void exit() {
        if (!connected) return;
        try {
            out.writeUTF("EXIT");
            out.flush();
        } catch (IOException ignored) {
        } finally {
            connected = false;
            try { in.close(); } catch (IOException ignored) {}
            try { out.close(); } catch (IOException ignored) {}
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    /**
     * Ensures the connection is active
     * @throws IOException if not connected
     */
    private void ensureConnected() throws IOException {
        if (!connected || socket.isClosed()) throw new IOException("Not connected");
    }

    /**
     * Closes the connection
     */
    @Override
    public void close() {
        exit();
    }
}