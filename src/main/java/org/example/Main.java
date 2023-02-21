package org.example;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;


public class Main extends Application {
    public static void CircTest() {
        Circuit circ = new Circuit();
        for (int i = 0; i < 4; i++) {
            circ.AddElement();
        }
        ElectricElement R1 = new ElectricElement(1);
        ElectricElement R2 = new ElectricElement(2);
        ElectricElement R3 = new ElectricElement(3);
        ElectricElement R4 = new ElectricElement(4);
        ElectricElement R5 = new ElectricElement(5);

        circ.ConnectElement(0, 1, R1);
        circ.ConnectElement(0, 2, R2);
        circ.ConnectElement(1, 2, R3);
        circ.ConnectElement(1, 3, R4);
        circ.ConnectElement(2, 3, R5);

        double r = circ.R(0, 1);
        System.out.print("R = ");
        System.out.println(r);
    }

    public static void CircTest2() {
        Circuit circ = new Circuit();
        for (int i = 0; i < 5; i++) {
            circ.AddElement();
        }
        ElectricElement R1 = new ElectricElement(1);
        ElectricElement R2 = new ElectricElement(2);
        ElectricElement R3 = new ElectricElement(3);
        ElectricElement R4 = new ElectricElement(4);
        ElectricElement R5 = new ElectricElement(5);

        circ.ConnectElement(0, 1, R1);
        circ.ConnectElement(1, 2, R3);
        circ.ConnectElement(2, 4, R5);
        circ.ConnectElement(0, 3, R2);
        circ.ConnectElement(3, 4, R4);

        double[] I = circ.I(0, 4, 10);
        double[] U = circ.U(0, 4, 10);
        System.out.println(Arrays.toString(U));

        double r = circ.R(0, 4);
        System.out.print("R = ");
        System.out.println(r);

        circ.Simulate(0, 4, 10);
    }

    public static void CircTest3() throws IOException {
        // Circuit circ = new Circuit("src/main/resources/input.txt");
        Circuit circ = new Circuit("output.txt");

        double[] I = circ.I(0, 4, 10);
        double[] U = circ.U(0, 4, 10);
        System.out.println(Arrays.toString(U));

        double r = circ.R(0, 4);
        System.out.print("R = ");
        System.out.println(r);

        circ.Simulate(0, 4, 10);

        circ.SaveCircuit("output.txt");
    }

    public static void Gauss() {
        Scanner scan = new Scanner(System.in);
        System.out.println("Gaussian Elimination Algorithm Test\n");
        /** Make an object of GaussianElimination class **/
        GaussianElimination ge = new GaussianElimination();

        System.out.println("\nEnter number of variables");
        int N = scan.nextInt();

        double[] B = new double[N];
        double[][] A = new double[N][N];

        System.out.println("\nEnter "+ N +" equations coefficients ");
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                A[i][j] = scan.nextDouble();

        System.out.println("\nEnter "+ N +" solutions");
        for (int i = 0; i < N; i++)
            B[i] = scan.nextDouble();

        ge.solve(A,B);
    }


    int width = 800;
    int height = 600;
    int margin = 50;
    int cx = (int) (width / 2.0);
    int cy = (int) (height / 2.0);

    public static void main(String[] args) throws IOException {
        launch(args);
    }

    private void drawShapes(GraphicsContext gc) {
        gc.setStroke(Color.RED);
        gc.strokeRoundRect(10, 10, 230, 230, 10, 10);
    }

    public static class MouseGestures {

        double orgSceneX, orgSceneY;
        double orgTranslateX, orgTranslateY;

        public void makeDraggable(Node node) {
            node.setOnMousePressed(circleOnMousePressedEventHandler);
            node.setOnMouseDragged(circleOnMouseDraggedEventHandler);
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

                } else {
                    Node p = ((Node) (t.getSource()));
                    p.setTranslateX(newTranslateX);
                    p.setTranslateY(newTranslateY);
                }
            }
        };
    }


    public Circle drawCircle(int i, Circuit circ) {
        Circle circle = new Circle(10);
        circle.setStroke(Color.GREEN);
        circle.setFill(Color.GREEN);
        if (i == 0) {
            circle.relocate(margin, height / 2.0);
        }
        else if (i == circ.V - 1) {
            circle.relocate(width - margin, height / 2.0);
        }
        else {
            int ay = cy - margin * 2;
            int by = cy + margin * 2;
            int ax = cx - margin * 5;
            int bx = cx + margin * 5;
            int x = (int) (Math.random() * (bx - ax) + ax);
            int y = (int) (Math.random() * (by - ay) + ay);
            circle.relocate(x, y);
        }
        circle.setOnMousePressed(event -> {
            if (circle.getFill() == Color.GREEN) {
                circle.setFill(Color.RED);
            }
            else {
                circle.setFill(Color.GREEN);
            }
        });
        return circle;
    }
    public void my_test(Stage stage) throws IOException {


        Group root = new Group();
        Circuit circ = new Circuit("output.txt");
        LinkedList<Circle> circles = new LinkedList<>();
        for (int i = 0; i < circ.V; i++) {
            Circle circle = drawCircle(i, circ);
            circles.add(circle);
        }


        Pane overlay = new Pane();
        overlay.getChildren().addAll(circles);

        root.getChildren().addAll(overlay);

        stage.setScene(new Scene(root, width, height));
        stage.show();
    }

    @Override
    public void start(Stage stage) throws IOException {
        my_test(stage);
    }
}