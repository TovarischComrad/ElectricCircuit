package org.example;


import java.awt.*;
import java.io.IOException;
import java.util.LinkedList;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.event.ActionEvent;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


public class Main extends Application {
    Circuit circuit;
    int width = 800;
    int height = 600;
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

        public void MoveLine(MouseEvent t, Circle c) {
            String c_id = c.getId();
            ObservableList<Node> nodes = pane.getChildren();
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i) instanceof Line) {
                    Line line = (Line) pane.getChildren().get(i);
                    String l_id = line.getId();
                    String[] ids = l_id.split(" ");
                    if (c_id.equals(ids[1])) {
                        line.setStartX(t.getSceneX());
                        line.setStartY(t.getSceneY());
                    } else if (c_id.equals(ids[2])) {
                        line.setEndX(t.getSceneX());
                        line.setEndY(t.getSceneY());
                    }
                }
            }
        }

        EventHandler<MouseEvent> circleOnMousePressedEventHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                orgSceneX = t.getSceneX();
                orgSceneY = t.getSceneY();
                if (t.getSource() instanceof Circle) {
                    Circle p = ((Circle) (t.getSource()));
                    orgTranslateX = p.getCenterX();
                    orgTranslateY = p.getCenterY();

                    // доп действие
                    if (p.getFill() == Color.GREEN) {
                        p.setFill(Color.RED);
                    }
                    else {
                        p.setFill(Color.GREEN);
                    }
                } else {
                    Node p = ((Node) (t.getSource()));
                    orgTranslateX = p.getTranslateX();
                    orgTranslateY = p.getTranslateY();
                }
            }
        };

        EventHandler<MouseEvent> circleOnMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
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
                    MoveLine(t, p);
                } else {
                    Node p = ((Node) (t.getSource()));
                    p.setTranslateX(newTranslateX);
                    p.setTranslateY(newTranslateY);
                }
            }
        };
    }

    // Расчёт положения узла
    public void Location(Circle circle, int i, Circuit circ) {
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
            circle.relocate(x, y);
        }
    }

    // Инициализация круга, расчёт положения и назначение действия при нажатии
    public Circle drawCircle(int i, Circuit circ) {
        Circle circle = new Circle(10);
        circle.setStroke(Color.GREEN);
        circle.setFill(Color.GREEN);
        Location(circle, i, circ);
        Mouse mg = new Mouse();
        mg.makeDraggable(circle);
        return circle;
    }

    // Заполнение сцены
    @Override
    public void start(Stage stage) throws IOException {
        // Электрическая схема
        circuit = new Circuit("output.txt");

        // Инициализация узлов
        LinkedList<Circle> circles = new LinkedList<>();
        for (int i = 0; i < circuit.V; i++) {
            Circle circle = drawCircle(i, circuit);
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


        FlowPane flowPane = new FlowPane();
        Label lbl = new Label("Здесь будет сопротивление");
        Button btn = new Button("R");
        btn.setPrefWidth(80);
        btn.setOnAction(event -> {
            double R = circuit.R(0, circuit.V - 1);
            lbl.setText(String.valueOf(R));
        });
        flowPane.getChildren().addAll(btn, lbl);

        Pane overlay = new Pane();
        overlay.getChildren().addAll(lines);
        overlay.getChildren().addAll(circles);
        Mouse.pane = overlay;

        Group root = new Group();
        root.getChildren().addAll(overlay, flowPane);
        stage.setScene(new Scene(root, width, height));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}