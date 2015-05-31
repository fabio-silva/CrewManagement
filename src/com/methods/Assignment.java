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
            String equipment = p.getEquipment();
            boolean stayOvernight = false;
            if (p.getDuties().size() > 1) {
                stayOvernight = true;
            }

            ArrayList<String> missingCrew = findAvailableCrew(p, equipment, stayOvernight);

            if(missingCrew.size() != 0){
                System.out.println("FALTA TRIPULACAO: " + missingCrew.get(0));
            }
        }
    }

    private ArrayList<String> findAvailableCrew(Pairing p, String equipment, boolean stayOvernight) {
        ArrayList<String> missingCrew = new ArrayList<String>();
        Map<Double, Person> attendants = new TreeMap<Double, Person>();
        Map<Double, Person> captains = new TreeMap<Double, Person>();
        double auction;

        for(Person person : crew){
            if (person.isAvailable(p)) {
                auction = person.getAuction(equipment, stayOvernight);

                if(person.getFunction().compareTo("captain") == 0){
                    captains.put(auction, person);
                }
                else if(person.getFunction().compareTo("attendant") == 0){
                    attendants.put(auction, person);
                }
            }
        }

        int attendantsSize = attendants.size();
        int captainsSize = captains.size();
        int attedantsAdded = 0;
        int captainAdded = 0;

        if (attendantsSize >= 4) {
            for(Map.Entry<Double, Person> a : attendants.entrySet()){
                if (attedantsAdded < 5) {
                    attendants.get(a).addPairings(p);
                    attedantsAdded++;
                }
                else {
                    break;
                }
            }
        }
        else {
            missingCrew.add("attendants");
        }

        if (captainsSize >= 4) {
            for(Map.Entry<Double, Person> a : captains.entrySet()){
                if (captainAdded < 5) {
                    captains.get(a).addPairings(p);
                    captainAdded++;
                }
                else {
                    break;
                }
            }
        }
        else {
            missingCrew.add("captains");
        }

        return missingCrew;
    }

}
