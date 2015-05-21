package com.methods;

import com.project.Main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class Chromosome implements Comparable{

    final long PENALTY = 2000;


    private ArrayList<Integer> genes;
    private double fit;
    private double cost;

    public Chromosome(ArrayList<Integer> genes){
        this.genes = genes;
    }

    public Chromosome() {
        this.cost = 0;
    }

    public void setGenes(ArrayList<Integer> genes){
        this.genes = genes;
    }

    public double getFit(){
        return fit;
    }

    public void setFit(double fit){
        this.fit = fit;
    }

    public ArrayList<Integer> getGenes(){
        return genes;
    }

    public double zeroProbability(){
        double probability = 0.0;
        double zeroCounter = 0.0;

        for(int i = 0; i < genes.size(); i++){
            if(genes.get(i) == 0){
                zeroCounter += 1.0;
            }
        }

        if(zeroCounter != 0){
            probability = zeroCounter/(double)genes.size();
        }

        return probability;
    }

    @Override
    public int compareTo(Object o) {
        double othersFit = ((Chromosome)o).getFit();
        return Double.compare(this.fit, othersFit);
    }

    public double getCost(){
        double cost = 0.0;
        for(int i = 0; i < genes.size(); i++){
            if(genes.get(i) == 1) {
                cost += Main.pairingsList.get(i).getCost();
            }
        }

        this.cost = cost;

        return cost;
    }


    public void fit(ArrayList<ArrayList<Double>> problemMatrix) {
        double fitness = 0.0;

        for(int i = 0; i < genes.size(); i++){
            fitness += Main.pairingsList.get(i).getCost()*genes.get(i);
        }

        fitness += (PENALTY*noDeadHeadedFlights(problemMatrix));

        fit = fitness;
    }

    public int noDeadHeadedFlights(ArrayList<ArrayList<Double>> problemMatrix){
        int deadHeadedCounter = 0;
        int flightCounter = 0;

        for(int i = 0; i < problemMatrix.size(); i++){
            flightCounter = 0;
            for(int j = 0; j < problemMatrix.get(i).size(); j++){
                if(Math.round(problemMatrix.get(i).get(j)) == 1 && genes.get(j) == 1){
                    ++flightCounter;
                }
            }
            if(flightCounter > 1 || flightCounter == 0){
                deadHeadedCounter++;
            }
        }

        return deadHeadedCounter;
    }

}