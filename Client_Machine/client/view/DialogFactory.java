package client.view;

import javax.swing.*;
import java.awt.*;

/**
 * DialogFactory: Utility class for creating consistent dialogs
 * Provides standardized dialog creation methods for the application
 */
public class DialogFactory {
    
    /**
     * Shows an input dialog with a prompt message
     * @param parent the parent component
     * @param message the prompt message
     * @return user input or null if cancelled
     */
    public static String showInputDialog(Component parent, String message) {
        return JOptionPane.showInputDialog(parent, message, "Input", JOptionPane.QUESTION_MESSAGE);
    }
    
    /**
     * Shows an information message dialog
     * @param parent the parent component
     * @param message the information message
     */
    public static void showMessageDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Shows an error message dialog
     * @param parent the parent component
     * @param message the error message
     */
    public static void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows a confirmation dialog
     * @param parent the parent component
     * @param message the confirmation message
     * @return true if user confirmed, false otherwise
     */
    public static boolean showConfirmDialog(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Confirm", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Shows a file chooser for selecting a file to open
     * @param parent the parent component
     * @return selected file path or null if cancelled
     */
    public static java.io.File showOpenFileDialog(Component parent) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }
    
    /**
     * Shows a file chooser for selecting a file to save
     * @param parent the parent component
     * @param suggestedFileName suggested file name
     * @return selected file path or null if cancelled
     */
    public static java.io.File showSaveFileDialog(Component parent, String suggestedFileName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File(suggestedFileName));
        if (chooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }
}