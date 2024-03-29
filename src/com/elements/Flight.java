package com.elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Flight {

    private int flightId;
    private String origin;
    private String destination;
    private String equipment;
    private Date departureTime;
    private Date arrivalTime;
    private Integer dayOfMonth;
    private ArrayList<Integer> operatingDays; //Binary array

    public Flight(String origin, String destination, String equipment, String departureTime, String arrivalTime, String days){
        this.origin = origin;
        this.destination = destination;
        this.equipment = equipment;
        this.operatingDays = parseDays(days);
        this.dayOfMonth = 0;
        this.flightId = -1;
        try{
            this.arrivalTime = new SimpleDateFormat("HH:mm").parse(arrivalTime);
            this.departureTime = new SimpleDateFormat("HH:mm").parse(departureTime);
        }catch (ParseException ex){
            System.out.println("Error parsing date");
        }
    }

    public Flight(Flight f){
        this.origin = f.origin;
        this.destination = f.destination;
        this.equipment = f.equipment;
        this.operatingDays = f.operatingDays;
        this.arrivalTime = f.arrivalTime;
        this.departureTime = f.departureTime;
        this.dayOfMonth = f.dayOfMonth;
        this.flightId = -1;
    }

    public String getOrigin(){
        return origin;
    }

    public int getFlightId() {
        return flightId;
    }

    public double getDuration(){
        return TimeUnit.MILLISECONDS.toMinutes(arrivalTime.getTime() - departureTime.getTime());
    }

    public String getDestination(){
        return destination;
    }

    public String getEquipment(){
        return equipment;
    }

    public Date getDepartureTime(){
        return departureTime;
    }

    public Date getArrivalTime(){
        return arrivalTime;
    }

    public ArrayList<Integer> getOperatingDays(){
        return operatingDays;
    }

    public Integer getDayOfMonth(){
        return dayOfMonth;
    }

    public void setDayOfMonth(Integer day, int flightId){
        this.dayOfMonth = day;
        String originalDeparture = new SimpleDateFormat("HH:mm").format(departureTime);
        String originalArrival = new SimpleDateFormat("HH:mm").format(arrivalTime);
        String newDeparture = "2014-05-" + day + " " + originalDeparture;
        String newArrival = "2014-05-" + day + " " + originalArrival;

        try {
            departureTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(newDeparture);
            arrivalTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(newArrival);
        }catch (ParseException ex){
            System.out.println("Parse Error");
        }
        this.flightId = flightId;
    }

    public ArrayList<Integer> parseDays(String days){
        ArrayList<Integer> dayArray = new ArrayList<Integer>();
        for(int i = 0; i < days.length(); i++){
            if(days.charAt(i) != '.'){
                dayArray.add(1);
            }
            else{
                dayArray.add(0);
            }
        }

        return dayArray;
    }

    public boolean equals(Object obj){
        if (!(obj instanceof Flight)) {
            return false;
        }

        Flight f = (Flight) obj;


        return this.departureTime.equals(f.departureTime)
               && this.arrivalTime.equals(f.arrivalTime)
               && this.origin.equals(f.origin)
               && this.destination.equals(f.destination)
               && this.dayOfMonth.equals(f.dayOfMonth);
    }

    public String toString(){
        return origin +"->" + destination + ", " + departureTime + "->" + arrivalTime + ", " + dayOfMonth;
    }

}
