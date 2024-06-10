package com.example.seventhassignmentsocketprogrammingfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 435, 655);
        stage.setTitle("Seventh Assignment");
        stage.setScene(scene);
        Image icon = new Image(getClass().getResourceAsStream("/3a5bfffc046bec6f5e8d0fdae2a89956.jpg"));
        stage.getIcons().add(icon);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}