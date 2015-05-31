package com.methods;


import com.elements.Flight;
import com.elements.Pairing;
import com.project.Main;

import java.awt.image.AreaAveragingScaleFilter;
import java.net.CookieHandler;
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

//        for(ArrayList<Pairing> p : pairingsDivided){
//            for (Pairing pai : p ){
//                System.out.print(pai.getFlights());
//            }
//            System.out.println("\n\n\n");
//        }


//        double totalCost = 0;
//        Chromosome solution;
//        int genesLeft;
//
//
//
//        for (ArrayList<Pairing> pairingArray : pairingsDivided) {
//            solution = initialPopulation(pairingArray);
//            genesLeft = allFlightsCovered(solution.getGenes(), pairingArray);
//
//            while(genesLeft != 0){
//                System.out.println("Unavailable solution founded - " + genesLeft);
//                solution = initialPopulation(pairingArray);
//                genesLeft = allFlightsCovered(solution.getGenes(), pairingArray);
//            }
//
//            totalCost = solution.getCost();
//            System.out.println("Available solution FONDED: " + solution.getCost() + "--------------------------------");
//
//        }

        //System.out.println(pairingsDivided.size());
        findSimplexSolution(pairingsDivided);

        return null;
    }

    private void findSimplexSolution(ArrayList<ArrayList<Pairing>> pairingsDivided) {
        ArrayList<Double> costMatrix;
        ArrayList<ArrayList<Double>> matrix;
        ArrayList<Double> flightLine;
        HashSet<Integer> flightIds;

        for (ArrayList<Pairing> pairingArray : pairingsDivided) {

            flightIds = new HashSet<Integer>();
            matrix = new ArrayList<ArrayList<Double>>();
            costMatrix = new ArrayList<Double>();

            for(Pairing p : pairingArray){
                costMatrix.add(p.getCost());
                for( Flight f : p.getFlights()){
                    flightIds.add(f.getFlightId());
                }
            }

            for (Integer fId : flightIds){
                flightLine = new ArrayList<Double>();

                for(Pairing p : pairingArray) {
                    if(p.hasFlight(fId)) {
                        flightLine.add(1.0);
                    }
                    else {
                        flightLine.add(0.0);
                    }
                }
                matrix.add(flightLine);
            }

            Simplex exampleProblem = new Simplex(costMatrix, matrix);

            ArrayList<Double> simplexSolution = exampleProblem.solve();

            System.out.println("Solução com simplex: " + simplexSolution);

            Scanner s = new Scanner(System.in);
            // s.nextLine();

        }
    }

    private ArrayList<ArrayList<Pairing>> pairingsFromFlights() {
        ArrayList<Pairing> pairings = new ArrayList<Pairing>(selectedPairings);
        ArrayList<ArrayList<Pairing>> res;
//        Collections.sort(pairings);
//        int i;
//        boolean added;
//
//        while(pairings.size() != 0){
//            i = 0;
//            ArrayList<Pairing> destination = new ArrayList<Pairing>();
//            destination.add(pairings.get(0));
//            pairings.remove(0);
//
//            while(i < pairings.size()){
//                added = false;
//                for (int j = 0 ; j < destination.size(); j++){
//                    if(hasCommonFlights(pairings.get(i), destination.get(j))) {
//                        destination.add(pairings.get(i));
//                        pairings.remove(i);
//                        added = true;
//                        break;
//                    }
//                }
//                if(!added){
//                    i++;
//                }
//            }
//            res.add(new ArrayList<Pairing>(destination));
//        }

        res = convertToArray(pairings);

        ArrayList<ArrayList<Pairing>> sorted = sortPairings(res);

        for(ArrayList<Pairing> x : sorted) {
            for (Pairing y : x) {
                System.out.print(y.getFlights());
            }
            System.out.println();
        }


        boolean add = hasCommonFlightsBetweenPairings(sorted);

        while(add){
            System.out.println("---------------------------------------------------------------------------------");
            for(ArrayList<Pairing> x : sorted) {
                for (Pairing y : x) {
                    System.out.print(y.getFlights());
                }
                System.out.println();
            }
            sorted = sortPairings(sorted);
            add = hasCommonFlightsBetweenPairings(sorted);
        }

        System.out.println("---------------------------------------------------------------------------------");


        return sorted;

    }

    private ArrayList<ArrayList<Pairing>> convertToArray(ArrayList<Pairing> pairings) {

        ArrayList<ArrayList<Pairing>> res = new ArrayList<ArrayList<Pairing>>();
        ArrayList<Pairing> auxiliar;

        for (int i = 0; i < pairings.size(); i++){
            auxiliar = new ArrayList<Pairing>();
            auxiliar.add(pairings.get(i));
            res.add(new ArrayList<Pairing>(auxiliar));
        }

        return res;
    }

    private boolean hasCommonFlightsBetweenPairings( ArrayList<ArrayList<Pairing>> sorted) {
        int iterator = 0;
        int next = 1;
        boolean found;
        boolean res = false;

        while (iterator < sorted.size() - 1) {

            found = false;

            for(int j = 0; j < sorted.get(iterator).size(); j++ ) {
                for(int k = 0; k < sorted.get(next).size(); k++ ) {
                    if(hasCommonFlights(sorted.get(iterator).get(j), sorted.get(next).get(k))) {
                        found = true;
                        sorted.get(iterator).addAll(sorted.get(next));
                        sorted.remove(next);
                        res = true;
                        break;
                    }
                }
                if(found){
                    break;
                }
            }


                if (next < (sorted.size() - 1)) {
                    next++;
                } else {
                    iterator++;
                    next = iterator + 1;
                }
        }

        return res;
    }

    private ArrayList<ArrayList<Pairing>> sortPairings(ArrayList<ArrayList<Pairing>> pairings) {
        ArrayList<ArrayList<Pairing>> res = new ArrayList<ArrayList<Pairing>>();
        int maxSize;
        int iterator;

        while(pairings.size() != 0) {
            maxSize = 0;
            iterator = 0;


            for (int i = 0; i < pairings.size(); i++) {
                if (pairings.get(i).size() >= maxSize) {
                    iterator = i;
                    maxSize = pairings.get(i).size();
                }
            }
            res.add(new ArrayList<Pairing>(pairings.get(iterator)));
            pairings.remove(iterator);

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
