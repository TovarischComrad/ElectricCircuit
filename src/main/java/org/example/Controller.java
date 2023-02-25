package org.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;


import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

public class Controller {
    static Circuit circuit;
    public Label r_inf;
    public Label i_inf;
    public Label u_inf;
    public TextField end;
    public TextField start;
    int width;
    int height;
    int margin = 50;
    int cy = (int) (height / 2.0);

    @FXML
    public Label om;
    @FXML
    public Label ampere;
    @FXML
    public TextField volt;
    @FXML
    public ListView<String> log;


    // Вспомогательный класс для реализации перемещения объекта
    public class Mouse {
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
                }
                else if (t.getSource() instanceof Line) {
                    Line l = ((Line) (t.getSource()));
                    String id = l.getId();
                    double u = Double.parseDouble(volt.getText());
                    double[] lst = circuit.U(0, circuit.V - 1, u);
                    String[] idl = id.split(" ");
                    int k = Integer.parseInt(idl[0]);

                    double i = lst[k];
                    String I = String.format( "%.2f", i);
                    i_inf.setText("I = " + I + " А");

                    double r = 0;
                    int start = Integer.parseInt(idl[1]);
                    int end = Integer.parseInt(idl[2]);
                    LinkedList<Edge> lst2 = circuit.AdjList.get(start);
                    for (Edge edge : lst2) {
                        if (edge.elementId == end) {
                            r = edge.electricElement.R;
                            break;
                        }
                    }
                    String R = String.format( "%.2f", r);
                    r_inf.setText("R = " + R + " Ом");

                    u = i * r;
                    String U = String.format( "%.2f", u);
                    u_inf.setText("U = " + U + " В");
                }
                else {
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
                }
                else if (t.getSource() instanceof Line) {
                    //
                }
                else {
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

        Mouse mg = new Mouse();
        // Инициализация элементов (рёбер)
        LinkedList<Line> lines = new LinkedList<>();
        for (int i = 0; i < circuit.V; i++) {
            LinkedList<Edge> edges = circuit.AdjList.get(i);
            for (Edge e : edges) {
                Line line = new Line();
                String id = e.Id + " " + i + " " + e.elementId;
                line.setId(id);
                line.setStrokeWidth(3.0);
                line.setStartX(circles.get(i).getLayoutX());
                line.setStartY(circles.get(i).getLayoutY());
                line.setEndX(circles.get(e.elementId).getLayoutX());
                line.setEndY(circles.get(e.elementId).getLayoutY());
                mg.makeDraggable(line);
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

        om.setText("= ... Ом");
        ampere.setText("I = ... А");
        volt.setText("0");
        log.getItems().clear();
        circuit = null;
    }

    @FXML
    private void R() {
        if (circuit != null) {
            double r = circuit.R(0, circuit.V - 1);
            String s = "= " + r + " Ом";
            om.setText(s);
        }
    }

    @FXML
    private void U() {
        if (circuit != null) {
            double u = Double.parseDouble(volt.getText());
            double r = circuit.R(0, circuit.V - 1);
            double i = u / r;
            String num = String.format( "%.2f", i);
            String s = "I = " + num + " А";
            ampere.setText(s);
        }
    }

    @FXML
    private void Simulate() {
        double u = Double.parseDouble(volt.getText());
        double[] res = circuit.Simulate(0, circuit.V - 1, u);
        ObservableList<String> logs = FXCollections.observableArrayList();
        for (int i = 0; i < res.length; i++) {
            String s = "";
            if (res[i] == 0) {
                s = "Устройство " + i + " функционирует нормально";
            }
            else if (res[i] == 1) {
                s = "Устройство " + i + " перегорело";
            }
            else if (res[i] == -1) {
                s = "Устройству " + i + " не хватило тока";
            }
            logs.add(s);
        }
        log.setItems(logs);
    }

    @FXML
    private void Add(ActionEvent event) {
        if (circuit == null) {
            circuit = new Circuit();
        }

        circuit.AddElement();
        int i = circuit.V - 1;
        Circle circle = drawCircle(i, circuit, event);
        circle.setId(String.valueOf(i));

        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        Pane overlay = (Pane) scene.lookup("#overlay");
        overlay.getChildren().addAll(circle);
    }

    @FXML
    private void AddEdge(ActionEvent event) {
        int st = Integer.parseInt(start.getText());
        int en = Integer.parseInt(end.getText());
        circuit.ConnectElement(st, en, new ElectricElement());

        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        Pane overlay = (Pane) scene.lookup("#overlay");

        Circle circle1 = new Circle();
        Circle circle2 = new Circle();
        ObservableList<Node> lst = overlay.getChildren();
        for (Node value : lst) {
            if (Objects.equals(value.getId(), String.valueOf(st))) {
                circle1 = (Circle) value;
            }
            if (Objects.equals(value.getId(), String.valueOf(en))) {
                circle2 = (Circle) value;
            }
        }

        Mouse mg = new Mouse();
        Line line = new Line();
        String id = circuit.E - 1 + " " + st + " " + en;
        line.setId(id);
        line.setStrokeWidth(3.0);
        line.setStartX(circle1.getLayoutX());
        line.setStartY(circle1.getLayoutY());
        line.setEndX(circle2.getLayoutX());
        line.setEndY(circle2.getLayoutY());
        mg.makeDraggable(line);
        overlay.getChildren().addAll(line);
    }
}
