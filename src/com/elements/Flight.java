package com.elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Flight {

    private String origin;
    private String destination;
    Date departureTime;
    Date arrivalTime;
    ArrayList<Integer> operatingDays; //Binary array

    public Flight(String origin, String destination, String departureTime, String arrivalTime, String days){
        this.origin = origin;
        this.destination = destination;
        this.operatingDays = parseDays(days);

        try{
            this.arrivalTime = new SimpleDateFormat("HH:mm").parse(arrivalTime);
            this.departureTime = new SimpleDateFormat("HH:mm").parse(departureTime);
        }catch (ParseException ex){
            System.out.println("Error parsing date");
        }
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
}
