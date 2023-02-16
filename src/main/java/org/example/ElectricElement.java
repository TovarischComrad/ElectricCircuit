package org.example;

public class ElectricElement {

    double R;   // Сопротивление [Ом]
    double I;   // Расчитанная сила тока [А]
    double eps; // Допустимое отклонение

    public ElectricElement() {
        R = 1;
        I = 1;
        eps = 0.5;
    }

    public ElectricElement(double r) {
        R = r;
        I = 1;
        eps = 0.5;
    }

    public ElectricElement(double r, double i, double e) {
        R = r;
        I = i;
        eps = e;
    }
}


