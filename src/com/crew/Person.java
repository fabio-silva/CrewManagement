package com.crew;


import com.elements.Pairing;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Person {

    String function;
    int id;
    double flightHours;
    String equipment;
    boolean stay_overnight;
    int salary;
    ArrayList<Pairing> pairings;


    public Person(int id, String function, String equipment, boolean stayOvernight) {
        Random rand = new Random();
        this.id = id;
        this.function = function;
        this.salary = rand.nextInt(5000) + 500;
        this.flightHours = rand.nextInt(20000) + 1;
        this.equipment = equipment;
        this.stay_overnight = stayOvernight;
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

    public double getAuction(String flightEquipment, boolean stayOvernight) {

        if(flightEquipment.compareTo(this.equipment) != 0) {
            return 0.0;
        }
        double auction = (1 / this.salary) + this.flightHours;

        if(stayOvernight == this.stay_overnight){
            auction += 200;
        }

        return auction;
    }
}
