package org.example;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.io.IOException;
import java.sql.SQLException;

public class Main {
    static TradeCalculator tradeCalculator;
    static Trade trade;
    static Team userTeam;
    static Team oppTeam;
    static PlayerDAO playerDatabase;
    static String[] positionList;
    static Scanner scanner;
    static DatabaseManager dm;

    public static void main(String[] args) {
        System.out.println("Welcome to the Fantasy Trade Calculator!");
        initialize();
        int rosterSize =  getInt("How many players are on your roster including bench?", startingRosterSize(),30);
        fillTeamRoster(userTeam, "your", rosterSize);
        fillTeamRoster(oppTeam, "your trade partner's", rosterSize);
        getTradeInfo();
        try {
            dm.closeConnection();
        } catch (SQLException e) {
            System.out.println("There was a fatal error in the program, please rerun.");
        }
        executeTradeCalculator();
    }

    public static void initialize(){
        try {
            scanner = new Scanner(System.in);
            dm = new DatabaseManager();
            playerDatabase = dm.createPlayerDAO();
            positionList = new String[]{"QB", "RB", "WR", "TE"};
            HashMap<String, Integer> rosCons = setRosConstruction();
            userTeam = new Team ("User's Team", rosCons);
            oppTeam = new Team ("Trade Partner's Team", rosCons);
            trade = new Trade(userTeam, oppTeam);
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
                if (!userTeam.getRoster().contains(p) && !oppTeam.getRoster().contains(p)){
                    team.addPlayerToRoster(p);
                }
                else
                    System.out.println("This player is already on a roster. Please enter a valid player.");
            }
            else
                System.out.println("Please enter a valid player or check spelling.");
        }
    }

    private static void getTradeInfo(){
        int numPlayersGained = getInt("Please enter the number of players you've gained from this trade.",0, startingRosterSize());
        int numPlayersLost = getInt("Please enter the number of players you've traded away.", 0, userTeam.getRoster().size());
        System.out.println("Please enter all the players you've gained from this trade.");
        addPlayersInTrade(numPlayersGained,trade.assetsGained, oppTeam);
        System.out.println("Please enter all the players you've traded away.");
        addPlayersInTrade(numPlayersLost, trade.assetsLost, userTeam);
        tradeCalculator = new TradeCalculator(trade);
    }

    private static void addPlayersInTrade(int numPlayers, ArrayList<Player> playerList, Team tradePartnerTeam){
        while (playerList.size() != numPlayers){
            String playerName = scanner.nextLine();
            Player p = playerDatabase.getPlayerByName(playerName);
            if (p != null && tradePartnerTeam.getRoster().contains(p)){
                playerList.add(p);
            }
            else
                System.out.println("Please enter a valid player or check spelling.");
        }
    }

    private static void executeTradeCalculator(){
        scanner.close();
        try{
            System.out.println("\nEvaluating Trade....");
            Thread.sleep(1000);
            System.out.println("Referencing Database....");
            Thread.sleep(1000);
            System.out.println("Exploring alternate dimensions....");
            Thread.sleep(1000);
            System.out.println("Solving world hunger....\n");
            Thread.sleep(1000);
            System.out.println("\n" + tradeCalculator);
        } catch (InterruptedException e){
            System.out.println("Please rerun the program, a fatal error occurred.");
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
