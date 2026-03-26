package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;

/**
 * ServerMain: starts the central TCP server and accepts multiple clients.
 * Usage:
 *   java server.ServerMain [port]
 * Default port: 5000
 */
public class ServerMain {
    public static void main(String[] args) {
        int port = 5000;
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            RoomManager roomManager = new RoomManager(Path.of("."));
            while (true) {
                Socket client = serverSocket.accept();
                // Set socket timeouts to prevent blocking forever
                client.setSoTimeout(30000); // 30 second timeout
                ClientHandler handler = new ClientHandler(client, roomManager);
                Thread t = new Thread(handler, "ClientHandler-" + client.getRemoteSocketAddress());
                t.setDaemon(true);
                t.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}
