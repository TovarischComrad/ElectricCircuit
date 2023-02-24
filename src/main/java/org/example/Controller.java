package org.example;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class Controller {
    Circuit circuit;
    int width;
    int height;
    int margin = 50;
    int cy = (int) (height / 2.0);

    // Вспомогательный класс для реализации перемещения объекта
    public static class Mouse {
        static Pane pane;
        double orgSceneX, orgSceneY;
        double orgTranslateX, orgTranslateY;

        public void makeDraggable(Node node) {
            node.setOnMousePressed(circleOnMousePressedEventHandler);
            node.setOnMouseDragged(circleOnMouseDraggedEventHandler);
        }

        public void MoveLine(MouseEvent t, Circle c, int x, int y) {
            String c_id = c.getId();
            ObservableList<Node> nodes = pane.getChildren();
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i) instanceof Line) {
                    Line line = (Line) pane.getChildren().get(i);
                    String l_id = line.getId();
                    String[] ids = l_id.split(" ");
                    if (c_id.equals(ids[1])) {
                        line.setStartX(t.getSceneX() - x);
                        line.setStartY(t.getSceneY() - y);
                    } else if (c_id.equals(ids[2])) {
                        line.setEndX(t.getSceneX() - x);
                        line.setEndY(t.getSceneY() - y);
                    }
                }
            }
        }

        EventHandler<MouseEvent> circleOnMousePressedEventHandler = new EventHandler<>() {
            @Override
            public void handle(MouseEvent t) {
                orgSceneX = t.getSceneX();
                orgSceneY = t.getSceneY();
                if (t.getSource() instanceof Circle) {
                    Circle p = ((Circle) (t.getSource()));
                    orgTranslateX = p.getCenterX();
                    orgTranslateY = p.getCenterY();
                } else {
                    Node p = ((Node) (t.getSource()));
                    orgTranslateX = p.getTranslateX();
                    orgTranslateY = p.getTranslateY();
                }
            }
        };

        EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = new EventHandler<>() {
            @Override
            public void handle(MouseEvent t) {
                double offsetX = t.getSceneX() - orgSceneX;
                double offsetY = t.getSceneY() - orgSceneY;
                double newTranslateX = orgTranslateX + offsetX;
                double newTranslateY = orgTranslateY + offsetY;
                if (t.getSource() instanceof Circle) {
                    Circle p = ((Circle) (t.getSource()));
                    p.setCenterX(newTranslateX);
                    p.setCenterY(newTranslateY);
                    MoveLine(t, p, (int) pane.getLayoutX(), (int) pane.getLayoutY());
                } else {
                    Node p = ((Node) (t.getSource()));
                    p.setTranslateX(newTranslateX);
                    p.setTranslateY(newTranslateY);
                }
            }
        };
    }

    // Расчёт положения узла
    public void Location(Circle circle, int i, Circuit circ, Event event) {
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        Pane overlay = (Pane) scene.lookup("#overlay");
        int dx = (int) overlay.getLayoutX();
        int dy = (int) overlay.getLayoutY();

        // Расчёт положения вершин
        if (i == 0) {
            circle.relocate(margin, height / 2.0);
        }
        else if (i == circ.V - 1) {
            circle.relocate(width - margin, height / 2.0);
        }
        else {
            int k = (width - 2 * margin) / (circ.V - 1);
            int ay = cy - margin * 4;
            int by = cy + margin * 4;
            int x = margin + k * i;
            int y = (int) (Math.random() * (by - ay) + ay);
            circle.relocate(dx + x, dy + y);
        }
    }

    // Инициализация круга, расчёт положения и назначение действия при нажатии
    public Circle drawCircle(int i, Circuit circ, Event event) {
        Circle circle = new Circle(5);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.BLACK);
        Location(circle, i, circ, event);
        Mouse mg = new Mouse();
        mg.makeDraggable(circle);
        return circle;
    }

    public void DrawCircuit(Circuit circuit, Pane overlay, Event event) {
        width = (int) overlay.getWidth();
        height = (int) overlay.getHeight();

        // Инициализация узлов
        LinkedList<Circle> circles = new LinkedList<>();
        for (int i = 0; i < circuit.V; i++) {
            Circle circle = drawCircle(i, circuit, event);
            circle.setId(String.valueOf(i));
            circles.add(circle);
        }

        // Инициализация элементов (рёбер)
        LinkedList<Line> lines = new LinkedList<>();
        for (int i = 0; i < circuit.V; i++) {
            LinkedList<Edge> edges = circuit.AdjList.get(i);
            for (Edge e : edges) {
                Line line = new Line();
                String id = "l " + i + " " + e.elementId;
                line.setId(id);
                line.setStartX(circles.get(i).getLayoutX());
                line.setStartY(circles.get(i).getLayoutY());
                line.setEndX(circles.get(e.elementId).getLayoutX());
                line.setEndY(circles.get(e.elementId).getLayoutY());
                lines.add(line);
            }
        }
        overlay.getChildren().addAll(lines);
        overlay.getChildren().addAll(circles);
        Mouse.pane = overlay;
    }

    @FXML
    private void Open(ActionEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Scene scene = node.getScene();

        Clear(event);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("..\\ElectricCircuit"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            circuit = new Circuit(file.getName());
            Pane overlay = (Pane) scene.lookup("#overlay");
            DrawCircuit(circuit, overlay, event);
        }
    }

    @FXML
    private void Save(ActionEvent event) throws IOException {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("..\\ElectricCircuit"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            circuit.SaveCircuit(file.getName());
        }
    }

    @FXML
    private void Clear(ActionEvent event) {
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        Pane overlay = (Pane) scene.lookup("#overlay");
        overlay.getChildren().clear();
    }
}
