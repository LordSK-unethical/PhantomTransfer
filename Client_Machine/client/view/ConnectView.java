package client.view;

import javax.swing.*;
import java.awt.*;

/**
 * ConnectView: Connection screen UI component
 * Handles server connection input fields and connection button
 */
public class ConnectView extends JPanel {
    private JTextField ipField;
    private JTextField portField;
    private JButton connectButton;

    /**
     * Creates the connection view
     */
    public ConnectView() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Server IP:"), gbc);
        gbc.gridx = 1;
        ipField = new JTextField("10.29.209.230", 15);
        add(ipField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Server Port:"), gbc);
        gbc.gridx = 1;
        portField = new JTextField("5000", 15);
        add(portField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        connectButton = new JButton("Connect to Server");
        add(connectButton, gbc);
    }

    public String getIp() {
        return ipField.getText().trim();
    }

    public String getPort() {
        return portField.getText().trim();
    }

    public void setConnectListener(Runnable listener) {
        connectButton.addActionListener(e -> listener.run());
    }

    public void setConnectButtonEnabled(boolean enabled) {
        connectButton.setEnabled(enabled);
    }
}