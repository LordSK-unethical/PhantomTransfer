package client;

import client.model.ClientModel;
import client.view.ClientView;
import client.controller.ClientController;
import javax.swing.*;

public class ClientLauncher {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(ClientLauncher::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        ClientModel model = new ClientModel();
        ClientView view = new ClientView();
        new ClientController(model, view);
        view.setVisible(true);
    }
}
