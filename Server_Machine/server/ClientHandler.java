package server;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;

/**
 * ClientHandler: handles one client connection and processes protocol commands.
 * Commands (all sent via writeUTF/readUTF):
 * - CREATE_ROOM
 *   -> Server: "ROOM_CODE <code>"
 * - JOIN_ROOM <code>
 *   -> Server: "JOINED" or "ERROR <msg>"
 * - LIST_FILES
 *   -> Server: "FILE_LIST <count>" then <count> filenames via writeUTF
 * - UPLOAD
 *   -> Server: "READY"
 *      Client: filename (UTF), size (long), then raw bytes
 *      -> Server: "UPLOADED"
 * - DOWNLOAD <filename>
 *   -> Server: "DOWNLOAD_PORT <port>" (opens new ServerSocket)
 *      Client connects to <port>, reads file size (long), then raw bytes
 * - LEAVE_ROOM
 *   -> Server: "LEFT"
 * - DESTROY_ROOM
 *   -> Server: "DESTROYED" or "ERROR <msg>"
 * - EXIT
 *   -> Server closes connection
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final RoomManager roomManager;
    private final FileTransferHandler fileTransferHandler = new FileTransferHandler();
    private DataInputStream in;
    private DataOutputStream out;
    private volatile boolean running = true;
    private Room currentRoom;

    public ClientHandler(Socket socket, RoomManager roomManager) {
        this.socket = socket;
        this.roomManager = roomManager;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public Room getCurrentRoom() {
        return this.currentRoom;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            while (running) {
                String line;
                try {
                    line = in.readUTF();
                } catch (EOFException eof) {
                    break;
                }
                if (line == null) break;
                processCommand(line.trim());
            }
        } catch (IOException e) {
            // Connection issues; clean up below
        } finally {
            try {
                if (currentRoom != null) {
                    // If host disconnects, RoomManager will destroy the room.
                    roomManager.leaveRoom(this);
                }
            } catch (Exception ignored) {}
            closeQuietly();
        }
    }

    private void processCommand(String cmdLine) throws IOException {
        String[] parts = cmdLine.split("\\s+", 2);
        String cmd = parts[0].toUpperCase();
        String arg = parts.length > 1 ? parts[1] : "";
        switch (cmd) {
            case "CREATE_ROOM" -> handleCreateRoom();
            case "JOIN_ROOM" -> handleJoinRoom(arg);
            case "LIST_FILES" -> handleListFiles();
            case "UPLOAD" -> handleUpload();
            case "DOWNLOAD" -> handleDownload(arg);
            case "LEAVE_ROOM" -> handleLeaveRoom();
            case "DESTROY_ROOM" -> handleDestroyRoom();
            case "EXIT" -> {
                running = false;
                // Graceful close happens in finally
            }
            default -> sendError("Unknown command");
        }
    }

    private void handleCreateRoom() throws IOException {
        if (currentRoom != null) {
            sendError("Already in a room");
            return;
        }
        try {
            Room room = roomManager.createRoom(this);
            out.writeUTF("ROOM_CODE " + room.code);
            out.flush();
        } catch (IOException e) {
            sendError("Failed to create room");
        }
    }

    private void handleJoinRoom(String code) throws IOException {
        if (code == null || code.isEmpty()) {
            sendError("Room code required");
            return;
        }
        if (currentRoom != null) {
            sendError("Leave current room first");
            return;
        }
        boolean ok = roomManager.joinRoom(code, this);
        if (ok) {
            out.writeUTF("JOINED");
        } else {
            out.writeUTF("ERROR Room not found");
        }
        out.flush();
    }

    private void handleListFiles() throws IOException {
        if (currentRoom == null) {
            sendError("Not in a room");
            return;
        }
        try {
            List<String> files = roomManager.listFiles(currentRoom.code);
            out.writeUTF("FILE_LIST " + files.size());
            for (String f : files) {
                out.writeUTF(f);
            }
            out.flush();
        } catch (IOException e) {
            sendError("Failed to list files");
        }
    }

    private void handleUpload() throws IOException {
        if (currentRoom == null) {
            sendError("Not in a room");
            return;
        }
        
        // Read file metadata
        String filename = in.readUTF();
        long expectedSize = in.readLong();
        
        // Validate filename
        if (filename == null || filename.isEmpty() || filename.contains("..") || filename.contains("/")) {
            sendError("Invalid filename");
            return;
        }
        
        // Validate file size (prevent DoS)
        if (expectedSize <= 0 || expectedSize > 10L * 1024 * 1024 * 1024) { // 10GB max
            sendError("Invalid file size");
            return;
        }
        
        out.writeUTF("READY");
        out.flush();
        
        try {
            long actualSize = fileTransferHandler.receiveUpload(currentRoom, filename, expectedSize, in);
            
            // Verify complete transfer
            if (actualSize == expectedSize) {
                out.writeUTF("UPLOAD_COMPLETE");
                out.flush();
            } else {
                sendError("Upload incomplete: expected " + expectedSize + " bytes, received " + actualSize);
            }
        } catch (IOException e) {
            sendError("Upload failed: " + e.getMessage());
        }
    }

private void handleDownload(String filename) throws IOException {
    if (currentRoom == null) {
        sendError("Not in a room");
        return;
    }
    if (filename == null || filename.isEmpty()) {
        sendError("Filename required");
        return;
    }
    
    // Validate filename to prevent path traversal
    if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
        sendError("Invalid filename");
        return;
    }
    
    Path folder = roomManager.getRoomFolder(currentRoom.code);
    if (folder == null) {
        sendError("Room not found");
        return;
    }
    
    Path filePath = folder.resolve(filename);
    if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
        sendError("ERROR File not found");
        out.flush();
        return;
    }
    
    long size = Files.size(filePath);
    
    // Send file metadata
    out.writeUTF("FILE");
    out.writeLong(size);
    out.flush();
    
    // Stream file data directly through command connection
    try (InputStream fileIn = new BufferedInputStream(Files.newInputStream(filePath))) {
        byte[] buffer = new byte[8192];
        long remaining = size;
        
        while (remaining > 0) {
            int toRead = (int) Math.min(buffer.length, remaining);
            int read = fileIn.read(buffer, 0, toRead);
            if (read == -1) {
                throw new IOException("Unexpected end of file during download");
            }
            out.write(buffer, 0, read);
            remaining -= read;
        }
        out.flush();
        
        // Send completion signal
        out.writeUTF("COMPLETE");
        out.flush();
        
    } catch (IOException e) {
        System.err.println("Download failed for " + filename + ": " + e.getMessage());
        sendError("Download failed: " + e.getMessage());
    }
}

    private void handleLeaveRoom() throws IOException {
        if (currentRoom == null) {
            sendError("Not in a room");
            return;
        }
        roomManager.leaveRoom(this);
        out.writeUTF("LEFT");
        out.flush();
    }

    private void handleDestroyRoom() throws IOException {
        if (currentRoom == null) {
            sendError("Not in a room");
            return;
        }
        if (currentRoom.host != this) {
            sendError("Only host can destroy");
            return;
        }
        roomManager.destroyRoom(currentRoom.code);
        out.writeUTF("DESTROYED");
        out.flush();
    }

    public void onRoomDestroyed() {
        try {
            out.writeUTF("ROOM_DESTROYED");
            out.flush();
        } catch (IOException ignored) {
        } finally {
            running = false;
            closeQuietly();
        }
    }

    public void forceDisconnect(String reason) {
        try {
            out.writeUTF("DISCONNECTED " + reason);
            out.flush();
        } catch (IOException ignored) {
        } finally {
            running = false;
            closeQuietly();
        }
    }

    private void sendError(String msg) throws IOException {
        out.writeUTF("ERROR " + msg);
        out.flush();
    }

    private void closeQuietly() {
        try { if (in != null) in.close(); } catch (IOException ignored) {}
        try { if (out != null) out.close(); } catch (IOException ignored) {}
        try { socket.close(); } catch (IOException ignored) {}
    }
}
