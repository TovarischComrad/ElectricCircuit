package org.example;

public class GaussianElimination {
    public double[] solve(double[][] A, double[] B)
    {
        // Прямой ход
        int N = B.length;
        for (int k = 0; k < N; k++)
        {
            // Нахождение строки опорного элемента
            int max = k;
            for (int i = k + 1; i < N; i++)
                if (Math.abs(A[i][k]) > Math.abs(A[max][k]))
                    max = i;

            // Перестановка
            double[] temp = A[k];
            A[k] = A[max];
            A[max] = temp;
            double t = B[k];
            B[k] = B[max];
            B[max] = t;

            for (int i = k + 1; i < N; i++)
            {
                double factor = A[i][k] / A[k][k];
                B[i] -= factor * B[k];
                for (int j = k; j < N; j++)
                    A[i][j] -= factor * A[k][j];
            }
        }

        // Обратный ход
        double[] solution = new double[N];
        for (int i = N - 1; i >= 0; i--)
        {
            double sum = 0.0;
            for (int j = i + 1; j < N; j++)
                sum += A[i][j] * solution[j];
            if (A[i][i] != 0) {
                solution[i] = (B[i] - sum) / A[i][i];
            }
            else {
                solution[i] = 0;
            }
        }
        return solution;
    }
}
