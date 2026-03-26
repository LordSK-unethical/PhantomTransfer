package server;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * RoomManager: manages lifecycle of rooms, membership, and storage folders.
 * Protocol notes:
 * - A room has a unique code and a dedicated folder under roomsRoot.
 * - The host is the creator; when the host leaves/destroys, the room is destroyed,
 *   all members are disconnected, and the folder is deleted.
 */
public class RoomManager {
    private final Map<String, Room> rooms = new HashMap<>();
    private final Path roomsRoot;
    private final Random random = new Random();

    public RoomManager(Path baseDirectory) throws IOException {
        this.roomsRoot = baseDirectory.resolve("rooms");
        Files.createDirectories(this.roomsRoot);
    }

    public synchronized Room createRoom(ClientHandler host) throws IOException {
        String code;
        do {
            code = generateCode(6);
        } while (rooms.containsKey(code));
        Path folder = roomsRoot.resolve(code);
        Files.createDirectories(folder);
        Room room = new Room(code, host, folder);
        rooms.put(code, room);
        host.setCurrentRoom(room);
        return room;
    }

    public synchronized boolean joinRoom(String code, ClientHandler client) {
        Room room = rooms.get(code);
        if (room == null) return false;
        room.members.add(client);
        client.setCurrentRoom(room);
        return true;
    }

    public synchronized void leaveRoom(ClientHandler client) {
        Room room = client.getCurrentRoom();
        if (room == null) return;
        room.members.remove(client);
        client.setCurrentRoom(null);
        if (client == room.host) {
            destroyRoom(room.code);
        } else if (room.members.isEmpty()) {
            destroyRoom(room.code);
        }
    }

    public synchronized void destroyRoom(String code) {
        Room room = rooms.remove(code);
        if (room == null) return;
        for (ClientHandler ch : new ArrayList<>(room.members)) {
            try {
                ch.onRoomDestroyed();
            } catch (Exception ignored) {
            }
        }
        try {
            deleteRecursively(room.folder);
        } catch (IOException ignored) {
        }
    }

    public synchronized List<String> listFiles(String code) throws IOException {
        Room room = rooms.get(code);
        if (room == null) return Collections.emptyList();
        if (!Files.exists(room.folder)) return Collections.emptyList();
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(room.folder)) {
            List<String> names = new ArrayList<>();
            for (Path p : ds) {
                if (Files.isRegularFile(p)) {
                    names.add(p.getFileName().toString());
                }
            }
            return names;
        }
    }

    public synchronized Path getRoomFolder(String code) {
        Room room = rooms.get(code);
        return room != null ? room.folder : null;
    }

    private String generateCode(int len) {
        String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return sb.toString();
    }

    private void deleteRecursively(Path root) throws IOException {
        if (root == null || !Files.exists(root)) return;
        try (var walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                }
            });
        }
    }
}

class Room {
    final String code;
    final ClientHandler host;
    final Set<ClientHandler> members = Collections.synchronizedSet(new HashSet<>());
    final Path folder;

    Room(String code, ClientHandler host, Path folder) {
        this.code = code;
        this.host = host;
        this.folder = folder;
        this.members.add(host);
    }
}
