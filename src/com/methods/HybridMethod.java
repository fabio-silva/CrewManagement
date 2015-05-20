package com.methods;


import com.elements.Pairing;
import com.project.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HybridMethod extends Method{

    private ArrayList<Chromosome> population;
    private ArrayList<Pairing> selectedPairings;
    private Chromosome finalChromosome;

    public HybridMethod(ArrayList<Double> costMatrix, ArrayList<ArrayList<Double>> problemMatrix){

        super(costMatrix, problemMatrix);
        population = new ArrayList<Chromosome>();
        selectedPairings = new ArrayList<Pairing>(Main.pairingsList);
        finalChromosome = new Chromosome();
    }

    public void coverFlight(int flightIndex, Chromosome chromosome){
        double minCost = Double.POSITIVE_INFINITY;
        int chosenPairing = -1;

        for(int i = 0; i < selectedPairings.size(); i++){
            if(selectedPairings.get(i).hasFlight(flightIndex) && selectedPairings.get(i).getCost() < minCost){
                minCost = selectedPairings.get(i).getCost();
                chosenPairing = i;
            }
        }

        if(chosenPairing != -1){
            //System.out.println("COVERING");
            chromosome.getGenes().set(chosenPairing, 1);
        }
    }

    public void correctChromosome(Chromosome chromosome){
        boolean allFlightsCovered = true;

        do {
            allFlightsCovered = true;
            //System.out.println("DO");
            //System.out.println(chromosome.getGenes());
            for (int i = 0; i < problemMatrix.size(); i++) {
                int flightCounter = 0;

                for (int j = 0; j < chromosome.getGenes().size(); j++) {
                    if (chromosome.getGenes().get(j) == 1 && problemMatrix.get(i).get(j).intValue() == 1) {
                        flightCounter++;
                    }
                }

                if (flightCounter < 1) { //Flight not covered exactly once
                    allFlightsCovered = false;
                    coverFlight(i, chromosome);
                    // System.out.println(chromosome.getGenes());
                    //System.out.println("CORRECTiNG FLIGght " + i);
                    //System.exit(1);
                }
            }
        }while(!allFlightsCovered);

        //System.out.println("VALUE PAIRING: " + chromosome.getGenes());
    }

    public int allFlightsCovered(ArrayList<Integer>gene){
        int flightSize = problemMatrix.size();
        int coveredFlights = 0;

        for(int i = 0; i < selectedPairings.size(); i++){
            if(gene.get(i) == 1){
                coveredFlights += selectedPairings.get(i).getFlights().size();
            }
        }


        return flightSize - coveredFlights;
    }

    public void getFlightsForPairing(int pairingIndex, ArrayList<Integer> flights){
        for(int i = 0; i < problemMatrix.size(); i++){
            if(selectedPairings.get(pairingIndex).hasFlight(i)){
                flights.add(i);
            }
        }
    }

    public void getPairingsWithDifferentFlights(ArrayList<Integer> flights, ArrayList<Integer> pairings){
        boolean hasFlight;
        for(int i = 0; i < selectedPairings.size(); i++){
            hasFlight = false;
            for(int j = 0; j < flights.size(); j++){
                if(selectedPairings.get(i).hasFlight(flights.get(j))){
                    hasFlight = true;
                    break;
                }
            }
            if(hasFlight == false){
                pairings.add(i);
            }
        }
    }

    public Chromosome initialPopulation(){
        Random r = new Random();
        int firstGene = r.nextInt(selectedPairings.size());
        int nextGene;
        ArrayList<Integer> genes = new ArrayList<Integer>();
        ArrayList<Integer> usedPairings = new ArrayList<Integer>();
        ArrayList<Integer> selectedFlights = new ArrayList<Integer>();

        for(int i = 0; i < selectedPairings.size(); i++){
            if(i == firstGene) {
                genes.add(1);
                getFlightsForPairing(firstGene, usedPairings);
                getPairingsWithDifferentFlights(selectedFlights, usedPairings);
            } else{
                genes.add(0);
            }
        }

        while(usedPairings.size() > 0){
            nextGene = r.nextInt(usedPairings.size());
            genes.set(usedPairings.get(nextGene), 1);
            getFlightsForPairing(usedPairings.get(nextGene), selectedFlights);
            usedPairings.clear();
            getPairingsWithDifferentFlights(selectedFlights, usedPairings);
        }

        int coveredFlights = allFlightsCovered(genes);
        Chromosome c = new Chromosome(genes);
        //correctChromosome(c);
        //c.fit(problemMatrix);

        return c;
    }



    @Override
    public ArrayList<Double> solve() {

        auxiliarSolveFunction();

        System.out.println("FINAL chromosome: " + finalChromosome.getGenes() + "  -  " + finalChromosome.getCost());
        if(allFlightsCovered(finalChromosome.getGenes()) == 0){
            System.out.println("Valid Solution");
        }


        ArrayList<Double> solution = new ArrayList<Double>();

        for(int i = 0; i < finalChromosome.getGenes().size(); i++){
            solution.add(i, Double.valueOf(finalChromosome.getGenes().get(i)));
        }

        return solution;
    }

    public void auxiliarSolveFunction(Chromosome bestChromosome){

        int uncovered;
        int number = 0;
        int founded = 0;
        Chromosome temp;

        while(number < 15){
            temp = initialPopulation();
            uncovered = allFlightsCovered(temp.getGenes());
            if(uncovered == 0) {
                System.out.println("iteracao");
                population.add(temp);
                Collections.sort(population);
                number++;
            }
        }

        int unusedPairing = removePairing();

        if(unusedPairing != -1){
            System.out.println("outra");
            selectedPairings.remove(unusedPairing);
            if (bestChromosome.getCost() > population.get(0).getCost()){
                auxiliarSolveFunction(bestChromosome);
            }
            else{
                auxiliarSolveFunction(population.get(0));
            }
        }
        else {
            finalChromosome = bestChromosome;
        }

    }

    public void auxiliarSolveFunction() {

        int uncovered;
        int number = 0;
        int founded = 0;
        Chromosome temp;

        while (number < 15) {
            temp = initialPopulation();
            uncovered = allFlightsCovered(temp.getGenes());
            if (uncovered == 0) {
                System.out.println("iteracao");
                population.add(temp);
                Collections.sort(population);
                number++;
            }
        }


        int unusedPairing = removePairing();

        if (unusedPairing != -1) {
            System.out.println("outra");
            selectedPairings.remove(unusedPairing);
            auxiliarSolveFunction(population.get(0));
        } else {
            finalChromosome = population.get(0);
        }
    }


    private int removePairing() {
        boolean contain = false;

        for(int i = 0; i < selectedPairings.size(); i++){
            for(int j = 0; j < population.size(); j++){
                if(population.get(j).getGenes().get(i) == 1){
                    contain = true;
                }
            }
            if(!contain){
                return i;
            }
        }

        return -1;
    }


}
