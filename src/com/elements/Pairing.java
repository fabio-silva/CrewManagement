package com.elements;

import com.project.Main;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Pairing implements Comparable{

    final static int MAX_DUTIES = 3;
    final static double MIN_REST = 10.0;
    final static double MAX_REST = 18.0;
    final static double TAFB = 500;
    final static int _8IN24_ = 2;
    final double FD = 0.29;

    private ArrayList<Duty> duties;

    private double cost;
    private long timeAwayFromBase;
    private String equipment;


    public Pairing(){
        duties = new ArrayList<Duty>();
        cost = 0.0;
        timeAwayFromBase = 0;
    }

    public ArrayList<Duty> getDuties() {
        return duties;
    }

    public int hashCode(){
        int hash = 0;
        for(int i = 0; i < duties.size(); i++){
            hash += duties.get(i).hashCode();
        }

        return hash;
    }
    public boolean equals(Object obj){
        if(!(obj instanceof Pairing)){
            return false;
        }

        if(((Pairing) obj).duties.size() != this.duties.size()){
            return false;
        }

        int counter = 0;

        for(int i = 0; i < duties.size(); i++){
            for(int j = 0; j < ((Pairing) obj).duties.size(); j++){
                if(duties.get(i).equals(((Pairing) obj).duties.get(j))){
                    counter++;
                    break;
                }
            }
        }
        if(counter == duties.size()){
            return true;
        } else{
            return false;
        }
    }

    public long getTimeAwayFromBase() {
        return timeAwayFromBase;
    }

    @Override
    public int compareTo(Object o) {
        int otherNumberOfFlights = ((Pairing)o).getFlights().size();
        return Integer.compare(otherNumberOfFlights, this.getFlights().size());
    }

    public Date getFirstDate(){
        return duties.get(0).getFirstDate();
    }

    public Date getLastDate() {
        return duties.get(duties.size() - 1).getLastDate();
    }

    public ArrayList<Flight> getFlights(){
        ArrayList<Flight> flights = new ArrayList<Flight>();

        for(int i = 0; i < duties.size(); i++){
            flights.addAll(duties.get(i).getFlights());
        }

        return flights;
}

    public String getOrigin(){
        return duties.get(0).getOrigin();
    }

    public String getDestination(){
        return duties.get(duties.size()-1).getDestination();
    }


    public String toString(){
        String res = new String();

        for(int i = 0; i < duties.size(); i++){
            res += duties.get(i).toString() + "\n";
        }

        return res;
    }

    public double getBiggestFlightTime(){
        double biggestTime = 0;

        for (int i = 0; i < duties.size(); i++ ) {
            double biggestFlightTimeTemp = duties.get(i).getBiggestFlightTime();
            if(biggestFlightTimeTemp > biggestTime){
                biggestTime = biggestFlightTimeTemp;
            }
        }

        return biggestTime;
    }


    public double getCost() {
        return cost;
    }

    private void addDuty(Duty d) {
        if(duties.size() == 0){
            duties.add(d);
            timeAwayFromBase = TimeUnit.MILLISECONDS.toMinutes(d.getLastDate().getTime() - d.getFirstDate().getTime());
        }
        else{
            long timeAwayFromBaseTemp = TimeUnit.MILLISECONDS.toMinutes((d.getLastDate().getTime() - duties.get(duties.size()-1).getFirstDate().getTime()));
            if(duties.size() < 2 && getOrigin().compareTo(d.getDestination()) == 0 ){
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

        ArrayList<Pairing> pairings = new ArrayList<Pairing>();
        ArrayList<Duty> dutiesList = new ArrayList<Duty>(duties);

        for (int i = 0; i < dutiesList.size(); i++) {
            for (int j = 0; j < dutiesList.size(); j++) {
                Pairing p = new Pairing();
                Duty f = dutiesList.get(i);
                Duty c = dutiesList.get(j);
                p.addDuty(f);
                if(isFeasible(p)){
                    pairings.add(p);
                }
                Pairing pCopy = new Pairing();
                pCopy.addDuty(f);
                depthSearch(dutiesList, f, c, j, pCopy);
                if (isFeasible(pCopy))
                    pairings.add(pCopy);
            }
        }

        HashSet<Pairing> hashFromArray = new HashSet<Pairing>(pairings);
        for(Pairing p : hashFromArray){
            System.out.println("-------PAIRING-----------");
            System.out.println(p);
        }
        return hashFromArray;
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

        if(parent.getDestination().equals(child.getOrigin()) && diffMin >= 600 && diffMin <= 1800){
            p.addDuty(child);
        }
        else{
            if(childCounter+1 < duties.size())
                depthSearch(duties, parent, duties.get(childCounter+1), childCounter+1, p);
        }
    }

    public boolean hasCommonFlights(Pairing p){
        for(int i = 0; i < Main.flights.size(); i++){
            if(this.hasFlight(i) && p.hasFlight(i)){
                return true;
            }
        }

        return false;
    }

    public String getEquipment() {
        return duties.get(0).getFlights().get(0).getEquipment();
    }
}
