package client.util;

import javax.swing.*;

/**
 * ErrorDialog: Centralized error handling utility
 * Provides consistent error dialogs throughout the application
 */
public class ErrorDialog {
    
    /**
     * Shows an error dialog with the specified message
     * @param parent the parent component
     * @param message the error message
     */
    public static void showError(JComponent parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows an error dialog with the specified message and title
     * @param parent the parent component
     * @param title the dialog title
     * @param message the error message
     */
    public static void showError(JComponent parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows an error dialog for network-related errors
     * @param parent the parent component
     * @param operation the operation that failed
     * @param error the error message
     */
    public static void showNetworkError(JComponent parent, String operation, String error) {
        showError(parent, "Network Error", operation + " failed: " + error);
    }
    
    /**
     * Shows an error dialog for file operation errors
     * @param parent the parent component
     * @param operation the file operation that failed
     * @param error the error message
     */
    public static void showFileError(JComponent parent, String operation, String error) {
        showError(parent, "File Error", operation + " failed: " + error);
    }
    
    /**
     * Shows an error dialog for validation errors
     * @param parent the parent component
     * @param fieldName the field that failed validation
     * @param message the validation error message
     */
    public static void showValidationError(JComponent parent, String fieldName, String message) {
        showError(parent, "Validation Error", fieldName + ": " + message);
    }
}