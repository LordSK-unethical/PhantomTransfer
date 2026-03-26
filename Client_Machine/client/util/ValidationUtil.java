package client.util;

/**
 * ValidationUtil: Input validation utility methods
 * Provides common validation functions for user input
 */
public class ValidationUtil {
    
    /**
     * Validates an IP address format
     * @param ip the IP address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidIpAddress(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }
        
        // Simple IP validation - can be enhanced for production
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }
        
        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates a port number
     * @param port the port number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPort(int port) {
        return port > 0 && port <= 65535;
    }
    
    /**
     * Validates a port number from string input
     * @param portStr the port number as string
     * @return true if valid, false otherwise
     */
    public static boolean isValidPort(String portStr) {
        try {
            int port = Integer.parseInt(portStr);
            return isValidPort(port);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates a room code format
     * @param code the room code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidRoomCode(String code) {
        return code != null && code.matches("^[A-Z0-9]{6}$");
    }
    
    /**
     * Validates a filename
     * @param filename the filename to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidFilename(String filename) {
        return filename != null && !filename.trim().isEmpty() && 
               filename.matches("^[^\\/:*?\"<>|]+$");
    }
    
    /**
     * Validates that a string is not null or empty
     * @param input the string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }
}