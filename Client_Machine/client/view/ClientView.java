package client.view;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * ClientView: The View in MVC architecture.
 * Manages the UI elements and transitions between screens.
 * Contains only Swing components and event triggers - no business logic.
 */
public class ClientView extends JFrame {
    // UI Components
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Connection Screen Components
    private JTextField ipField;
    private JTextField portField;
    private JButton connectButton;
    
    // Room Screen Components
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
     * Creates the main application view
     */
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

    /**
     * Creates the connection panel UI
     * @return configured connection panel
     */
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

    /**
     * Creates the room panel UI
     * @return configured room panel
     */
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

        return panel;
    }

    // Screen navigation methods
    public void showConnectionScreen() {
        cardLayout.show(mainPanel, "CONNECTION");
    }

    public void showRoomScreen() {
        cardLayout.show(mainPanel, "ROOM");
    }

    // UI update methods
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

    public void setConnectButtonEnabled(boolean enabled) {
        connectButton.setEnabled(enabled);
    }

    // Data retrieval methods
    public String getIp() {
        return ipField.getText().trim();
    }

    public String getPort() {
        return portField.getText().trim();
    }

    public String getSelectedFile() {
        return fileList.getSelectedValue();
    }

    public boolean isHostMode() {
        return destroyButton.isEnabled();
    }

    // Dialog methods
    public String promptInput(String message) {
        return JOptionPane.showInputDialog(this, message, "Input", JOptionPane.QUESTION_MESSAGE);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Event listener registration methods
    public void setConnectListener(Runnable listener) {
        connectButton.addActionListener(e -> listener.run());
    }

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