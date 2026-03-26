package client.model;

import java.util.List;

/**
 * RoomState: Data class representing the state of a room
 * Contains room code, file list, and host status
 */
public class RoomState {
    private final String roomCode;
    private final List<String> files;
    private final boolean isHost;

    public RoomState(String roomCode, List<String> files, boolean isHost) {
        this.roomCode = roomCode;
        this.files = files;
        this.isHost = isHost;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public List<String> getFiles() {
        return files;
    }

    public boolean isHost() {
        return isHost;
    }

    public boolean isInRoom() {
        return roomCode != null;
    }
}