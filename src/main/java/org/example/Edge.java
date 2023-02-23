package org.example;

public class Edge {

    static int MaxId = 0;

    int Id; // номер дуги
    ElectricElement electricElement;
    double w;
    int elementId; // узел, куда направлена дуга

    public Edge(ElectricElement el, int elementId) {
        this.elementId = elementId;
        Id = MaxId;
        MaxId++;
        electricElement = el;
        w = 0;
    }

}
