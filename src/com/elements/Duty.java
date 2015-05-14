package com.elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Duty {
    final static double MIN_SIT = 0.5;
    final static double MAX_SIT = 4.0;
    final static double MAX_ELAPSE = 12.0;
    final static double MAX_FLY = 8.0;
    final double FD = 0.57; // 4/7
    final double MIN_GUARANTEED = 3.0;

    private ArrayList<Flight> flights;
    private double cost;
    private double elapsed;
    private double flyTime;
    private Date firstDate;
    private Date lastDate;

    public Duty(){
        flights = new ArrayList<Flight>();
        cost = 0.0;
        elapsed = 0.0;
        flyTime = 0.0;
        firstDate = null;
        lastDate = null;
    }

    public boolean equals(Object obj){
        if(!(obj instanceof Duty)){
            return false;
        }

        if(((Duty) obj).flights.size() != flights.size()){
            return false;
        }

        for(int i = 0; i < flights.size(); i++){
            if(this.flights.get(i).getOrigin().equals("FWA")) {
                if (!(this.flights.get(i).equals(((Duty) obj).flights.get(i)))) {
                    return false;
                }
            }
        }
        return true;
    }

    public int hashCode() {
        return (int)(cost*10+flyTime);
    }

    public double getCost(){
        return cost;
    }

    public Date getFirstDate(){
        return firstDate;
    }

    public Date getLastDate(){
        return lastDate;
    }

    public String getOrigin(){
        return flights.get(0).getOrigin();
    }

    public String getDestination(){
        return flights.get(flights.size()-1).getDestination();
    }

    public ArrayList<Flight> getFlights(){
        return flights;
    }

    public boolean hasFlight(int flightId){
        for(Flight f : flights) {
            if(f.getFlightId() == flightId){
                return true;
            }
        }

        return false;
    }

    public void addFlight(Flight f){
        if(flights.size() == 0){
            flights.add(f);
            elapsed = (((double)flights.get(0).getArrivalTime().getTime() - (double)flights.get(0).getDepartureTime().getTime())/(60*1000));
            flyTime = elapsed;
            firstDate = f.getDepartureTime();
        }
        else{
            double elapsedTemp = (((double)f.getArrivalTime().getTime() - (double)flights.get(0).getDepartureTime().getTime())/(60*1000));
            double flyTimeTemp = flyTime + (((double)f.getArrivalTime().getTime() - (double)f.getDepartureTime().getTime())/(60*1000));
            if(elapsedTemp <= 720 && flyTimeTemp <= 480){
                elapsed = elapsedTemp;
                flyTime = flyTimeTemp;
                flights.add(f);
            }
            else{
                return;
            }
        }

        lastDate = f.getArrivalTime();

        double product = elapsed * FD;

        cost = Math.max(product, Math.max(flyTime, MIN_GUARANTEED));

        //System.out.println("ELAPSED = " + elapsed + ", FLY = " + flyTime + ", COST = " + cost + ", PRODUCT = " + product);
    }

    public static HashSet<Duty> makeDuties(Map<Integer, ArrayList<Flight> > flights){
        HashSet<Duty> duties = new HashSet<Duty>();

        for(Map.Entry<Integer, ArrayList<Flight>> entry : flights.entrySet()){
            for(int i = 0; i < entry.getValue().size(); i++){
                for(int j = 0; j < entry.getValue().size(); j++){
                    Duty d = new Duty();
                    Flight f = entry.getValue().get(i);
                    Flight c = entry.getValue().get(j);
                    d.addFlight(f);
                    depthSearch(entry.getValue(), f, c, j, d);
                    duties.add(d);
                }
            }
        }
        return duties;
    }

    public static void depthSearch(ArrayList<Flight> flights, Flight parent, Flight child, int childCounter, Duty d){
        if(child == null) return;

        long diffMin = (child.getDepartureTime().getTime() - parent.getArrivalTime().getTime()) / 60000;

        if(parent.getDestination().equals(child.getOrigin()) && diffMin >= 30 && diffMin <= 240){
            d.addFlight(child);
            if(childCounter+1 < flights.size())
                depthSearch(flights, child, flights.get(childCounter+1), childCounter+1, d);
        }
        else{
            if(childCounter+1 < flights.size())
                depthSearch(flights, parent, flights.get(childCounter+1), childCounter+1, d);
        }
    }
}
