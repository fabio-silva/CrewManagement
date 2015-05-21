package com.methods;


import com.elements.Pairing;
import com.project.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class HybridMethod extends Method{

    private ArrayList<Chromosome> population;
    private ArrayList<Pairing> selectedPairings;
    private Chromosome finalChromosome;
    HashMap<Integer, Integer> choosePairing;

    public HybridMethod(ArrayList<Double> costMatrix, ArrayList<ArrayList<Double>> problemMatrix){

        super(costMatrix, problemMatrix);
        population = new ArrayList<Chromosome>();
        selectedPairings = new ArrayList<Pairing>(Main.pairingsList);
        finalChromosome = new Chromosome();
        choosePairing = new HashMap<Integer, Integer>();


        for(int i = 0; i < selectedPairings.size(); i++){
            choosePairing.put(i, 0);
        }
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

        ArrayList<State> states = new ArrayList<State>();
        ArrayList<Integer> availablePairings = new ArrayList<Integer>();
        Chromosome c = new Chromosome();

        int exitProbability;

        for(int i = 0; i < selectedPairings.size(); i++){
            availablePairings.add(i);
        }

        State state = new State(availablePairings,new ArrayList<Integer>(), problemMatrix);

        Random r = new Random();
        int randomPairing = r.nextInt(availablePairings.size());
        int pairingsLeft = state.chooseAPairing(randomPairing);
        c.setGenes(new ArrayList<Integer>(state.getChomosome()));
        states.add(new State(state));

        while(allFlightsCovered(c.getGenes()) != 0){
            if(pairingsLeft > 0){
                randomPairing = r.nextInt(state.getAvailablePairings().size());
                pairingsLeft = state.chooseAPairing(randomPairing);
                states.add(new State(state));
                c.setGenes(new ArrayList<Integer>(state.getChomosome()));
            }
            else{
                exitProbability = r.nextInt(100);
                if(exitProbability > 90){  // in Hillclimbing prevent local minimums
                    return null;
                }
                states.remove(states.size() - 1);
                if (states.size() == 0){
                    return null;
                }
                state = states.get(states.size() - 1);
                while(state.getAvailablePairings().size() < 1){
                    states.remove(states.size() - 1);
                    if (states.size() == 0){
                        return null;
                    }
                    state = states.get(states.size() - 1);
                }
                randomPairing = r.nextInt(state.getAvailablePairings().size());
                pairingsLeft = state.chooseAPairing(randomPairing);
                c.setGenes(new ArrayList<Integer>(state.getChomosome()));
            }
        }

        System.out.println("CONSEGUIU");
        System.out.println(c.getGenes() + " - " + c.getCost() );




        //int coveredFlights = allFlightsCovered(genes);
        //Chromosome c = new Chromosome(genes);
        //correctChromosome(c);
        //c.fit(problemMatrix);

        return c;
    }



    @Override
    public ArrayList<Double> solve() {



        Chromosome solution = initialPopulation();
        int j = 0;

        while(solution == null){
            j++;
            System.out.println("new solution: " + j);
            solution = initialPopulation();
        }

        return null;/*

        ArrayList<Double> res = new ArrayList<Double>();

        for(int i = 0; i < finalChromosome.getGenes().size(); i++){
            res.add(i, Double.valueOf(finalChromosome.getGenes().get(i)));
        }

        res.add(solution.getCost());

        return res;*/

        /*

        auxiliarSolveFunction();

        System.out.println("FINAL chromosome: " + finalChromosome.getGenes() + "  -  " + finalChromosome.getCost());
        if(allFlightsCovered(finalChromosome.getGenes()) == 0){
            System.out.println("Valid Solution");
        }


        ArrayList<Double> solution = new ArrayList<Double>();

        for(int i = 0; i < finalChromosome.getGenes().size(); i++){
            solution.add(i, Double.valueOf(finalChromosome.getGenes().get(i)));
        }

        return solution;*/
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
            System.out.println("nop");
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
            System.out.println("nop");

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
