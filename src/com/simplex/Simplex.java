package com.simplex;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Simplex {

    ArrayList<Double> costMatrix;

    ArrayList<ArrayList<Double>> problemMatrix;


    public Simplex(ArrayList<Double> costMatrix, ArrayList<ArrayList<Double>> problemMatrix) {
        this.costMatrix = costMatrix;

        this.problemMatrix = problemMatrix;
    }

    public ArrayList<Double> solve(){
        ArrayList<Double> solution = new ArrayList<Double>();

        System.out.println("binary matrix\n\n");
        constructProblem(); // contruct problem binary matrix
        System.out.println("dual matrix\n\n");
        constructDualProblem();
        System.out.println("Add slack variables to matrix\n\n");
        addSlackVariables();
        System.out.println("reverse last line\n\n");
        //reverseLastLine();

        solution = matrixMethod();


        return solution;
    }

    private ArrayList<Double> matrixMethod() {

        for (int i = 0; i < problemMatrix.size(); i++){
            for (int j = 0; j < problemMatrix.get(i).size(); j++){
                System.out.printf("%-7.2f", problemMatrix.get(i).get(j));
            }
            System.out.println();
        }

        System.out.println("\n");
        System.out.println("\n");

        int column = chooseColumnPivot();
        Scanner sc = new Scanner(System.in);
        int line;

        while (column != -1){
            line = chooseLinePivot(column);

            System.out.println("Line: " + line);
            System.out.println("Column: " + column);

            if (line != -1) {
                constructNextStep(column, line);
                System.out.println("encontrou");
            }

            column = chooseColumnPivot();
            //sc.next();
        }


        return null;
    }

    private void constructNextStep(int column, int line) {
        ArrayList<ArrayList<Double>> nextMatrix = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> pivotLine = new ArrayList<Double>();

        ArrayList<Double> auxiliarLine;
        ArrayList<Double> matrixLine = problemMatrix.get(line);

        double pivotValue = matrixLine.get(column);

        double quotient; // Value for division
        double newValue;

        for (int i = 0; i < matrixLine.size(); i++) {
            pivotLine.add(i,(matrixLine.get(i)/pivotValue));
        }


        for (int i = 0; i < problemMatrix.size(); i++) {
            if (i != line) {
                quotient = problemMatrix.get(i).get(column);
                auxiliarLine = new ArrayList<Double>();
                for (int j = 0; j < problemMatrix.get(i).size(); j++) {
                    newValue = problemMatrix.get(i).get(j) - (quotient * pivotLine.get(j));
                    auxiliarLine.add(j, newValue);
                }
                nextMatrix.add(i, auxiliarLine);
            }
            else
                nextMatrix.add(line, pivotLine);
        }


        for (int i = 0; i < nextMatrix.size(); i++){
            for (int j = 0; j < nextMatrix.get(i).size(); j++){
                System.out.printf("%-7.2f", nextMatrix.get(i).get(j));
            }
            System.out.println();
        }

        System.out.println("\n");
        System.out.println("\n");

        problemMatrix = nextMatrix;
    }

    private int chooseLinePivot(int column) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        double quotient;
        int columnsNumber = problemMatrix.get(0).size();
        double lessValue = Double.POSITIVE_INFINITY;

        for (int i = 0; i < problemMatrix.size() - 1; i++) {
            if (problemMatrix.get(i).get(column) != 0) {
                quotient = (problemMatrix.get(i).get(columnsNumber - 1) / problemMatrix.get(i).get(column));
                System.out.println("quotient: " + quotient);
                if (quotient < lessValue && quotient >= 0) {
                    System.out.println("escolheu: " + quotient);
                    lessValue = quotient;
                    res.clear();
                    res.add(i);
                }
                else if (quotient == lessValue) {
                    res.add(i);
                }
            }
        }

        if (res.isEmpty()) {
            return -1;
        }
        else {
            Random rand = new Random();
            int randomNum = rand.nextInt(res.size());
            return res.get(randomNum);
        }
    }

    private int chooseColumnPivot() {
        int matrixLastLine = problemMatrix.size() - 1;
        double highValue = 0;
        ArrayList<Integer> res = new ArrayList<Integer>();

        for (int i = 0; i < problemMatrix.get(matrixLastLine).size() - 1; i++){
            double elem = problemMatrix.get(matrixLastLine).get(i);
            if (elem > highValue){
                highValue = elem;
                res.clear();
                res.add(i);
            }
            else if (elem == highValue && elem != 0) {
                res.add(i);
            }
        }

        if (res.isEmpty()) {
            return -1;
        }
        else {
            Random rand = new Random();
            int randomNum = rand.nextInt(res.size());
            return res.get(randomNum);
        }
    }

    private void reverseLastLine() {
        int matrixLastLine = problemMatrix.size() - 1;

        for (int i = 0; i < problemMatrix.get(matrixLastLine).size(); i++){
            double elem = problemMatrix.get(matrixLastLine).get(i);
            problemMatrix.get(matrixLastLine).set(i, -elem);
        }
    }

    private void addSlackVariables() {
        int nSlackVariable = problemMatrix.size() - 1;
        ArrayList<Double> auxiliarLine;

        for (int i = 0; i < problemMatrix.size(); i++){

            auxiliarLine = problemMatrix.get(i);
            double lastElem = auxiliarLine.get(auxiliarLine.size() - 1);
            auxiliarLine.remove(auxiliarLine.size() - 1 );

            for (int j = 0; j < nSlackVariable; j++){
                if (i == j)
                    auxiliarLine.add(1.0);
                else
                    auxiliarLine.add(0.0);
            }
            auxiliarLine.add(lastElem);

            problemMatrix.set(i, auxiliarLine);
        }

        for (int i = 0; i < problemMatrix.size(); i++){
            for (int j = 0; j < problemMatrix.get(i).size(); j++){
                System.out.print(problemMatrix.get(i).get(j) + " ");
            }
            System.out.println("");
        }
    }

    private void constructDualProblem() {
        ArrayList<ArrayList<Double>> auxiliarMatrix = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> auxiliarLine;
        int length = problemMatrix.get(0).size();

        for (int i = 0; i < length; i++){
            auxiliarLine = new ArrayList<Double>();
            for (int k = 0; k < problemMatrix.size(); k++) {
                auxiliarLine.add(problemMatrix.get(k).get(i));
            }
            auxiliarMatrix.add(auxiliarLine);
        }

        for (int i = 0; i < auxiliarMatrix.size(); i++){
            for (int j = 0; j < auxiliarMatrix.get(i).size(); j++){
                System.out.print(auxiliarMatrix.get(i).get(j) + " ");
            }
            System.out.println("");
        }

        problemMatrix = auxiliarMatrix;

    }

    private void constructProblem() {

        ArrayList<ArrayList<Double>> auxiliarMatrix = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> reverseLine;
        ArrayList<Double> auxiliarLine;

        for (int i = 0; i < problemMatrix.size(); i++){
            problemMatrix.get(i).add(1.0);
            reverseLine = new ArrayList<Double>();
            auxiliarLine = new ArrayList<Double>();
            for (int j = 0; j < problemMatrix.get(i).size(); j++){
                reverseLine.add(-problemMatrix.get(i).get(j));
                if(j == i || j == problemMatrix.get(i).size() - 1 )
                    auxiliarLine.add(-1.0);
                else
                    auxiliarLine.add(0.0);
            }
            auxiliarMatrix.add(reverseLine);
            auxiliarMatrix.add(auxiliarLine);
        }
        problemMatrix.addAll(auxiliarMatrix);
        costMatrix.add(0.0);

        problemMatrix.add(costMatrix);

        for (int i = 0; i < problemMatrix.size(); i++){
            for (int j = 0; j < problemMatrix.get(i).size(); j++){
                System.out.print(problemMatrix.get(i).get(j) + " ");
            }
            System.out.println("");
        }
    }

}
