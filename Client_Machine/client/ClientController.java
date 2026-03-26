package client;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * ClientController: The Controller in MVC architecture.
 * Listens for UI events and updates Model and View.
 */
public class ClientController {
    private ClientModel model;
    private ClientView view;
    private Timer refreshTimer;

    public ClientController(ClientModel model, ClientView view) {
        this.model = model;
        this.view = view;
        initListeners();
        initRefreshTimer();
    }

    private void initRefreshTimer() {
        // Refresh every 5 seconds if in a room
        refreshTimer = new Timer(5000, e -> {
            if (model.isConnected() && model.getCurrentRoomCode() != null) {
                refreshFileListSilently();
            }
        });
        refreshTimer.start();
    }

    private void refreshFileListSilently() {
        try {
            List<String> files = model.refreshFileList();
            SwingUtilities.invokeLater(() -> view.updateFileList(files));
        } catch (IOException ignored) {
            // Silently fail during background refresh
        }
    }

    private void initListeners() {
        view.getConnectButton().addActionListener(e -> connect());
        view.getCreateRoomButton().addActionListener(e -> createRoom());
        view.getJoinRoomButton().addActionListener(e -> joinRoom());
        view.getUploadButton().addActionListener(e -> upload());
        view.getDownloadButton().addActionListener(e -> download());
        view.getLeaveButton().addActionListener(e -> leaveRoom());
        view.getDestroyButton().addActionListener(e -> destroyRoom());
        view.getRefreshButton().addActionListener(e -> refreshFileList());
    }

    private void connect() {
        String host = view.getIp();
        String portStr = view.getPort();
        try {
            int port = Integer.parseInt(portStr);
            model.connect(host, port);
            view.showRoomScreen();
            view.showMessage("Connected to server " + host + ":" + port);
        } catch (NumberFormatException e) {
            view.showError("Invalid port number.");
        } catch (IOException e) {
            view.showError("Connection failed: " + e.getMessage());
        }
    }

    private void createRoom() {
        try {
            String code = model.createRoom();
            view.setRoomLabel("Room: " + code);
            view.setRoomActionsEnabled(true);
            refreshFileList();
            view.showMessage("Room created: " + code);
        } catch (IOException e) {
            view.showError("Failed to create room: " + e.getMessage());
        }
    }

    private void joinRoom() {
        String code = view.promptInput("Enter Room Code:");
        if (code == null || code.trim().isEmpty()) return;
        try {
            if (model.joinRoom(code.trim())) {
                view.setRoomLabel("Room: " + code);
                view.setRoomActionsEnabled(true);
                refreshFileList();
                view.showMessage("Joined room: " + code);
            } else {
                view.showError("Invalid room code.");
            }
        } catch (IOException e) {
            view.showError("Failed to join room: " + e.getMessage());
        }
    }

    private void leaveRoom() {
        try {
            model.leaveRoom();
            view.setRoomLabel("Not in a room");
            view.setRoomActionsEnabled(false);
            view.updateFileList(null);
            view.showMessage("Left room.");
        } catch (IOException e) {
            view.showError("Error leaving room: " + e.getMessage());
        }
    }

    private void destroyRoom() {
        try {
            model.destroyRoom();
            view.setRoomLabel("Not in a room");
            view.setRoomActionsEnabled(false);
            view.updateFileList(null);
            view.showMessage("Room destroyed.");
        } catch (IOException e) {
            view.showError("Error destroying room: " + e.getMessage());
        }
    }

    private void refreshFileList() {
        try {
            List<String> files = model.refreshFileList();
            view.updateFileList(files);
        } catch (IOException e) {
            view.showError("Failed to refresh file list: " + e.getMessage());
        }
    }

    private void upload() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            Path path = chooser.getSelectedFile().toPath();
            new Thread(() -> {
                try {
                    model.uploadFile(path);
                    SwingUtilities.invokeLater(() -> {
                        refreshFileList();
                        view.showMessage("Upload complete.");
                    });
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> view.showError("Upload failed: " + e.getMessage()));
                }
            }).start();
        }
    }

    private void download() {
        String filename = view.getSelectedFile();
        if (filename == null) {
            view.showError("Select a file to download.");
            return;
        }
        
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(filename));
        if (chooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            Path dest = chooser.getSelectedFile().toPath();
            new Thread(() -> {
                try {
                    model.downloadFile(filename, dest);
                    SwingUtilities.invokeLater(() -> view.showMessage("Download complete to " + dest.toAbsolutePath()));
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> view.showError("Download failed: " + e.getMessage()));
                }
            }).start();
        }
    }
}
