package org.example;

import java.io.IOException;
import java.util.Arrays;

public class Test {
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
        Circuit circ = new Circuit("input.txt");

        double[] I = circ.I(0, 4, 10);
        double[] U = circ.U(0, 4, 10);
        System.out.println(Arrays.toString(U));

        double r = circ.R(0, 4);
        System.out.print("R = ");
        System.out.println(r);

        circ.Simulate(0, 4, 10);

        circ.SaveCircuit("input.txt");
    }
}
