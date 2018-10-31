package com.geekbrains.cloudstorage.common;

/**
 * This class implements FileDeleteRequest.
 * Uses for generate file delete request (by filename) from client to server.
 *
 * @author k.a.erikov@gmail.com
 */
public class FileDeleteRequest extends AbstractMessage {

    /**
     * Local variable.
     * 'String filename' contain name of file.
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
    public FileDeleteRequest(final String filename) {
        this.filename = filename;
    }
}
