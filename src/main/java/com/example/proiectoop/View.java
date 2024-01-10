package com.example.proiectoop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class View extends Application {
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(View.class.getResource("scenaLabirint.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        Image image = new Image(View.class.getResourceAsStream("/com/example/proiectoop/fibonacci.jpg"));

        stage.setTitle("Joc labirint");
        stage.getIcons().add(image);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}