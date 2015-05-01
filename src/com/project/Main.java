package com.project;

import com.simplex.Simplex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {


        ArrayList<Double> costMatrix = new ArrayList<Double>(Arrays.asList(30.0, 40.0, 50.0));

        ArrayList<ArrayList<Double>> problemMatrix = new ArrayList<ArrayList<Double>>(Arrays.asList(
                new ArrayList<Double>(Arrays.asList(1.0, 0.0, 1.0)),
                new ArrayList<Double>(Arrays.asList(0.0, 1.0, 1.0)),
                new ArrayList<Double>(Arrays.asList(0.0, 1.0, 0.0))
        ));


	    Simplex exampleProblem = new Simplex(costMatrix, problemMatrix);

        exampleProblem.solve();

    }
}
