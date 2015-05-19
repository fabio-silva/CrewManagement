package com.crew;


import com.elements.Flight;
import com.elements.Pairing;

import java.util.ArrayList;
import java.util.Date;

public class Person {

    private String name;
    private String function;
    private double flightHours;
    private int age;
    private String flightPreference;
    private int maxNoDays;
    ArrayList<Pairing> pairings;


    public Person(String name, String function, double flightHours, int age, String flightPreference, int maxNoDays) {
        this.name = name;
        this.function = function;
        this.flightHours = flightHours;
        this.age = age;
        this.flightPreference = flightPreference;
        this.maxNoDays = maxNoDays;
    }

    public Person() {

    }

    public void addPairings(Pairing p){
        pairings.add(p);
    }


    public String getFunction() {
        return function;
    }

    public boolean isAvailable(Pairing p) {

        for (Pairing pairing : pairings) {

            Date endDate1 = new Date(pairing.getLastDate().getTime() + (1000 * 60 * 60 * 24));
            Date endDate2 = new Date(p.getLastDate().getTime() + (1000 * 60 * 60 * 24));

            if (endDate1.compareTo(p.getFirstDate()) >= 0 || endDate2.compareTo(pairing.getFirstDate()) >= 0) {
                return false;
            }
        }

        return true;
    }

    public double getAuction(String flightType, double pairingDays) {
        double auction = 0;

        if(flightType.compareTo(flightPreference) == 0){
            auction += 50;
        }

        if(pairingDays < maxNoDays){
            auction += 50;
        }

        auction = ( (flightHours / 10) * (age / 10) ) + auction;

        return auction;
    }
}
