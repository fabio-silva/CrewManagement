package com.crew;


public class Person {

    private String name;
    private String function;
    private double flightHours;
    private int age;
    private String flightPreference;
    private int maxNoDays;


    public Person(String name, String function, double flightHours, int age, String flightPreference, int maxNoDays) {
        this.name = name;
        this.function = function;
        this.flightHours = flightHours;
        this.age = age;
        this.flightPreference = flightPreference;
        this.maxNoDays = maxNoDays;
    }
}
