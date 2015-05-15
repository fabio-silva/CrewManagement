package com.methods;


import java.util.ArrayList;

public abstract class Method {

    protected ArrayList<Double> costMatrix;

    protected ArrayList<ArrayList<Double>> problemMatrix;

    public Method(ArrayList<Double> costMatrix, ArrayList<ArrayList<Double>> problemMatrix){
        this.costMatrix = costMatrix;
        this.problemMatrix = problemMatrix;
    }

    public abstract ArrayList<Double> solve();
}
