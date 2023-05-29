package org.example;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.IOException;
import java.sql.SQLException;

public class Main {
    static TradeCalculator tc;
    static Trade trade;
    static Team userTeam;
    static Team oppTeam;
    static PlayerDAO playerDatabase;
    static String[] positionList;
    static Scanner scanner;

    public static void main(String[] args) {
        System.out.println("Welcome to the Fantasy Trade Calculator!");
        initialize();
        HashMap<String, Integer> rosCons = setRosConstruction();
        userTeam = new Team ("User's Team", rosCons);
        oppTeam = new Team ("Trade Partner's Team", rosCons);
        trade = new Trade(userTeam, oppTeam);
        int rosterSize =  getInt("How many players are on your roster including bench?", startingRosterSize(),30);
        fillTeamRoster(userTeam, "your", rosterSize);
        fillTeamRoster(oppTeam, "your trade partner's", rosterSize);
//        for (Player p : userTeam.getRoster()){
//            System.out.println(p);
//        }
//        for (Player p : oppTeam.getRoster()){
//            System.out.println(p);
//        }
        getTradeInfo();

//        for (Player p : trade.assetsGained){
//            System.out.println(p);
//        }
//        for (Player p : trade.assetsLost){
//            System.out.println(p);
//        }

        try{
            System.out.println("Evaluating Trade....");
            Thread.sleep(1000);
            System.out.println("Referencing Database....");
            Thread.sleep(1000);
            System.out.println("Exploring alternate dimensions....");
            Thread.sleep(1000);
            System.out.println("Solving world hunger....");
            Thread.sleep(1000);
            System.out.println(tc.getTradeWinner());
        } catch (InterruptedException e){
            System.out.println("Uh oh");
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

    private static HashMap<String, Integer> setRosConstruction() {
        HashMap<String, Integer> rosCons = new HashMap<>();
        for (String pos : positionList) {
            rosCons.put(pos, getInt("Please enter the number of " + pos + "s on your starting roster.", 0, 5));
        }
        return rosCons;
    }

    private static void fillTeamRoster(Team team, String teamName, int rosterSize){
        System.out.println("Please enter all the players on " + teamName + " roster (including bench).");
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

    private static void getTradeInfo(){
        int numPlayersGained = getInt("Please enter the number of players you've gained from this trade.",1, startingRosterSize());
        int numPlayersLost = getInt("Please enter the number of players you've traded away.", 1, userTeam.getRoster().size());
        System.out.println("Please enter all the players you've gained from this trade.");
        addPlayersInTrade(numPlayersGained,trade.assetsGained);
        System.out.println("Please enter all the players you've traded away.");
        addPlayersInTrade(numPlayersLost, trade.assetsLost);
        tc = new TradeCalculator(trade);
    }

    private static void addPlayersInTrade(int numPlayers, ArrayList<Player> playerList){
        while (playerList.size() != numPlayers){
            String playerName = scanner.nextLine();
            Player p = playerDatabase.getPlayerByName(playerName);
            if (p != null){
                playerList.add(p);
            }
            else
                System.out.println("Please enter a valid player or check spelling.");
        }
    }

    private static int getInt(String outputText, int lowerBound, int upperBound){
        int i;
        System.out.println(outputText);
        while(true){
            try {
                i = scanner.nextInt();
                if (i >= lowerBound && i <= upperBound){
                    break;
                }
                System.out.println("Please enter a valid number.");
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine();
            }
        }
        scanner.nextLine();
        return i;
    }

    private static int startingRosterSize(){
        int count = 0;
        for (int positionLimit : userTeam.getRosterConstruction().values()){
            count += positionLimit;
        }
        return count;
    }

}
