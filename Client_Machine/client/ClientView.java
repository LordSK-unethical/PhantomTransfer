package client;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * ClientView: The View in MVC architecture.
 * Manages the UI elements and transitions between screens.
 */
public class ClientView extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Connection Screen Elements
    private JTextField ipField;
    private JTextField portField;
    private JButton connectButton;

    // Room Screen Elements
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

    public ClientView() {
        setTitle("File Transfer MVC Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createConnectionPanel(), "CONNECTION");
        mainPanel.add(createRoomPanel(), "ROOM");

        add(mainPanel);
        showConnectionScreen();
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Server IP:"), gbc);
        gbc.gridx = 1;
        ipField = new JTextField("10.29.209.230", 15);
        panel.add(ipField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Server Port:"), gbc);
        gbc.gridx = 1;
        portField = new JTextField("5000", 15);
        panel.add(portField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        connectButton = new JButton("Connect to Server");
        panel.add(connectButton, gbc);

        return panel;
    }

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel: Room info and actions
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        roomLabel = new JLabel("Not in a room");
        createRoomButton = new JButton("Create Room");
        joinRoomButton = new JButton("Join Room");
        topPanel.add(roomLabel);
        topPanel.add(createRoomButton);
        topPanel.add(joinRoomButton);
        panel.add(topPanel, BorderLayout.NORTH);

        // Center Panel: File list
        fileListModel = new DefaultListModel<>();
        fileList = new JList<>(fileListModel);
        panel.add(new JScrollPane(fileList), BorderLayout.CENTER);

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
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Initially disable room-specific actions
        setRoomActionsEnabled(false);

        return panel;
    }

    public void setRoomActionsEnabled(boolean enabled) {
        uploadButton.setEnabled(enabled);
        downloadButton.setEnabled(enabled);
        leaveButton.setEnabled(enabled);
        destroyButton.setEnabled(enabled);
        refreshButton.setEnabled(enabled);
        createRoomButton.setEnabled(!enabled);
        joinRoomButton.setEnabled(!enabled);
    }

    public void updateFileList(List<String> files) {
        fileListModel.clear();
        if (files != null) {
            for (String f : files) fileListModel.addElement(f);
        }
    }

    public void setRoomLabel(String text) {
        roomLabel.setText(text);
    }

    public void showConnectionScreen() {
        cardLayout.show(mainPanel, "CONNECTION");
    }

    public void showRoomScreen() {
        cardLayout.show(mainPanel, "ROOM");
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public String promptInput(String msg) {
        return JOptionPane.showInputDialog(this, msg);
    }

    // Getters for Controller to attach listeners
    public String getIp() { return ipField.getText().trim(); }
    public String getPort() { return portField.getText().trim(); }
    public JButton getConnectButton() { return connectButton; }
    public JButton getCreateRoomButton() { return createRoomButton; }
    public JButton getJoinRoomButton() { return joinRoomButton; }
    public JButton getUploadButton() { return uploadButton; }
    public JButton getDownloadButton() { return downloadButton; }
    public JButton getLeaveButton() { return leaveButton; }
    public JButton getDestroyButton() { return destroyButton; }
    public JButton getRefreshButton() { return refreshButton; }
    public String getSelectedFile() { return fileList.getSelectedValue(); }
}
