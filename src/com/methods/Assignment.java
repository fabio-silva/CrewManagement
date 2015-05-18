package com.methods;


import com.crew.Person;
import com.elements.Pairing;

import java.util.ArrayList;

public class Assignment {

    ArrayList<Pairing> pairings;
    ArrayList<Person> crew;


    public Assignment(ArrayList<Pairing> pairings, ArrayList<Person> crew) {
        this.pairings = pairings;
        this.crew = crew;
    }

    public void solve(){
        for(Pairing p : pairings){
            double pairingDays = p.getTimeAwayFromBase() / 1440;
            double biggestFlightTime = p.getBiggestFlightTime();
            ArrayList<Person> availableCrew = findAvailableCrew(p);

            String flightType;

            if (biggestFlightTime > 120 && biggestFlightTime < 600){
                flightType = "medium";
            }
            else if (biggestFlightTime >= 600){
                flightType = "big";
            }
            else{
                flightType = "small";
            }

            findCapitain(availableCrew, flightType, pairingDays);
            findFirstOfficer(availableCrew, flightType, pairingDays);
            findPurser(availableCrew, flightType, pairingDays);
            findAttendant(availableCrew, flightType, pairingDays);
        }
    }

    private ArrayList<Person> findAvailableCrew(Pairing p) {
        ArrayList<Person> availablePerson = new ArrayList<Person>();

        for(Person person : crew){
            if (person.isAvailable(p)) {
                availablePerson.add(person);
            }
        }

        return availablePerson;
    }

    private void findAttendant(ArrayList<Person> availableCrew, String flightType, double pairingDays) {

        for(Person selectedAttentant : availableCrew){

        }

    }

    private void findPurser(ArrayList<Person> availableCrew, String flightType, double pairingDays) {

    }

    private void findFirstOfficer(ArrayList<Person> availableCrew, String flightType, double pairingDays) {

    }

    private void findCapitain(ArrayList<Person> availableCrew, String flightType, double pairingDays) {
        ArrayList<Person> candidates = new ArrayList<Person>();


    }
}
