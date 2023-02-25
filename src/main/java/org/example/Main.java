package org.example;


import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = FXMLLoader
                .load(Objects.requireNonNull(getClass().getClassLoader().getResource("style.fxml")));
        stage.setTitle("Electric Circuit");
        stage.setScene(scene);
        stage.show();
        Pane overlay = (Pane) scene.lookup("#overlay");
        Controller.Mouse.pane = overlay;
        Controller.width = (int) overlay.getWidth();
        Controller.height = (int) overlay.getHeight();
        Controller.cx = Controller.width / 2;
        Controller.cy = Controller.height / 2;
    }

    public static void main(String[] args) { launch(args); }
}