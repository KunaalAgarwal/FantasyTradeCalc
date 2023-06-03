package org.example;

import java.util.ArrayList;
public class Trade {
    Team team1;
    Team team2;
    ArrayList<Player> assetsLost; //in terms of team 1
    ArrayList<Player> assetsGained;

    public Trade(Team team1, Team team2){
        this.team1 = team1;
        this.team2 = team2;
        assetsGained = new ArrayList<>();
        assetsLost = new ArrayList<>();
    }

    public void executeTrade(){
        for (Player p : assetsLost){
            if (team1.getRoster().contains(p) && !team2.getRoster().contains(p)) {
                team1.removePlayerFromRoster(p);
                team2.addPlayerToRoster(p);
            }
        }
        for (Player a : assetsGained){
            if (team2.getRoster().contains(a) && !team1.getRoster().contains(a)) {
                team2.removePlayerFromRoster(a);
                team1.addPlayerToRoster(a);
            }
        }
    }
    @Override
    public String toString(){
        return "Assets Lost: " + assetsLost.toString() + "\nAssets Gained: " + assetsGained.toString();
    }

}
