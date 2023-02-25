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
import javafx.scene.control.Tooltip;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

public class Controller {
    static Circuit circuit;
    static int width;
    static int height;
    int margin = 50;
    static int cx;
    static int cy;
    int rad = 7;

    @FXML
    public Label om;
    @FXML
    public Label ampere;
    @FXML
    public TextField volt;
    @FXML
    public ListView<String> log;
    @FXML
    public Label r_inf;
    @FXML
    public Label i_inf;
    @FXML
    public Label u_inf;
    @FXML
    public TextField end;
    @FXML
    public TextField start;
    @FXML
    public TextField r;
    @FXML
    public TextField i;
    @FXML
    public TextField eps;


    // Вспомогательный класс для реализации перемещения объекта
    public class Mouse {
        static Pane pane;
        double orgSceneX, orgSceneY;
        double orgTranslateX, orgTranslateY;

        public void makeDraggable(Node node) {
            node.setOnMousePressed(circleOnMousePressedEventHandler);
            node.setOnMouseDragged(circleOnMouseDraggedEventHandler);
        }

        public void MoveLine(Circle c, int x, int y) {
            String c_id = c.getId();
            ObservableList<Node> nodes = pane.getChildren();
            for (int i = 0; i < nodes.size(); i++) {
                if (nodes.get(i) instanceof Line) {
                    Line line = (Line) pane.getChildren().get(i);
                    String l_id = line.getId();
                    String[] ids = l_id.split(" ");
                    if (c_id.equals(ids[1])) {
                        line.setStartX(x + c.getLayoutX());
                        line.setStartY(y + c.getLayoutY());
                    } else if (c_id.equals(ids[2])) {
                        line.setEndX(x + c.getLayoutX());
                        line.setEndY(y + c.getLayoutY());
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
                    MoveLine(p, (int) newTranslateX, (int) newTranslateY);
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
            circle.setCenterX(margin);
            circle.setCenterY(cy);
        }
        else if (i == circ.V - 1) {
            circle.setCenterX(width - margin);
            circle.setCenterY(cy);
        }
        else {
            int k = (width - 2 * margin) / (circ.V - 1);
            int ay = -margin * 2;
            int by = margin * 2;
            int x = margin + k * i;
            int y = (int) (Math.random() * (by - ay) + ay);
            circle.setCenterX(dx + x);
            circle.setCenterY(dy + y);
        }
    }

    // Инициализация круга, расчёт положения и назначение действия при нажатии
    public Circle drawCircle(int i, Circuit circ, Event event) {
        Circle circle = new Circle(rad);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.BLACK);
        Location(circle, i, circ, event);
        Mouse mg = new Mouse();
        mg.makeDraggable(circle);
        return circle;
    }

    public void DrawCircuit(Circuit circuit, Pane overlay, Event event) {
        // Инициализация узлов
        LinkedList<Circle> circles = new LinkedList<>();
        for (int i = 0; i < circuit.V; i++) {
            Circle circle = drawCircle(i, circuit, event);
            circle.setId(String.valueOf(i));
            Tooltip.install(circle, new Tooltip("Узел №" + i));
            circles.add(circle);
        }

        // Инициализация элементов (рёбер)
        Mouse mg = new Mouse();
        LinkedList<Line> lines = new LinkedList<>();
        for (int i = 0; i < circuit.V; i++) {
            LinkedList<Edge> edges = circuit.AdjList.get(i);
            for (Edge e : edges) {
                Line line = new Line();
                String id = e.Id + " " + i + " " + e.elementId;
                line.setId(id);
                line.setStrokeWidth(3.0);
                line.setStartX(circles.get(i).getCenterX());
                line.setStartY(circles.get(i).getCenterY());
                line.setEndX(circles.get(e.elementId).getCenterX());
                line.setEndY(circles.get(e.elementId).getCenterY());
                Tooltip.install(line, new Tooltip("Устройство №" + e.Id));
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
        start.setText("");
        end.setText("");
        r.setText("1");
        i.setText("1");
        eps.setText("0.5");
    }

    @FXML
    private void R() {
        if (circuit != null) {
            double r = circuit.R(0, circuit.V - 1);
            String R = String.format("%.2f", r);
            String s = "= " + R + " Ом";
            om.setText(s);
        }
    }

    @FXML
    private void U() {
        if (circuit != null) {
            double u;
            try {
                u = Double.parseDouble(volt.getText());
                if (u < 0) { throw new NumberFormatException(); }
            }
            catch (NumberFormatException e) {
                volt.setText("0");
                return;
            }

            double r = circuit.R(0, circuit.V - 1);
            double i = u / r;
            String num = String.format("%.2f", i);
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
        Circle circle = new Circle(rad);
        circle.setStroke(Color.BLACK);
        circle.setFill(Color.BLACK);
        Mouse mg = new Mouse();
        mg.makeDraggable(circle);
        circle.setId(String.valueOf(circuit.V - 1));

        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        Pane overlay = (Pane) scene.lookup("#overlay");

        circle.setCenterX(cx);
        circle.setCenterY(cy);
        Tooltip.install(circle, new Tooltip("Узел №" + (circuit.V - 1)));
        overlay.getChildren().addAll(circle);
    }

    @FXML
    private void AddEdge(ActionEvent event) {
        // Проверка ввода
        int st, en;
        double R, I, E;
        try {
            st = Integer.parseInt(start.getText());
            en = Integer.parseInt(end.getText());
            boolean fl = st == en || circuit == null
                    || st < 0 || st >= circuit.V || en < 0 || en >= circuit.V;
            if (fl) { throw new NumberFormatException(); }
            if (en < st) {
                int tmp = st;
                st = en;
                en = tmp;
            }
            LinkedList<Edge> lst = circuit.AdjList.get(st);
            for (Edge e : lst) {
                if (e.elementId == en) { throw new NumberFormatException(); }
            }

            R = Double.parseDouble(r.getText());
            I = Double.parseDouble(i.getText());
            E = Double.parseDouble(eps.getText());
            if (R < 0 || I < 0 || E < 0) { throw new NumberFormatException(); }
        }
        catch (NumberFormatException e) {
            start.setText("");
            end.setText("");
            r.setText("1");
            i.setText("1");
            eps.setText("0.5");
            return;
        }

        circuit.ConnectElement(st, en, new ElectricElement(R, I, E));
        Node node = (Node) event.getSource();
        Scene scene = node.getScene();
        Pane overlay = (Pane) scene.lookup("#overlay");

        Circle circle1 = (Circle) overlay.lookup("#" + st);
        Circle circle2 = (Circle) overlay.lookup("#" + en);

        Mouse mg = new Mouse();
        Line line = new Line();
        String id = circuit.E - 1 + " " + st + " " + en;
        line.setId(id);
        line.setStrokeWidth(3.0);
        line.setStartX(circle1.getCenterX());
        line.setStartY(circle1.getCenterY());
        line.setEndX(circle2.getCenterX());
        line.setEndY(circle2.getCenterY());
        Tooltip.install(line, new Tooltip("Устройство №" + (circuit.E - 1)));
        mg.makeDraggable(line);
        overlay.getChildren().addAll(line);

        start.setText("");
        end.setText("");
    }
}
