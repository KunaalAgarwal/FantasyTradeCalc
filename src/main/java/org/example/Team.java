package org.example;

import java.util.*;

public class Team {
    String name;
    private ArrayList<Player> roster;
    private ArrayList<Player> startingRoster;
    private final HashMap<String, Integer> rosterConstruction;

    public Team(String name, HashMap<String,Integer> rosterConstruction){
        this.name = name;
        this.roster = new ArrayList<>();
        this.rosterConstruction = rosterConstruction;
        this.startingRoster = new ArrayList<>();
    }

    public void addPlayerToRoster(Player player){
        if (roster.contains(player)){
            throw new IllegalArgumentException("Player is already on roster");
        }
        roster.add(player);
    }

    public void removePlayerFromRoster(Player player){
        if (roster.contains(player)){
            roster.remove(player);
        }
        else
            throw new IllegalArgumentException("Player not found on roster");
    }
    public ArrayList<Player> getStartingRoster(){
        if (startingRoster.size() == 0){
            return null;
        }
        return startingRoster;
    }

    protected ArrayList<Player> fillStartingRoster(){
        for (String position : rosterConstruction.keySet()){
            List<Player> sortedPosPlayers = sortRoster(position);
            if (sortedPosPlayers.size() > 0) {
                for (int i = 0; i < rosterConstruction.get(position); i++) {
                    startingRoster.add(sortedPosPlayers.get(i));
                }
            }
        }
        return startingRoster;
    }
    protected List<Player> sortRoster(String position){
        List<Player> temp;
        if (position.equals("FLEX")){
            temp = new ArrayList<>(roster.stream().sorted(Comparator.comparing(Player::getProjection).reversed()).toList());
            temp.removeIf(p -> p.getPosition().equals("QB"));
        }
        else
            temp = new ArrayList<>(roster.stream().filter(player -> player.getPosition().equals(position)).sorted(Comparator.comparing(Player::getProjection).reversed()).toList());
        temp.removeIf(p -> startingRoster.contains(p));
        return temp;
    }

    public double getTotalProjection(){
        double totProj = 0;
        for (Player p : startingRoster){
            totProj += p.getProjection();
        }
        return totProj;
    }

    public double getAvgProj(){
        return getTotalProjection()/startingRoster.size();
    }

    public double getAvgInjuryRisk(){
        double totRisk = 0; int playersIncluded = 0;
        for (Player p : startingRoster){
            if (p.getInjuryRisk() != 0.0){
                totRisk += p.getInjuryRisk();
                playersIncluded += 1;
            }
        }
        return totRisk/playersIncluded;
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Integer> getRosterConstruction(){
        return rosterConstruction;
    }

    public ArrayList<Player> getRoster(){
        return roster;
    }
    @Override
    public String toString(){
        return "Team: " + name + "\n" + "Roster: " + roster.toString();
    }
}
