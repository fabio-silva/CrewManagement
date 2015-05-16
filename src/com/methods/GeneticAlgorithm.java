package com.methods;

import com.project.Main;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

public class GeneticAlgorithm extends Method{

    final double PENALTY = 0.75;
    private ArrayList<Chromosome> population;

    public GeneticAlgorithm(ArrayList<Double> costMatrix, ArrayList<ArrayList<Double>> problemMatrix){
        super(costMatrix, problemMatrix);
        population = new ArrayList<Chromosome>();

        //After initial population, call Collections.sort(population)
    }

    public void correctChromosome(Chromosome chromosome){
        boolean allFlightsCovered = true;

        do {
            allFlightsCovered = true;
            for (int i = 0; i < problemMatrix.size(); i++) {
                int flightCounter = 0;

                for (int j = 0; j < chromosome.getGenes().size(); j++) {
                    if (chromosome.getGenes().get(j).intValue() == 1 && problemMatrix.get(i).get(j).intValue() == 1) {
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
            chromosome.getGenes().set(chosenPairing, 1.0);
        }
    }

    public int noDeadHeadedFlights(Chromosome chromosome){
        int deadHeadedCounter = 0;
        int flightCounter = 0;

        for(int i = 0; i < problemMatrix.size(); i++){
            flightCounter = 0;
            for(int j = 0; j < problemMatrix.get(i).size(); j++){
                if(problemMatrix.get(i).get(j).intValue() == 1 && chromosome.getGenes().get(i).intValue() == 1){
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

    public ArrayList<Double> crossover(Chromosome firstChromosome, Chromosome secondChromosome){
        ArrayList<Double> child = new ArrayList<Double>();

        for(int i = 0; i < firstChromosome.getGenes().size(); i++){
            if(firstChromosome.getGenes().get(i).equals(secondChromosome.getGenes().get(i))){
                child.add(firstChromosome.getGenes().get(i));
            } else{
                Random r = new Random();
                Integer randomNumber = new Integer(r.nextInt(2));
                child.add(randomNumber.doubleValue());
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
            chromosome.getGenes().set(randomGene, (double)samples[i]);
        }
    }

    public ArrayList<Double> solve(){
        return null;
    }
}
