package com.geekbrains.cloudstorage.client;

import com.geekbrains.cloudstorage.common.AbstractMessage;
import com.geekbrains.cloudstorage.common.FileListMessage;
import com.geekbrains.cloudstorage.common.FileRequest;
import com.geekbrains.cloudstorage.common.FileDeleteRequest;
import com.geekbrains.cloudstorage.common.FileMessage;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

/**
 * This class implements MainController.
 * Uses for define JavaFX client window logic.
 *
 * @author @FlameXander
 */
public class MainController extends Window implements Initializable {

    /**
     * Bind JavaFX ListView variable.
     */
    @FXML
    private ListView<String> serverFilesList;

    /**
     * Method to initialize client window.
     *
     * @param location  URL
     * @param resources ResourceBundle
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        Network.start();
        initializeDragAndDrop();
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    /*
                    'while' statement cannot complete without throwing an exception less... (Ctrl+F1)
                    Inspection info: Reports for, while, or do statements which can only exit by throwing an exception.
                    While such statements may be correct, they are often a symptom of coding errors.
                    */
                    AbstractMessage am = Network.readObject();

                    if (am instanceof FileMessage) {
                        // Receiving file from server
                        FileMessage fm = (FileMessage) am;
                        downloadFile(fm);
                    }

                    // Receive list of client files from server
                    if (am instanceof FileListMessage) {
                        FileListMessage flm = (FileListMessage) am;
                        refreshRemoteFilesList(flm);
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    /**
     * Method run when 'Download' button pressed.
     */
    public void pressOnDownloadBtn() {
        Network.sendMsg(
                new FileRequest(
                        serverFilesList.getSelectionModel().getSelectedItem()));
    }

    /**
     * Method run when 'Delete' button pressed.
     */
    public void pressOnDeleteBtn() {
        Network.sendMsg(
                new FileDeleteRequest(
                        serverFilesList.getSelectionModel().getSelectedItem()));
    }

    /**
     * Method run when 'Upload' button pressed.
     *
     * @throws IOException if there is an issue.
     */
    public void pressOnUploadBtn() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(this);
        uploadFile(file);
    }


    /**
     * Method downloading file from server.
     * Method run after 'Download' button pressed.
     *
     * @param fm FileMessage
     */
    private void downloadFile(final FileMessage fm) {
        if (Platform.isFxApplicationThread()) {
            saveFile(fm);
        } else {
            Platform.runLater(() -> saveFile(fm));
        }
    }

    /**
     * Method for saving downloaded file from server.
     * Uses FileChooser window to specify destination location.
     *
     * @param fm FileMessage
     */
    private void saveFile(final FileMessage fm) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(fm.getFilename());
        FileChooser.ExtensionFilter extFilter;
        extFilter = new FileChooser.ExtensionFilter("Any files (*.*)", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);
        File dest = fileChooser.showSaveDialog(this);
        if (dest != null) {
            try {
                Files.write(
                        Paths.get(
                                String.valueOf(dest)),
                        fm.getData(),
                        StandardOpenOption.CREATE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Method for draw list of client files
     * on server-side in JavaFX client window.
     *
     * @param flm FileListMessage
     */
    private void refreshRemoteFilesList(final FileListMessage flm) {
        if (Platform.isFxApplicationThread()) {
            serverFilesList.getItems().clear();
            flm.getFiles().forEach(s -> serverFilesList.getItems().add(s));
        } else {
            Platform.runLater(() -> {
                serverFilesList.getItems().clear();
                flm.getFiles().forEach(s -> serverFilesList.getItems().add(s));
            });
        }
    }

    /**
     * Method uploading file to server.
     * Method run after 'Upload' button pressed.
     *
     * @param file File
     * @throws IOException if there is an issue.
     */
    private void uploadFile(final File file) throws IOException {
        if (file != null) {
            Network.sendMsg(new FileMessage(Paths.get(file.getAbsolutePath())));
        }
    }

    /**
     * Method initialize Drag and Drop function.
     * Method uploading file to server.
     * Using as alternative to 'Upload' button.
     */
    private void initializeDragAndDrop() {
        serverFilesList.setOnDragOver(event -> {
            if (event.getGestureSource() != serverFilesList
                    && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        serverFilesList.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                for (File o : db.getFiles()) {
                    try {
                        uploadFile(o.getAbsoluteFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
}
