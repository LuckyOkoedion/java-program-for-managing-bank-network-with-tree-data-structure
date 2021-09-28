package com.okoedion_lucky;

// Model of each bank node in the tree

public class BankNode {
    int parent;
    int id;
    double probability;

    public BankNode(int parent, int id, double probability) {
        this.parent = parent;
        this.id = id;
        this.probability = probability;
    }
}
