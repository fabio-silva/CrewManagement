package com.methods;


import com.crew.Person;
import com.elements.Pairing;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Assignment {

    ArrayList<Pairing> pairings;
    ArrayList<Person> crew;
    double averageSalaryStayOvernight;
    double averageSalaryDontStayOvernight;

    public Assignment(ArrayList<Person> crew) {
        this.crew = crew;
        findAverageSalaries();
    }

    private void findAverageSalaries() {
        int totalStayOvernightSalary = 0;
        int totalStayOvernight = 0;
        int totalDontStayOvernightSalary = 0;
        int totalDontStayOvernight = 0;

        for (Person p : crew) {
            if (p.stay_overnight()) {
                totalStayOvernight++;
                totalStayOvernightSalary+= p.getSalary();
            }
            else {
                totalDontStayOvernight++;
                totalDontStayOvernightSalary+= p.getSalary();
            }
        }

        averageSalaryStayOvernight = totalStayOvernightSalary / totalStayOvernight;
        averageSalaryDontStayOvernight = totalDontStayOvernightSalary / totalDontStayOvernight;
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

    public double getAverageSalaryStayOvernight() {
        return averageSalaryStayOvernight;
    }

    public double getAverageSalaryDontStayOvernight() {
        return averageSalaryDontStayOvernight;
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
        int attendantsAdded = 0;
        int captainAdded = 0;

        if (attendantsSize >= 4) {
            for(Map.Entry<Double,Person> entry : attendants.entrySet()) {
                if (attendantsAdded < 4) {
                    entry.getValue().addPairings(p);
                    attendantsAdded++;
                }
                else {
                    break;
                }
            }
        }
        else {
            missingCrew.add("attendants");
        }

        if (captainsSize >= 2) {
            for(Map.Entry<Double,Person> entry : captains.entrySet()) {
                if (captainAdded < 2) {
                    entry.getValue().addPairings(p);
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

    public void setPairings(ArrayList<Pairing> pairings) {
        this.pairings = pairings;
    }
}
