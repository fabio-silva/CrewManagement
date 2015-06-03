package com.project;

import com.crew.Person;
import com.elements.Duty;
import com.elements.Flight;
import com.elements.Pairing;
import com.methods.Assignment;
import com.methods.GeneticAlgorithm;
import com.methods.HybridMethod;
import com.methods.Simplex;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.text.html.HTMLDocument;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;

public class Main {

    public static ArrayList<Pairing> pairingsList;
    public static HashSet<Duty> duties;
    public static Map<Integer, ArrayList<Flight> > flights;
    public static ArrayList<Person> crewAvailable;
    public static Assignment assignment;

    public static void main(String[] args) {

        flights = new HashMap<Integer, ArrayList<Flight>>();
        ArrayList< Flight > fileFlights = new ArrayList<Flight>();
        crewAvailable = new ArrayList<Person>();


        for(int i = 1; i <= 28; i++){
            flights.put(i, new ArrayList<Flight>());
        }

        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader("flightSchedule_min"));
            while((line = reader.readLine()) != null){
                String[] parts = line.split(" ");
                fileFlights.add(new Flight(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
            }

            String crewData = readFile("crew.json");
            JSONArray crew = new JSONArray(crewData);
            for (int i = 0; i < crew.length(); i++) {
                JSONObject playerData = (JSONObject) crew.get(i);
                Person p = instantiatePerson(playerData, i);
                crewAvailable.add(p);
            }
            assignment = new Assignment(crewAvailable);
        }catch(FileNotFoundException ex){
            System.out.println("Not such file");
        }catch(IOException ex){
            System.out.println("Error reading");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int flightId = 0;
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
                firstWeekFlights.get(k).setDayOfMonth(i + 1, flightId);
                flightId++;
            }
            for(int k = 0; k < secondWeekFlights.size(); k++){
                secondWeekFlights.get(k).setDayOfMonth((i + 1) + 7, flightId);
                flightId++;
            }
            for(int k = 0; k < thirdWeekFlights.size(); k++){
                thirdWeekFlights.get(k).setDayOfMonth((i + 1) + 14, flightId);
                flightId++;
            }
            for(int k = 0; k < fourthWeekFlights.size(); k++){
                fourthWeekFlights.get(k).setDayOfMonth((i + 1) + 21, flightId);
                flightId++;
            }
        }

        int numberOfFlights = 0;

        for(int i = 1; i <= 28; i++){
            numberOfFlights += flights.get(i).size();
        }

        System.out.println("Number of flights: " + numberOfFlights);
        duties = Duty.makeDuties(flights);
        System.out.println("Number of feasible duties: " + duties.size());
        HashSet<Pairing> pairings = Pairing.makePairings(duties);
        pairingsList = new ArrayList<Pairing>(pairings);
        System.out.println("Number of feasible pairings: " + pairingsList.size());
        ArrayList<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
        ArrayList<Double> flightLine;
        ArrayList<Double> costMatrix = new ArrayList<Double>();


        for(Map.Entry<Integer, ArrayList<Flight>> f : flights.entrySet()){
            for(int i = 0; i < f.getValue().size(); i++){

                flightLine = new ArrayList<Double>();
                for(Pairing p : pairingsList){
                    if (p.hasFlight(f.getValue().get(i).getFlightId())){
                        flightLine.add(1.0);
                    }
                    else {
                       flightLine.add(0.0);
                    }
                }
                matrix.add(flightLine);
            }
        }

        for(Pairing p : pairingsList){
            costMatrix.add(p.getCost());
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Choose method: \n1 - Simplex\n2 - HillClimbing\n3 - Simplex hybrid");
        String choose = sc.nextLine();

        ArrayList<Pairing> finalSolution;
        long start = System.currentTimeMillis();

        if (choose.compareTo("1") == 0){
            Simplex exampleProblem = new Simplex(costMatrix, matrix, pairingsList);
            finalSolution = exampleProblem.solve();
        }
        else if (choose.compareTo("2") == 0) {
            HybridMethod hm = new HybridMethod(costMatrix, matrix);
            finalSolution = hm.solve();
        }
        else {
            HybridMethod hm = new HybridMethod(costMatrix, matrix);
            finalSolution = hm.simplexHybrid();
        }

        printSolution(finalSolution);

        long elapsed = System.currentTimeMillis() - start;

        System.out.println("elapsed time: " + elapsed/1000f);

    }

    private static void printSolution(ArrayList<Pairing> finalSolution) {
        assignment.setPairings(finalSolution);
        assignment.solve();
        System.out.println("SOLUÇÃO: " + finalSolution.size());
        double price = 0;

        for(Pairing p : finalSolution) {
            price += p.getCost();
            System.out.println(p);
            System.out.println("------------------------------------------------------------------------------------");
        }
        System.out.println("CUSTO FINAL : " + price);
    }

    public static boolean hasFlight(Duty d){
        for(int j = 0; j < d.getFlights().size(); j++){
            if(d.getFlights().get(j).getFlightId() == 0)
                return true;
        }
        return false;
    }

    private static String readFile(String name) throws IOException {
        FileInputStream stream = new FileInputStream(name);
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        } finally {
            stream.close();
        }
    }

    private static Person instantiatePerson(JSONObject crewData, int id) throws JSONException {
        String function = crewData.getString("Function");
        String equipment = crewData.getString("Equipment");
        boolean stayOvernight = crewData.getBoolean("Stay overnight");

        return new Person(id, function, equipment, stayOvernight);
    }

}
