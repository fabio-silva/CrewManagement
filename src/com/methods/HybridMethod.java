package com.methods;


import com.elements.Flight;
import com.elements.Pairing;
import com.project.Main;

import java.util.*;

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

    public int allFlightsCovered(ArrayList<Integer> gene, ArrayList<Pairing> pairingsList){
        HashSet<Integer> flights = new HashSet<Integer>();

        for(Pairing p : pairingsList) {
            for(Flight f : p.getFlights()){
                flights.add(f.getFlightId());
            }
        }

        int flightSize = flights.size();
        int coveredFlights = 0;

        for(int i = 0; i < pairingsList.size(); i++){
            if(gene.get(i) == 1){
                coveredFlights += pairingsList.get(i).getFlights().size();
            }
        }
        return flightSize - coveredFlights;
    }

    public Chromosome initialPopulation(ArrayList<Pairing> pairingsList){

        ArrayList<State> states = new ArrayList<State>();
        ArrayList<Integer> availablePairings = new ArrayList<Integer>();
        Chromosome c = new Chromosome();

        int exitProbability;

        for(int i = 0; i < pairingsList.size(); i++){
            availablePairings.add(i);
        }

        State state = new State(availablePairings,new ArrayList<Integer>(), problemMatrix, pairingsList);

        Random r = new Random();
        int randomPairing = r.nextInt(availablePairings.size());
        int pairingsLeft = state.chooseAPairing(randomPairing);
        c.setGenes(new ArrayList<Integer>(state.getChomosome()));
        states.add(new State(state));

        while(allFlightsCovered(c.getGenes(), pairingsList) != 0){
            if(pairingsLeft > 0){
                randomPairing = r.nextInt(state.getAvailablePairings().size());
                pairingsLeft = state.chooseAPairing(randomPairing);
                states.add(new State(state));
                c.setGenes(new ArrayList<Integer>(state.getChomosome()));
            }
            else{
                exitProbability = r.nextInt(100);
                if(exitProbability > 90){  // in Hillclimbing prevent local minimums
                    return c;
                }
                states.remove(states.size() - 1);
                if (states.size() == 0){
                    return c;
                }
                state = states.get(states.size() - 1);
                while(state.getAvailablePairings().size() < 1){
                    states.remove(states.size() - 1);
                    if (states.size() == 0){
                        return c;
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

        return c;
    }



    @Override
    public ArrayList<Double> solve() {

        ArrayList<ArrayList<Pairing>> pairingsDivided = pairingsFromFlights();
        double totalCost = 0;
        Chromosome solution;
        int genesLeft;



        for (ArrayList<Pairing> pairingArray : pairingsDivided) {
            solution = initialPopulation(pairingArray);
            genesLeft = allFlightsCovered(solution.getGenes(), pairingArray);

            while(genesLeft != 0){
                System.out.println("Unavailable solution founded - " + genesLeft);
                solution = initialPopulation(pairingArray);
                genesLeft = allFlightsCovered(solution.getGenes(), pairingArray);
            }

            totalCost = solution.getCost();
            System.out.println("Available solution FONDED: " + solution.getCost() + "--------------------------------");

        }

        System.out.println("TOTAL: " + totalCost);
/*
        Chromosome solution = initialPopulation();
        int j = 0;

        while(solution == null){
            j++;
            System.out.println("new solution: " + j);
            solution = initialPopulation();
        }

        */

        return null;
    }

    private ArrayList<ArrayList<Pairing>> pairingsFromFlights() {
        ArrayList<Pairing> pairings = new ArrayList<Pairing>(selectedPairings);
        ArrayList<ArrayList<Pairing>> res = new ArrayList<ArrayList<Pairing>>();
        Collections.sort(pairings);
        int i;
        boolean added;

        System.out.println("size: " + pairings.get(0).getFlights().size());

        while(pairings.size() != 0){
            i = 0;
            ArrayList<Pairing> destination = new ArrayList<Pairing>();
            destination.add(pairings.get(0));
            pairings.remove(0);

            while(i < pairings.size()){
                added = false;
                for (int j = 0 ; j < destination.size(); j++){
                    if(hasCommonFlights(pairings.get(i), destination.get(j))) {
                        destination.add(pairings.get(i));
                        pairings.remove(i);
                        added = true;
                        break;
                    }
                }
                if(!added){
                    i++;
                }
            }
            res.add(new ArrayList<Pairing>(destination));
        }


        return res;

    }

    private boolean hasCommonFlights(Pairing pairing, Pairing pairing1) {
        ArrayList<Flight> pairingFlights = pairing.getFlights();

        for (int i = 0; i < pairingFlights.size(); i++) {
            if(pairing1.hasFlight(pairingFlights.get(i).getFlightId())) {
                return true;
            }
        }

        return false;
    }

}
