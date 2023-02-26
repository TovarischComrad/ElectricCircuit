package org.example;

public class Edge {

    static int MaxId = 0;

    final int Id; // номер дуги
    final ElectricElement electricElement;
    final int elementId; // узел, куда направлена дуга

    public Edge(ElectricElement el, int elementId) {
        this.elementId = elementId;
        Id = MaxId;
        MaxId++;
        electricElement = el;
    }

}
