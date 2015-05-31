package com.methods;

import com.sun.tools.javac.util.ArrayUtils;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;

public class Simplex extends  Method{


    public Simplex(ArrayList<Double> costMatrix, ArrayList<ArrayList<Double>> problemMatrix) {
        super(costMatrix, problemMatrix);
    }

    public ArrayList<Double> solve(){

        System.out.println("binary matrix\n\n");
        constructProblem();
        System.out.println("dual matrix\n\n");
        constructDualProblem();
        System.out.println("Add slack variables to matrix\n\n");
        addSlackVariables();
        System.out.println("reverse last line\n\n");


        ArrayList<ArrayList<Double>> problemMatrixAux = new ArrayList<ArrayList<Double>>(problemMatrix);

        boolean solved = matrixMethod();
        while(!solved) {
            //Scanner s = new Scanner(System.in);
            //s.nextLine();
            problemMatrix = new ArrayList<ArrayList<Double>>(problemMatrixAux);
            solved = matrixMethod();
        }

        int lines = problemMatrix.size();
        int columns = problemMatrix.get(0).size();


        List solutionList = problemMatrix.get(lines - 1).subList(columns - lines ,columns);

        ArrayList<Double> solution = new ArrayList<Double>(solutionList);

        return solution;
    }

    private boolean matrixMethod() {
        HashMap<Integer, Integer> lineTimes = new HashMap<Integer, Integer>();

        int column = chooseColumnPivot();
        Scanner sc = new Scanner(System.in);
        int line;

        while (column != -1){
            line = chooseLinePivot(column);

            if (lineTimes.containsKey(line)) {
                int times = lineTimes.get(line);
                ++times;
                if (times > 30) {
                    return false;
                }
                lineTimes.put(line, times);
            }
            else {
                lineTimes.put(line, 1);
            }

            System.out.println("Line: " + line);
            System.out.println("Column: " + column);

            if (line != -1) {
                constructNextStep(column, line);
                System.out.println("encontrou");
            }
            column = chooseColumnPivot();
            //sc.next();
        }

        System.out.println("FINAL");
        return true;
    }

    private void constructNextStep(int column, int line) {
        ArrayList<ArrayList<Double>> nextMatrix = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> pivotLine = new ArrayList<Double>();
        ArrayList<Double> auxiliarLine;
        ArrayList<Double> matrixLine = problemMatrix.get(line);
        double pivotValue = matrixLine.get(column);
        double quotient;
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
                if (quotient < lessValue && quotient >= 0) {
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
    }

}
