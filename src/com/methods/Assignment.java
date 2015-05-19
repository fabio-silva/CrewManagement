package com.methods;


import com.crew.Person;
import com.elements.Pairing;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

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

            ArrayList<String> missingCrew = findAvailableCrew(p, flightType, pairingDays);

            if(missingCrew.size() != 0){
                System.out.println("FALTA TRIPULACAO: " + missingCrew.get(0));
            }
        }
    }

    private ArrayList<String> findAvailableCrew(Pairing p, String flightType, double pairingDays) {

        ArrayList<String> missingCrew = new ArrayList<String>();

        Map<Double, Person> attendants = new TreeMap<Double, Person>();
        Person purser = new Person();
        Person captain = new Person();
        Person firstOfficer = new Person();
        double purserBiggestAuction = 0;
        double captainBiggestAuction = 0;
        double firstOfficerBiggestAuction = 0;
        double auction;

        for(Person person : crew){
            if (person.isAvailable(p)) {
                auction = person.getAuction(flightType, pairingDays);
                if(person.getFunction().compareTo("purser") == 0 && auction > purserBiggestAuction){
                    purser = person;
                    purserBiggestAuction = auction;
                }
                else if(person.getFunction().compareTo("first_officer") == 0 && auction > firstOfficerBiggestAuction){
                    firstOfficer = person;
                    firstOfficerBiggestAuction = auction;
                }
                else if(person.getFunction().compareTo("capitain") == 0 && auction > captainBiggestAuction){
                    captain = person;
                    captainBiggestAuction = auction;
                }
                else if(person.getFunction().compareTo("attendant") == 0){
                    attendants.put(auction, person);
                }
            }
        }

        if(purserBiggestAuction == 0) missingCrew.add("purser");
        if(captainBiggestAuction == 0) missingCrew.add("capitain");
        if(firstOfficerBiggestAuction == 0) missingCrew.add("first_officer");



        if(flightType.compareTo("small") == 0 && attendants.size() < 2 ||
                flightType.compareTo("medium") == 0 && attendants.size() < 4 ||
                flightType.compareTo("big") == 0 && attendants.size() < 6) missingCrew.add("attendant");

        if(missingCrew.size() != 0 ) return missingCrew;

        purser.addPairings(p);
        captain.addPairings(p);
        firstOfficer.addPairings(p);

        int attendantsSize = attendants.size();

        if(flightType.compareTo("small") == 0){
            attendants.get(attendantsSize - 1).addPairings(p);
            attendants.get(attendantsSize - 2).addPairings(p);
        } else if(flightType.compareTo("medium") == 0){
            attendants.get(attendantsSize - 1).addPairings(p);
            attendants.get(attendantsSize - 2).addPairings(p);
            attendants.get(attendantsSize - 3).addPairings(p);
            attendants.get(attendantsSize - 4).addPairings(p);
        }
        else if(flightType.compareTo("big") == 0){
            attendants.get(attendantsSize - 1).addPairings(p);
            attendants.get(attendantsSize - 2).addPairings(p);
            attendants.get(attendantsSize - 3).addPairings(p);
            attendants.get(attendantsSize - 4).addPairings(p);
            attendants.get(attendantsSize - 5).addPairings(p);
            attendants.get(attendantsSize - 6).addPairings(p);
        }

        return missingCrew;
    }

}
