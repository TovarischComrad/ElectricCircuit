package org.example;


import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = FXMLLoader
                .load(Objects.requireNonNull(getClass().getClassLoader().getResource("style.fxml")));
        stage.setTitle("Electric Circuit");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}