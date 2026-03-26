package server;

import java.io.*;
import java.nio.file.*;

/**
 * FileTransferHandler: streams file data to/from clients without loading whole files into memory.
 * Upload flow:
 * 1) Server sends "READY"
 * 2) Client sends filename (UTF) and size (long), then raw bytes of length size.
 * 3) Server writes the file to the room folder and replies "UPLOADED".
 *
 * Download flow:
 * 1) Client sends "DOWNLOAD <filename>"
 * 2) Server replies "FILE_SIZE <size>" then raw bytes, or "ERROR <msg>" if not found.
 */
public class FileTransferHandler {
    public long receiveUpload(Room room, String filename, long expectedSize, DataInputStream in) throws IOException {
        Path dest = room.folder.resolve(sanitize(filename));
        long totalReceived = 0;
        
        try (OutputStream os = new BufferedOutputStream(Files.newOutputStream(dest, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))) {
            byte[] buffer = new byte[8192];
            long remaining = expectedSize;
            
            while (remaining > 0) {
                int toRead = (int) Math.min(buffer.length, remaining);
                int read = in.read(buffer, 0, toRead);
                if (read == -1) throw new EOFException("Unexpected end of stream during upload");
                
                os.write(buffer, 0, read);
                totalReceived += read;
                remaining -= read;
            }
            os.flush();
        }
        
        return totalReceived;
    }

    public long sendDownload(Room room, String filename, DataOutputStream out) throws IOException {
        Path src = room.folder.resolve(sanitize(filename));
        if (!Files.exists(src) || !Files.isRegularFile(src)) {
            return -1L;
        }
        long size = Files.size(src);
        try (InputStream is = new BufferedInputStream(Files.newInputStream(src, StandardOpenOption.READ))) {
            byte[] buffer = new byte[8192];
            long remaining = size;
            while (remaining > 0) {
                int read = is.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                if (read == -1) break;
                out.write(buffer, 0, read);
                remaining -= read;
            }
            out.flush();
        }
        return size;
    }

    private String sanitize(String name) {
        String cleaned = name.replace("\\", "_").replace("/", "_");
        if (cleaned.trim().isEmpty()) cleaned = "file";
        return cleaned;
    }
}
