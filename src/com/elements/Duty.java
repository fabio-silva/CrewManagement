package com.elements;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Duty {
    final static double MIN_SIT = 0.5;
    final static double MAX_SIT = 4.0;
    final static double MAX_ELAPSE = 12.0;
    final static double MAX_FLY = 8.0;
    final double FD = 0.57; // 4/7
    final double MIN_GUARANTEED = 3.0;

    private ArrayList<Flight> flights;
    private double cost;
    private long elapsed;
    private long flyTime;
    private Date firstDate;
    private Date lastDate;
    private double biggestFlightTime;

    public Duty(){
        flights = new ArrayList<Flight>();
        cost = 0.0;
        elapsed = 0;
        flyTime = 0;
        firstDate = null;
        lastDate = null;
    }

    public boolean equals(Object obj){
        Duty d = (Duty)obj;

        if(!(obj instanceof Duty)){
            return false;
        }

        if(((Duty) obj).flights.size() != this.flights.size()){
            return false;
        }

        int counter = 0;

        for(int i = 0; i < flights.size(); i++){
            for(int j = 0; j < ((Duty) obj).flights.size(); j++){
                if(flights.get(i).equals(((Duty) obj).flights.get(j))){
                    counter++;
                    break;
                }
            }
        }
        if(counter == flights.size()){
            return true;
        } else{
            return false;
        }
    }

    public int hashCode() {
        int hash = firstDate.hashCode() + lastDate.hashCode() + flights.size();
        return hash;
    }

    public double getCost(){
        return cost;
    }

    public Date getFirstDate(){
        return firstDate;
    }

    public double getBiggestFlightTime() {
        double biggestFlightTime = 0;

        for(int i = 0; i < flights.size(); i++){
            double biggestFlightTimeTemp = flights.get(i).getDuration();
            if(biggestFlightTimeTemp > biggestFlightTime){
                biggestFlightTime = biggestFlightTimeTemp;
            }
        }

        return biggestFlightTime;
    }

    public Date getLastDate(){
        return lastDate;
    }

    public String getOrigin(){
        return flights.get(0).getOrigin();
    }

    public String toString(){
        String res = new String();
        for(int i = 0; i < flights.size(); i++){
            res += " " + flights.get(i);
        }

        return "DUTY :" + res;
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
            elapsed = TimeUnit.MILLISECONDS.toMinutes(( flights.get(0).getArrivalTime().getTime() - flights.get(0).getDepartureTime().getTime()));
            flyTime = elapsed;
            firstDate = f.getDepartureTime();
        }
        else{
            long elapsedTemp = TimeUnit.MILLISECONDS.toMinutes((f.getArrivalTime().getTime() - flights.get(0).getDepartureTime().getTime()));
            long flyTimeTemp = flyTime + TimeUnit.MILLISECONDS.toMinutes((f.getArrivalTime().getTime() - f.getDepartureTime().getTime()));
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

    }

    public static HashSet<Duty> makeDuties(Map<Integer, ArrayList<Flight> > flights){
        ArrayList<Duty> duties = new ArrayList<Duty>();

        for(Map.Entry<Integer, ArrayList<Flight>> entry : flights.entrySet()){
            for(int i = 0; i < entry.getValue().size(); i++){
                for(int j = 0; j < entry.getValue().size(); j++){
                    Duty d = new Duty();
                    Flight f = entry.getValue().get(i);
                    Flight c = entry.getValue().get(j);
                    d.addFlight(f);
                    duties.add(d);
                    Duty dCopy = new Duty();
                    dCopy.addFlight(f);
                    depthSearch(entry.getValue(), f, c, j, dCopy);
                    duties.add(dCopy);
                }
            }
        }

        return new HashSet<Duty>(duties);
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
