package org.example;

import java.util.HashMap;
import java.util.LinkedList;


// TODO - разобраться с int и Integer
public class Circuit {

    static int MaxId = 0;

    // Вершина - соединяющий узел
    LinkedList<Integer> VertexList;
    // Ребро - электрический элемент
    HashMap<Integer, LinkedList<Edge>> AdjList;
    int V;
    int E;

    public Circuit() {
        V = 0;
        E = 0;
        VertexList = new LinkedList<>();
        AdjList = new HashMap<>();
    }

    public void AddElement() {
        V++;
        VertexList.add(MaxId);
        AdjList.put(MaxId, new LinkedList<>());
        MaxId++;
    }

    public void ConnectElement(Integer el1, Integer el2, ElectricElement el) {
        E++;
        Edge e = new Edge(el, el2);
        AdjList.get(el1).add(e);
    }

    // Нахождение дуг, ведущих в вершину id
    private LinkedList<Integer> To(int id) {
        LinkedList<Integer> res = new LinkedList<>();
        for (int i = 0; i < V; i++) {
            LinkedList<Edge> e = AdjList.get(VertexList.get(i));
            for (Edge edge : e) {
                if (edge.elementId == id) {
                    res.add(edge.Id);
                    break;
                }
            }
        }
        return res;
    }

    // Нахождение дуг, исходящих из вершины id
    private LinkedList<Integer> From(int id) {
        LinkedList<Integer> res = new LinkedList<>();
        LinkedList<Edge> e = AdjList.get(VertexList.get(id));
        for (Edge edge : e) {
            res.add(edge.Id);
        }
        return res;
    }

    // Расчёт сопротивления схемы
    public double R(int start, int end) {

        // Подготовка данных
        int N = V + E;
        double[] B = new double[N];
        double[][] A = new double[N][N];
        for (int i = 0; i < N; i++) {
            B[i] = 0;
            for (int j = 0; j < N; j++) {
                A[i][j] = 0;
            }
        }

        // Заполнение матрицы коэффициентов и свободных членов
        // Часть системы с уравнениями силы тока
        for (int i = 0; i < V; i++) {
            if (i == start) { B[i] = -1; }
            else {
                LinkedList<Integer> to = this.To(i);
                for (Integer j : to) { A[i][j] = 1; }
            }
            if (i == end) { B[i] = 1; }
            else {
                LinkedList<Integer> from = this.From(i);
                for (Integer j : from) { A[i][j] = -1; }
            }
        }

        // Часть системы с уравнениями потенциалов
        int k = 0;
        for (int i = 0; i < V; i++) {
            LinkedList<Edge> edges = AdjList.get(i);
            for (Edge edge : edges) {
                A[V + k][edge.Id] = edge.electricElement.R;
                // A[V + k][k] = edge.electricElement.R;
                if (edge.elementId != end) {
                    A[V + k][E + edge.elementId] = -1;
                }
                A[V + k][E + i] = 1;
                k++;
            }
        }

        GaussianElimination ge = new GaussianElimination();
        double[] res = ge.solve(A,B);

        return -res[E];
    }
}
