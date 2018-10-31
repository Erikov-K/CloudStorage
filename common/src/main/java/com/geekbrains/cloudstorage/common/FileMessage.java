package com.geekbrains.cloudstorage.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class implements FileMessage.
 * Uses for transfer file both sides.
 *
 * @author @FlameXander
 */
public class FileMessage extends AbstractMessage {

    /**
     * Local variable.
     * 'String filename' contain name of file.
     */
    private String filename;

    /**
     * Local variables.
     * 'byte[] data' contain file body (as byte array).
     */
    private byte[] data;

    /**
     * Getter.
     *
     * @return filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Getter.
     *
     * @return data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * FileRequest class constructor.
     *
     * @param path path
     * @throws IOException if there is an issue
     */
    public FileMessage(final Path path) throws IOException {
        filename = path.getFileName().toString();
        data = Files.readAllBytes(path);
    }
}
