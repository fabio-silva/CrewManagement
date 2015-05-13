package com.project;

import com.elements.Flight;
import com.simplex.Simplex;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        Map<Integer, ArrayList<Flight> > flights = new HashMap<Integer, ArrayList<Flight>>();
        ArrayList< Flight > fileFlights = new ArrayList<Flight>();

        for(int i = 1; i <= 28; i++){
            flights.put(i, new ArrayList<Flight>());
        }

        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader("flightSchedule"));
            while((line = reader.readLine()) != null){
                String[] parts = line.split(" ");
                fileFlights.add(new Flight(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));

            }
        }catch(FileNotFoundException ex){
            System.out.println("Not such file");
        }catch(IOException ex){
            System.out.println("Error reading");
        }

        for(int i = 0; i < 7; i++){
            ArrayList<Flight> firstWeekFlights = flights.get(i + 1);
            ArrayList<Flight> secondWeekFlights = flights.get((i + 1) + 7);
            ArrayList<Flight> thirdWeekFlights = flights.get((i + 1) + 14);
            ArrayList<Flight> fourthWeekFlights = flights.get((i + 1) + 21);

            for(int k = 0; k < fileFlights.size(); k++){
                if(fileFlights.get(k).getOperatingDays().get(i) == 1){
                    firstWeekFlights.add(new Flight(fileFlights.get(k)));
                    secondWeekFlights.add(new Flight(fileFlights.get(k)));
                    thirdWeekFlights.add(new Flight(fileFlights.get(k)));
                    fourthWeekFlights.add(new Flight(fileFlights.get(k)));
                }
            }

            for(int k = 0; k < firstWeekFlights.size(); k++){
                firstWeekFlights.get(k).setDayOfMonth(i + 1);
            }
            for(int k = 0; k < secondWeekFlights.size(); k++){
                secondWeekFlights.get(k).setDayOfMonth((i + 1) + 7);
            }
            for(int k = 0; k < thirdWeekFlights.size(); k++){
                thirdWeekFlights.get(k).setDayOfMonth((i + 1) + 14);
            }
            for(int k = 0; k < fourthWeekFlights.size(); k++){
                fourthWeekFlights.get(k).setDayOfMonth((i + 1) + 21);
            }



        }

       /* for(int i = 1; i <= 28; i++){
            System.out.println("------------------DAY " + i + "------------------------");
            for(int j = 0; j < flights.get(i).size(); j++){
                System.out.println(flights.get(i).get(j).getOrigin() + " -> " + flights.get(i).get(j).getDestination() + ", DAY = " + flights.get(i).get(j).getDayOfMonth());
            }
        }*/


        ArrayList<Double> costMatrix = new ArrayList<Double>(Arrays.asList(30.0, 40.0, 50.0));

        ArrayList<ArrayList<Double>> problemMatrix = new ArrayList<ArrayList<Double>>(Arrays.asList(
                new ArrayList<Double>(Arrays.asList(1.0, 0.0, 1.0)),
                new ArrayList<Double>(Arrays.asList(0.0, 1.0, 1.0)),
                new ArrayList<Double>(Arrays.asList(0.0, 1.0, 0.0))
        ));


        Simplex exampleProblem = new Simplex(costMatrix, problemMatrix);

        exampleProblem.solve();

    }
}
