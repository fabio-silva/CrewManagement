package com.elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class Pairing {

    final static int MAX_DUTIES = 3;
    final static double MIN_REST = 10.0;
    final static double MAX_REST = 18.0;
    final static double TAFB = 7200.0;
    final static int _8IN24_ = 2;
    final double FD = 0.29;

    private ArrayList<Duty> duties;

    private double cost;
    private double timeAwayFromBase;

    public Pairing(){
        duties = new ArrayList<Duty>();
        cost = 0.0;
        timeAwayFromBase = 0.0;
    }

    public ArrayList<Duty> getDuties() {
        return duties;
    }

    public String getOrigin(){
        return duties.get(0).getOrigin();
    }

    public String getDestination(){
        return duties.get(duties.size()-1).getDestination();
    }

    public double getCost() {
        return cost;
    }

    public boolean equals(Object obj){
        if(!(obj instanceof Pairing)){
            return false;
        }

        if(((Pairing) obj).duties.size() != duties.size()){
            return false;
        }


        for(int i = 0; i < duties.size(); i++){
            if (! ((Pairing) obj).duties.get(i).equals(this.duties.get(i))) {
                return false;
            }
        }

        return true;
    }

    private void addDuty(Duty d) {
        if(duties.size() == 0){
            duties.add(d);
            timeAwayFromBase = ((double)d.getLastDate().getTime() - (double)d.getFirstDate().getTime()) / (60*1000);
        }
        else{
            double timeAwayFromBaseTemp = ((double)d.getLastDate().getTime() - (double)duties.get(duties.size()-1).getFirstDate().getTime()) / (60*1000);
            if(duties.size() < 2 && timeAwayFromBaseTemp < TAFB && getOrigin().compareTo(d.getDestination()) == 0 ){
                duties.add(d);
                timeAwayFromBase = timeAwayFromBaseTemp;
            }
            else{
                return;
            }
        }

        double product = timeAwayFromBase * FD;

        double dutiesCost = 0;
        double min_guaranteed = 4.75*duties.size();

        for(Duty duty : duties){
            dutiesCost+= duty.getCost();
        }
        cost = Math.max(product, Math.max(dutiesCost, min_guaranteed));
    }

    public static HashSet<Pairing> makePairings(HashSet<Duty> duties) {

        HashSet<Pairing> pairings = new HashSet<Pairing>();
        ArrayList<Duty> dutiesList = new ArrayList<Duty>(duties);

        for (int i = 0; i < dutiesList.size(); i++) {
            for (int j = 0; j < dutiesList.size(); j++) {
                Pairing p = new Pairing();
                Duty f = dutiesList.get(i);
                Duty c = dutiesList.get(j);
                p.addDuty(f);
                depthSearch(dutiesList, f, c, j, p);
                if (isFeasible(p))
                    pairings.add(p);
            }
        }
        return pairings;
    }

    public static boolean isFeasible(Pairing p) {
        if(p.getOrigin().compareTo(p.getDestination()) == 0)
            return true;
        return false;
    }

    public boolean hasFlight(int flightId){
        for(Duty d : duties){
            if (d.hasFlight(flightId)) {
                return true;
            }
        }
        return false;
    }

    public static void depthSearch(ArrayList<Duty> duties, Duty parent, Duty child, int childCounter, Pairing p){
        if(child == null) return;

        if(p.getDuties().size() > 1) return;

        long diffMin = (child.getFirstDate().getTime() - parent.getLastDate().getTime()) / 60000;

        if(parent.getDestination().equals(child.getOrigin()) && diffMin >= 600 && diffMin <= 1080){
            p.addDuty(child);
            if(childCounter+1 < duties.size())
                depthSearch(duties, child, duties.get(childCounter+1), childCounter+1, p);
        }
        else{
            if(childCounter+1 < duties.size())
                depthSearch(duties, parent, duties.get(childCounter+1), childCounter+1, p);
        }
    }


}
