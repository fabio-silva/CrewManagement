package com.methods;

import com.elements.Pairing;
import com.project.Main;

import java.awt.image.AreaAveragingScaleFilter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

public class GeneticAlgorithm extends Method{

    final double PENALTY = 0.75;
    private ArrayList<Chromosome> population;
    private int noInitialPopulation;

    public GeneticAlgorithm(ArrayList<Double> costMatrix, ArrayList<ArrayList<Double>> problemMatrix){
        super(costMatrix, problemMatrix);
        population = new ArrayList<Chromosome>();
        noInitialPopulation = Main.pairingsList.size() * 2;
    }

    public void initialPopulation(){
        Random r = new Random();
        int firstGene = r.nextInt(Main.pairingsList.size());
        int nextGene;
        ArrayList<Integer> genes = new ArrayList<Integer>();
        ArrayList<Integer> selectedPairings = new ArrayList<Integer>();
        ArrayList<Integer> selectedFlights = new ArrayList<Integer>();

        for(int i = 0; i < Main.pairingsList.size(); i++){
            if(i == firstGene) {
                genes.add(1);
                getFlightsForPairing(firstGene, selectedFlights);
                getPairingsWithDifferentFlights(selectedFlights, selectedPairings);
            } else{
                genes.add(0);
            }
        }

        while(selectedPairings.size() > 0){
            nextGene = r.nextInt(selectedPairings.size());
            genes.set(selectedPairings.get(nextGene), 1);
            getFlightsForPairing(selectedPairings.get(nextGene), selectedFlights);
            selectedPairings.clear();
            getPairingsWithDifferentFlights(selectedFlights, selectedPairings);
        }

        Chromosome c = new Chromosome(genes);
        correctChromosome(c);
        c.setFit(fitnessCost(c));
        population.add(c);


    }

    public void getFlightsForPairing(int pairingIndex, ArrayList<Integer> flights){
        for(int i = 0; i < problemMatrix.size(); i++){
            if(Main.pairingsList.get(pairingIndex).hasFlight(i)){
                flights.add(i);
            }
        }
    }

    public void getPairingsWithDifferentFlights(ArrayList<Integer> flights, ArrayList<Integer> pairings){
        boolean hasFlight;
        for(int i = 0; i < Main.pairingsList.size(); i++){
            hasFlight = false;
            for(int j = 0; j < flights.size(); j++){
                if(Main.pairingsList.get(i).hasFlight(flights.get(j))){
                    hasFlight = true;
                    break;
                }
            }
            if(hasFlight == false){
                pairings.add(i);
            }
        }
    }

    public void correctChromosome(Chromosome chromosome){
        boolean allFlightsCovered = true;

        do {
            allFlightsCovered = true;
            for (int i = 0; i < problemMatrix.size(); i++) {
                int flightCounter = 0;

                for (int j = 0; j < chromosome.getGenes().size(); j++) {
                    if (chromosome.getGenes().get(j) == 1 && problemMatrix.get(i).get(j).intValue() == 1) {
                        flightCounter++;
                    }
                }

                if (flightCounter > 1) { //Flight not covered exactly once
                    allFlightsCovered = false;
                    coverFlight(i, chromosome);
                }
            }
        }while(!allFlightsCovered);
    }

    public void coverFlight(int flightIndex, Chromosome chromosome){
        double minCost = Double.POSITIVE_INFINITY;
        int chosenPairing = -1;

        for(int i = 0; i < Main.pairingsList.size(); i++){
            if(Main.pairingsList.get(i).hasFlight(flightIndex) && Main.pairingsList.get(i).getCost() < minCost){
                minCost = Main.pairingsList.get(i).getCost();
                chosenPairing = i;
            }
        }

        if(chosenPairing != -1){
            chromosome.getGenes().set(chosenPairing, 1);
        }
    }

    public int noDeadHeadedFlights(Chromosome chromosome){
        int deadHeadedCounter = 0;
        int flightCounter = 0;

        for(int i = 0; i < problemMatrix.size(); i++){
            flightCounter = 0;
            for(int j = 0; j < problemMatrix.get(i).size(); j++){
                if(problemMatrix.get(i).get(j).intValue() == 1 && chromosome.getGenes().get(i) == 1){
                    flightCounter++;
                }
            }

            if(flightCounter > 1){
                deadHeadedCounter++;
            }
        }

        return deadHeadedCounter;
    }


    public double fitnessCost(Chromosome chromosome){
        int deadHeadedFlights = 0;
        double fitness = 0.0;

        for(int i = 0; i < chromosome.getGenes().size(); i++){
            fitness += Main.pairingsList.get(i).getCost()*chromosome.getGenes().get(i);
        }

        fitness += (PENALTY*noDeadHeadedFlights(chromosome));

        return fitness;
    }

    public ArrayList<Integer> crossover(Chromosome firstChromosome, Chromosome secondChromosome){
        ArrayList<Integer> child = new ArrayList<Integer>();

        for(int i = 0; i < firstChromosome.getGenes().size(); i++){
            if(firstChromosome.getGenes().get(i).equals(secondChromosome.getGenes().get(i))){
                child.add(firstChromosome.getGenes().get(i));
            } else{
                Random r = new Random();
                Integer randomNumber = new Integer(r.nextInt(2));
                child.add(randomNumber);
            }
        }

        return child;
    }


    public void mutate(Chromosome chromosome){
        Chromosome fittest = population.get(0);
        Random r = new Random();
        int genesMutated = r.nextInt(1 + chromosome.getGenes().size());
        int[] possibleValues = new int[]{0, 1};
        double zeroProbability = fittest.zeroProbability();
        double[] discreteProbabilities = new double[]{zeroProbability, 1-zeroProbability};
        EnumeratedIntegerDistribution distribution = new EnumeratedIntegerDistribution(possibleValues, discreteProbabilities);
        int[] samples = distribution.sample(genesMutated);

        for(int i = 0; i < genesMutated; i++){
            int randomGene = r.nextInt(chromosome.getGenes().size());
            chromosome.getGenes().set(randomGene, samples[i]);
        }
    }

    public ArrayList<Double> solve(){
        for(int i = 0; i < noInitialPopulation; i++){
            initialPopulation();
        }

        Collections.sort(population);
        Chromosome parent1, parent2;

        while(true){ //Satisfatory solution?
            parent1 = population.get(0);
            parent2 = population.get(1);

            Chromosome child = new Chromosome(crossover(parent1, parent2));
            mutate(child);
            correctChromosome(child);
            child.setFit(fitnessCost(child));
            population.set(population.size()-1, child);
            Collections.sort(population);
        }
    }
}
