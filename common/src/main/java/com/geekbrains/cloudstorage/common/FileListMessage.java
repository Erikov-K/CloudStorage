package com.geekbrains.cloudstorage.common;

import java.util.List;

public class FileListMessage extends AbstractMessage {
    private List<String> files;

    public FileListMessage(List<String> files) {
        this.files = files;
    }

    public List<String> getFiles() {
        return files;
    }

}
