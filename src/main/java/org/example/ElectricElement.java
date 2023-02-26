package org.example;

public class ElectricElement {

    final double R;   // Сопротивление [Ом]
    final double I;   // Расчитанная сила тока [А]
    final double eps; // Допустимое отклонение

    public ElectricElement(double r, double i, double e) {
        R = r;
        I = i;
        eps = e;
    }
}


