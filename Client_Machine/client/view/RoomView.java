package client.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * RoomView: Room management screen UI component
 * Handles room operations and file list display
 */
public class RoomView extends JPanel {
    private JLabel roomLabel;
    private DefaultListModel<String> fileListModel;
    private JList<String> fileList;
    private JButton createRoomButton;
    private JButton joinRoomButton;
    private JButton uploadButton;
    private JButton downloadButton;
    private JButton leaveButton;
    private JButton destroyButton;
    private JButton refreshButton;

    /**
     * Creates the room view
     */
    public RoomView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel: Room info and actions
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        roomLabel = new JLabel("Not in a room");
        createRoomButton = new JButton("Create Room");
        joinRoomButton = new JButton("Join Room");
        topPanel.add(roomLabel);
        topPanel.add(createRoomButton);
        topPanel.add(joinRoomButton);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: File list
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        add(new JScrollPane(fileList), BorderLayout.CENTER);

        // Bottom Panel: File actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        refreshButton = new JButton("Refresh");
        uploadButton = new JButton("Upload");
        downloadButton = new JButton("Download");
        leaveButton = new JButton("Leave Room");
        destroyButton = new JButton("Destroy Room");
        
        bottomPanel.add(refreshButton);
        bottomPanel.add(uploadButton);
        bottomPanel.add(downloadButton);
        bottomPanel.add(leaveButton);
        bottomPanel.add(destroyButton);
        
        add(bottomPanel, BorderLayout.SOUTH);

        // Initial state
        setRoomActionsEnabled(false);
    }

    public void setRoomLabel(String text) {
        roomLabel.setText(text);
    }

    public void updateFileList(List<String> files) {
        fileListModel.clear();
        if (files != null) {
            for (String file : files) {
                fileListModel.addElement(file);
            }
        }
    }

    public void setRoomActionsEnabled(boolean enabled) {
        uploadButton.setEnabled(enabled);
        downloadButton.setEnabled(enabled);
        leaveButton.setEnabled(enabled);
        destroyButton.setEnabled(enabled);
        refreshButton.setEnabled(enabled);
    }

    public void setHostMode(boolean isHost) {
        destroyButton.setEnabled(isHost);
    }

    public String getSelectedFile() {
        return fileList.getSelectedValue();
    }

    // Event listener registration methods
    public void setCreateRoomListener(Runnable listener) {
        createRoomButton.addActionListener(e -> listener.run());
    }

    public void setJoinRoomListener(Runnable listener) {
        joinRoomButton.addActionListener(e -> listener.run());
    }

    public void setUploadListener(Runnable listener) {
        uploadButton.addActionListener(e -> listener.run());
    }

    public void setDownloadListener(Runnable listener) {
        downloadButton.addActionListener(e -> listener.run());
    }

    public void setLeaveRoomListener(Runnable listener) {
        leaveButton.addActionListener(e -> listener.run());
    }

    public void setDestroyRoomListener(Runnable listener) {
        destroyButton.addActionListener(e -> listener.run());
    }

    public void setRefreshListener(Runnable listener) {
        refreshButton.addActionListener(e -> listener.run());
    }
}