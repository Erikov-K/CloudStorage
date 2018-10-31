package com.geekbrains.cloudstorage.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class implements MainClient.
 * Uses for JavaFX client window.
 *
 * @author @FlameXander
 */
public class MainClient extends Application {

    /**
     * Local variable 'String title'.
     * Define JavaFX window title.
     */
    private static final String TITLE = "CloudStorage Client";

    /**
     * Local variable 'String fxml'.
     * Define JavaFX fxml-file.
     */
    private static final String FXML = "main.fxml";

    /**
     * Method start.
     *
     * @param primaryStage Stage
     * @throws Exception if there is an issue.
     */
    @Override
    public void start(final Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/" + FXML));
        primaryStage.setTitle(TITLE);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main method, that launch client.
     *
     * @param args String[] of args
     */
    public static void main(final String[] args) {
        launch(args);
    }
}


