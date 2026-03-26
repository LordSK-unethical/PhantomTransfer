package client.controller;

import client.model.ClientModel;
import client.view.ClientView;
import client.view.DialogFactory;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * ClientController: The Controller in MVC architecture.
 * Listens for UI events and updates Model and View accordingly.
 * Handles user interactions, validates input, and coordinates between model and view.
 */
public class ClientController {
    private final ClientModel model;
    private final ClientView view;
    private Timer refreshTimer;

    /**
     * Creates a new ClientController
     * @param model the application model
     * @param view the application view
     */
    public ClientController(ClientModel model, ClientView view) {
        this.model = model;
        this.view = view;
        initListeners();
        initRefreshTimer();
    }

    /**
     * Initializes the automatic refresh timer
     */
    private void initRefreshTimer() {
        // Refresh every 5 seconds if in a room
        refreshTimer = new Timer(5000, e -> {
            if (model.isConnected() && model.getCurrentRoomCode() != null) {
                refreshFileListSilently();
            }
        });
        refreshTimer.start();
    }

    /**
     * Silently refreshes file list for background updates
     */
    private void refreshFileListSilently() {
        try {
            List<String> files = model.refreshFileList();
            SwingUtilities.invokeLater(() -> view.updateFileList(files));
        } catch (IOException ignored) {
            // Silently fail during background refresh
        }
    }

    /**
     * Initializes all UI event listeners
     */
    private void initListeners() {
        view.setConnectListener(this::handleConnect);
        view.setCreateRoomListener(this::handleCreateRoom);
        view.setJoinRoomListener(this::handleJoinRoom);
        view.setUploadListener(this::handleUpload);
        view.setDownloadListener(this::handleDownload);
        view.setLeaveRoomListener(this::handleLeaveRoom);
        view.setDestroyRoomListener(this::handleDestroyRoom);
        view.setRefreshListener(this::handleRefresh);
    }

    /**
     * Handles connect button click
     */
    private void handleConnect() {
        String host = view.getIp();
        String portStr = view.getPort();
        
        if (host.isEmpty()) {
            view.showError("Please enter a server IP address.");
            return;
        }
        
        try {
            int port = Integer.parseInt(portStr);
            model.connect(host, port);
            view.showRoomScreen();
            view.setRoomActionsEnabled(false);
            view.showMessage("Connected to server " + host + ":" + port);
        } catch (NumberFormatException e) {
            view.showError("Invalid port number. Please enter a valid number.");
        } catch (IOException e) {
            view.showError("Connection failed: " + e.getMessage());
        }
    }

    /**
     * Handles create room button click
     */
    private void handleCreateRoom() {
        try {
            String code = model.createRoom();
            view.setRoomLabel("Room: " + code + " (Host)");
            view.setRoomActionsEnabled(true);
            view.setHostMode(true);
            refreshFileList();
            view.showMessage("Room created: " + code);
        } catch (IOException e) {
            view.showError("Failed to create room: " + e.getMessage());
        }
    }

    /**
     * Handles join room button click
     */
    private void handleJoinRoom() {
        String code = view.promptInput("Enter Room Code:");
        if (code == null || code.trim().isEmpty()) return;
        
        try {
            if (model.joinRoom(code.trim())) {
                view.setRoomLabel("Room: " + code);
                view.setRoomActionsEnabled(true);
                view.setHostMode(false);
                refreshFileList();
                view.showMessage("Joined room: " + code);
            } else {
                view.showError("Invalid room code or room does not exist.");
            }
        } catch (IOException e) {
            view.showError("Failed to join room: " + e.getMessage());
        }
    }

    /**
     * Handles leave room button click
     */
    private void handleLeaveRoom() {
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

    /**
     * Handles destroy room button click
     */
    private void handleDestroyRoom() {
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

    /**
     * Handles refresh button click
     */
    private void handleRefresh() {
        try {
            List<String> files = model.refreshFileList();
            view.updateFileList(files);
            view.showMessage("File list refreshed.");
        } catch (IOException e) {
            view.showError("Failed to refresh file list: " + e.getMessage());
        }
    }

    /**
     * Handles upload button click
     */
    private void handleUpload() {
        java.io.File selectedFile = DialogFactory.showOpenFileDialog(view);
        if (selectedFile == null) return;
        
        Path path = selectedFile.toPath();
        new Thread(() -> {
            try {
                model.uploadFile(path);
                SwingUtilities.invokeLater(() -> {
                    refreshFileList();
                    view.showMessage("Upload complete: " + path.getFileName());
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> 
                    view.showError("Upload failed: " + e.getMessage()));
            }
        }).start();
    }

    /**
     * Handles download button click
     */
    private void handleDownload() {
        String filename = view.getSelectedFile();
        if (filename == null) {
            view.showError("Please select a file to download.");
            return;
        }
        
        java.io.File destination = DialogFactory.showSaveFileDialog(view, filename);
        if (destination == null) return;
        
        Path dest = destination.toPath();
        new Thread(() -> {
            try {
                model.downloadFile(filename, dest);
                SwingUtilities.invokeLater(() -> 
                    view.showMessage("Download complete to " + dest.toAbsolutePath()));
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> 
                    view.showError("Download failed: " + e.getMessage()));
            }
        }).start();
    }

    /**
     * Refreshes the file list and updates the view
     */
    private void refreshFileList() {
        try {
            List<String> files = model.refreshFileList();
            view.updateFileList(files);
        } catch (IOException e) {
            view.showError("Failed to refresh file list: " + e.getMessage());
        }
    }
}