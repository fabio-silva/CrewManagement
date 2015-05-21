package com.methods;

import com.elements.Pairing;
import com.project.Main;
import com.sun.tools.javac.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class State {

    private ArrayList<Integer> coveredFlights; // voos ja cobertos
    private ArrayList<Integer> availablePairings; // pairings disponiveis
    private ArrayList<Integer> selectedPairings; // pairings ja selecionados
    private ArrayList<Integer> chomosome; // genes de pairings selecionados (1) - ex : 000010001
    private ArrayList<Pairing> allPairings; // todos os pairings gerados
    private ArrayList<ArrayList<Double>> problemMatrix;

    public State(ArrayList<Integer> availablePairings, ArrayList<Integer> coveredFlights, ArrayList<ArrayList<Double>> problemMatrix) {

        this.coveredFlights = new ArrayList<Integer>(coveredFlights);
        this.availablePairings = new ArrayList<Integer>(availablePairings);
        this.selectedPairings = new ArrayList<Integer>();
        allPairings = new ArrayList<Pairing>(Main.pairingsList);
        this.problemMatrix = problemMatrix;
        this.chomosome = new ArrayList<Integer>(Collections.nCopies(allPairings.size(), 0));
    }

    public State(State state) {
        this.coveredFlights = new ArrayList<Integer>(state.getCoveredFlights());
        this.availablePairings = new ArrayList<Integer>(state.getAvailablePairings());
        this.selectedPairings = new ArrayList<Integer>(state.getSelectedPairings());
        allPairings = new ArrayList<Pairing>(Main.pairingsList);
        this.problemMatrix = state.getProblemMatrix();
        this.chomosome = new ArrayList<Integer>(state.getChomosome());
    }

    private void addCoveredFlights(int pairingNumber) {

        for(int i = 0; i < problemMatrix.size(); i++){
            if (allPairings.get(pairingNumber).hasFlight(i)){
                coveredFlights.add(i);
            }
        }
    }


    public int chooseAPairing(int randomPairing) {
        int pairingNumber = availablePairings.get(randomPairing);
        selectedPairings.add(pairingNumber);
        addCoveredFlights(pairingNumber);
        removeUnavailablePairings();
        chomosome.set(pairingNumber, 1);

        return availablePairings.size();
    }

    private void removeUnavailablePairings() {

        availablePairings = new ArrayList<Integer>();
        boolean hasFlight;

        for(int i = 0; i < allPairings.size(); i++){
            hasFlight = false;
            for(int j = 0; j < coveredFlights.size(); j++){
                if(allPairings.get(i).hasFlight(coveredFlights.get(j))){
                    hasFlight = true;
                    break;
                }
            }
            if(hasFlight == false){
                availablePairings.add(i);
            }
        }
    }

    public ArrayList<Integer> getChomosome() {
        return chomosome;
    }

    public ArrayList<Integer> getCoveredFlights() {
        return coveredFlights;
    }

    public ArrayList<Integer> getAvailablePairings() {
        return availablePairings;
    }

    public ArrayList<Integer> getSelectedPairings() {
        return selectedPairings;
    }

    public ArrayList<Pairing> getAllPairings() {
        return allPairings;
    }

    public ArrayList<ArrayList<Double>> getProblemMatrix() {
        return problemMatrix;
    }
}
