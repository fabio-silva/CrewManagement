package com.methods;

import com.elements.Pairing;
import com.project.Main;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import org.apache.commons.math3.distribution.EnumeratedIntegerDistribution;

public class GeneticAlgorithm extends Method{

    private ArrayList<Chromosome> population;

    public GeneticAlgorithm(ArrayList<Double> costMatrix, ArrayList<ArrayList<Double>> problemMatrix){
        super(costMatrix, problemMatrix);
        population = new ArrayList<Chromosome>();
    }

    public int allFlightsCovered(ArrayList<Integer>gene){
        int flightSize = problemMatrix.size();
        int coveredFlights = 0;

        for(int i = 0; i < Main.pairingsList.size(); i++){
            if(gene.get(i) == 1){
                coveredFlights += Main.pairingsList.get(i).getFlights().size();
            }
        }


        return flightSize - coveredFlights;
    }

    public Chromosome initialPopulation(){
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

        int coveredFlights = allFlightsCovered(genes);
        Chromosome c = new Chromosome(genes);
        c.fit(problemMatrix);
        correctChromosome(c);

        return c;
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
            //System.out.println("COVERING");
            chromosome.getGenes().set(chosenPairing, 1);
        }
    }

    public boolean isAvailableSolution(Chromosome chromosome){
        int flightCounter = 0;

        for(int i = 0; i < problemMatrix.size(); i++){
            flightCounter = 0;
            for(int j = 0; j < problemMatrix.get(i).size(); j++){
                if(problemMatrix.get(i).get(j).intValue() == 1 && chromosome.getGenes().get(j) == 1){
                    flightCounter++;
                }
            }

            if(flightCounter != 1){
                return false;
            }
        }

        return true;

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
        int genesMutated = r.nextInt(chromosome.getGenes().size()) + 1;
        int[] possibleValues = new int[]{0, 1};
        double zeroProbability = fittest.zeroProbability();
        //System.out.println("zero prob = " + zeroProbability);
        double[] discreteProbabilities = new double[]{zeroProbability, 1-zeroProbability};
        EnumeratedIntegerDistribution distribution = new EnumeratedIntegerDistribution(possibleValues, discreteProbabilities);
        int[] samples = distribution.sample(genesMutated);

        for(int i = 0; i < genesMutated; i++){
            int randomGene = r.nextInt(chromosome.getGenes().size());
            chromosome.getGenes().set(randomGene, samples[i]);
        }
    }

    public ArrayList<Double> solve(){

        /*

        for(int i = 0; i < noInitialPopulation; i++){
            initialPopulation();
        }

        */
        int uncovered;
        int number = 0;
        int founded = 0;
        Chromosome temp;

        ArrayList<Pairing> pairingList = new ArrayList<Pairing>(Main.pairingsList);

        while(number < 20){
            temp = initialPopulation();
            uncovered = allFlightsCovered(temp.getGenes());
            if(uncovered == 0) {
                population.add(temp);
                number++;
            }
        }

        int unusedPairing = removePairing(pairingList);

        if(unusedPairing != -1){
            pairingList.remove(unusedPairing);

        }
        else {

        }

        System.out.println("INTERATION: " + number + " and founded: " + founded);
        System.exit(1);




        System.out.println("initial");
        Collections.sort(population);
        Chromosome parent1, parent2;
        int c = 0;

        boolean available = false;

        System.out.println("BEST COST BEFORE GENETIC: " + population.get(0).getCost());
        double bestCost =  population.get(0).getFit();
        double tempCost = population.get(0).getFit();

        while(!available || bestCost == tempCost){ //Satisfatory solution?

            int[] possibleValues = new int[population.size()];
            double[] discreteProbabilities = new double[population.size()];
            for(int i = 0; i < population.size(); i++){
                possibleValues[i] = i;
                discreteProbabilities[i] = 1 / population.get(i).getFit();
            }

            EnumeratedIntegerDistribution distribution = new EnumeratedIntegerDistribution(possibleValues, discreteProbabilities);
            int[] samples = distribution.sample(2);

            parent1 = population.get(samples[0]);
            parent2 = population.get(samples[1]);

            Chromosome child = new Chromosome(crossover(parent1, parent2));
            //System.out.println("MUTATING");
            mutate(child);
            //System.out.println("MUTATED");
            correctChromosome(child);

            //System.out.println("CORRECTED");
            child.fit(problemMatrix);
            System.out.println("CHILD FIT: " +  child.getFit());

            population.set(population.size()-1, child);
            Collections.sort(population);
            c++;
            available = isAvailableSolution(population.get(0));
            tempCost = population.get(0).getFit();
            System.out.println(c);
            System.out.println("BEST cost = " + bestCost);
            System.out.println("TEMP cost = " + tempCost);
        }

        System.out.println("BEST = " + population.get(0).getGenes());
        System.out.println("BEST COST AFTER GENETIC: " + population.get(0).getFit());

        return null;
    }

    private int removePairing(ArrayList<Pairing> pairingList) {
        boolean contain = false;

        for(int i = 0; i < pairingList.size(); i++){
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
