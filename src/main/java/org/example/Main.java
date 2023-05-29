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
        int rosterSize = getRosterSize(userTeam);
        fillTeamRoster(userTeam, "your", rosterSize);
        fillTeamRoster(oppTeam, "your trade partner's", rosterSize);
        for (Player p : oppTeam.getRoster()){
            System.out.println(p);
        }
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
                    if (inputNum >= 0 && inputNum < 5) {
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

    public static void fillTeamRoster(Team team, String teamName, int rosterSize){
        System.out.println("Please enter all the players on " + teamName + " roster (including bench)");
        while (team.getRoster().size() != rosterSize){
            String playerName = scanner.nextLine();
            Player p = playerDatabase.getPlayerByName(playerName);
            if (p != null){
                team.addPlayerToRoster(p);
            }
            else
                System.out.println("Please enter a valid player or check spelling.");
        }
    }

    private static int getRosterSize(Team team){
        int rosterSize;
        System.out.println("How many players are on your roster including bench?");
        while(true){
            try {
                rosterSize = scanner.nextInt();
                if (rosterSize >= startingRosterSize(team) && rosterSize <= 30){
                    break;
                }
                System.out.println("Please enter a valid number.");
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
            }
        }
        scanner.nextLine();
        return rosterSize;
    }

    private static int startingRosterSize(Team team){
        int count = 0;
        for (int positionLimit : team.getRosterConstruction().values()){
            count += positionLimit;
        }
        return count;
    }
}
