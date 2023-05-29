package org.example;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.IOException;
import java.sql.SQLException;

public class Main {
    TradeCalculator tc;
    static PlayerDAO playerDatabase;
    static String[] positionList;
    static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("Welcome to the Fantasy Trade Calculator!");
        initialize();
        HashMap<String, Integer> rosCons = setRosConstruction();
        Team userTeam = new Team ("User's Team", rosCons);
        Team oppTeam = new Team ("Trade Partner's Team", rosCons);

    }

    public static void initialize(){
        try{
            scanner = new Scanner(System.in);
            DatabaseManager dm = new DatabaseManager();
            playerDatabase = dm.createPlayerDAO();
            positionList = new String[]{"QB", "RB", "WR", "TE"};
        }
        catch (SQLException | IOException e){
            System.out.println("Database could not be accessed. Please rerun the program.");
        }
    }

    public static HashMap<String, Integer> setRosConstruction() {
        HashMap<String, Integer> rosCons = new HashMap<>();
        for (String pos : positionList) {
            System.out.println("Please enter the number of " + pos + "s on your starting roster.");
            int inputNum;
            while (true) {
                try {
                    inputNum = scanner.nextInt();
                    if (inputNum > 0 && inputNum < 5) {
                        break;
                    }
                    System.out.println("Please enter a valid number.");
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid number.");
                }
                scanner.nextLine();
            }
            rosCons.put(pos, inputNum);
        }
        return rosCons;
    }


}
