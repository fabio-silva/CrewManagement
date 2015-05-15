package com.methods;

import com.project.Main;

import java.util.ArrayList;

public class GeneticAlgorithm extends Method{

    public GeneticAlgorithm(ArrayList<Double> costMatrix, ArrayList<ArrayList<Double>> problemMatrix){
        super(costMatrix, problemMatrix);
    }

    public void correctChromosome(ArrayList<Double> chromosome){
        boolean allFlightsCovered = true;

        do {
            allFlightsCovered = true;
            for (int i = 0; i < problemMatrix.size(); i++) {
                int flightCounter = 0;

                for (int j = 0; j < chromosome.size(); j++) {
                    if (chromosome.get(j).equals(1.0) && problemMatrix.get(i).get(j).equals(1.0)) {
                        flightCounter++;
                    }
                }

                if (flightCounter != 1) { //Flight not covered exactly once
                    allFlightsCovered = false;
                    coverFlight(i, chromosome);
                }
            }
        }while(!allFlightsCovered);
    }

    public void coverFlight(int flightCounter, ArrayList<Double> chromosome){
        double minCost = Double.POSITIVE_INFINITY;
        int chosenPairing = -1;

        for(int i = 0; i < Main.pairingsList.size(); i++){
            if(Main.pairingsList.get(i).hasFlight(flightCounter) && Main.pairingsList.get(i).getCost() < minCost){
                minCost = Main.pairingsList.get(i).getCost();
                chosenPairing = i;
            }
        }

        if(chosenPairing != -1){
            chromosome.set(chosenPairing, 1.0);
        }
    }
    public ArrayList<Double> solve(){
        return null;
    }
}
