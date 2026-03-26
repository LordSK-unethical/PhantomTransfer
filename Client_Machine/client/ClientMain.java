package client;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

/**
 * ClientMain: CLI-only interface separated from networking and logic.
 * Menu:
 * - Create room
 * - Join room
 * - Upload file
 * - List files
 * - Download file
 * - Leave room
 * - Destroy room (host)
 * - Exit
 */
public class ClientMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String host = "103.160.166.70";
        int port = 5000;
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) {
            try { port = Integer.parseInt(args[1]); } catch (NumberFormatException ignored) {}
        }
        System.out.println("Connecting to server " + host + ":" + port);
        try (CommandHandler handler = new CommandHandler(host, port)) {
            FileUploader uploader = new FileUploader(handler);
            FileDownloader downloader = new FileDownloader(handler);
            while (true) {
                printMenu();
                String choice = scanner.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> {
                            String code = handler.createRoom();
                            System.out.println("Room created: " + code);
                        }
                        case "2" -> {
                            System.out.print("Enter room code: ");
                            String code = scanner.nextLine().trim();
                            boolean ok = handler.joinRoom(code);
                            System.out.println(ok ? "Joined room." : "Failed to join.");
                        }
                        case "3" -> {
                            System.out.print("Path to file to upload: ");
                            String path = scanner.nextLine().trim();
                            uploader.upload(Paths.get(path));
                            System.out.println("Upload complete.");
                        }
                        case "4" -> {
                            List<String> files = handler.listFiles();
                            System.out.println("Files:");
                            for (String f : files) System.out.println(" - " + f);
                        }
                        case "5" -> {
                            System.out.print("Filename to download: ");
                            String fname = scanner.nextLine().trim();
                            Path dest = Paths.get("downloads").resolve(fname);
                            downloader.download(fname, dest);
                            System.out.println("Downloaded to " + dest.toAbsolutePath());
                        }
                        case "6" -> {
                            handler.leaveRoom();
                            System.out.println("Left room.");
                        }
                        case "7" -> {
                            handler.destroyRoom();
                            System.out.println("Room destroyed.");
                        }
                        case "8" -> {
                            handler.exit();
                            System.out.println("Goodbye.");
                            return;
                        }
                        default -> System.out.println("Invalid choice.");
                    }
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1) Create room");
        System.out.println("2) Join room");
        System.out.println("3) Upload file");
        System.out.println("4) List files");
        System.out.println("5) Download file");
        System.out.println("6) Leave room");
        System.out.println("7) Destroy room (host)");
        System.out.println("8) Exit");
        System.out.print("Choose: ");
    }
}
