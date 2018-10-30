package com.geekbrains.cloudstorage.client;

import com.geekbrains.cloudstorage.common.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class MainController extends Window implements Initializable {
    @FXML
    TextField tfFileName;

//    @FXML
//    ListView<String> clientFilesList;

    @FXML
    ListView<String> serverFilesList;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();

                    if (am instanceof FileMessage) {
                        // скачивание файла с сервера
                        FileMessage fm = (FileMessage) am;
                        System.out.println(fm.getFilename());
                        Files.write(Paths.get("client_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
//                        refreshLocalFilesList();
                    }

                    // обновление списка файлов с сервера
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


//        clientFilesList.setItems(FXCollections.observableArrayList());
//        refreshLocalFilesList();
    }

    public void pressOnDownloadBtn(ActionEvent actionEvent) {
        if (tfFileName.getLength() > 0) {
            Network.sendMsg(new FileRequest(tfFileName.getText()));
            tfFileName.clear();
        }
    }
    /*
    public void refreshLocalFilesList() {
        if (Platform.isFxApplicationThread()) {
            try {
                clientFilesList.getItems().clear();
                Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> clientFilesList.getItems().add(o));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    clientFilesList.getItems().clear();
                    Files.list(Paths.get("client_storage")).map(p -> p.getFileName().toString()).forEach(o -> clientFilesList.getItems().add(o));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }*/

    // метод отрисовки файлов с сервера для JavaFX
    public void refreshRemoteFilesList(FileListMessage flm) {
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


    // Отправка файла на сервер
    public void pressOnUploadBtn(ActionEvent actionEvent) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(this);
        if (file != null) {
            Network.sendMsg(new FileMessage(Paths.get(file.getAbsolutePath())));
        }
    }

    /*
    // запрос на обновление списка файлов с сервера
    public void refreshRequestRemoteStorage(ActionEvent actionEvent) {
        Network.sendMsg(new FileListMessage());
    }
    */
}
