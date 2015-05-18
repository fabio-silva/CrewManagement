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
}
