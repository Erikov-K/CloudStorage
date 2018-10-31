package com.geekbrains.cloudstorage.common;

import java.util.List;

/**
 * This class implements FileListMessage.
 * Uses for transfer the list of client files that stored on server.
 *
 * @author k.a.erikov@gmail.com
 */
public class FileListMessage extends AbstractMessage {
    /**
     * Local variable.
     * 'List<String> files' contain names of files.
     */
    private List<String> files;

    /**
     * Getter.
     *
     * @return files
     */
    public List<String> getFiles() {
        return files;
    }

    /**
     * FileListMessage class constructor.
     *
     * @param files files
     */
    public FileListMessage(final List<String> files) {
        this.files = files;
    }
}
