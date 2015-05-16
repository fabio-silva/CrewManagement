package com.methods;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class Chromosome implements Comparable{

    private ArrayList<Integer> genes;
    private double fit;

    public Chromosome(ArrayList<Integer> genes){
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
            probability = (double)genes.size() / zeroCounter;
        }

        return probability;
    }

    @Override
    public int compareTo(Object o) {
        double othersFit = ((Chromosome)o).getFit();
        return Double.compare(this.fit, othersFit);
    }
}