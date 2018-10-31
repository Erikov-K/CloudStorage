package com.geekbrains.cloudstorage.common;

/**
 * This class implements FileRequest.
 * Uses for generate file request (by filename) from client to server.
 *
 * @author @FlameXander
 */
public class FileRequest extends AbstractMessage {
    /**
     * Local variable.
     */
    private String filename;

    /**
     * Getter.
     *
     * @return filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * FileRequest class constructor.
     *
     * @param filename filename
     */
    public FileRequest(final String filename) {
        this.filename = filename;
    }
}
