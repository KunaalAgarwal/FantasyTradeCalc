package org.example;

import java.util.ArrayList;

public class TradeCalculator {
    Trade trade;
    Team prevTeam1;
    Team prevTeam2;
    public TradeCalculator(Trade trade) {
        this.trade = trade;
        setPrevTeams();
        trade.executeTrade();
    }

    private void setPrevTeams() {
        prevTeam1 = new Team(trade.team1.getName(), trade.team1.getRosterConstruction());
        for (Player p : trade.team1.getRoster()){
            prevTeam1.addPlayerToRoster(p);
        }
        prevTeam1.getStartingRoster();
        prevTeam2 = new Team(trade.team2.getName(), trade.team2.getRosterConstruction());
        for (Player p : trade.team2.getRoster()){
            prevTeam2.addPlayerToRoster(p);
        }
        prevTeam2.getStartingRoster();
    }

    public Team getTradeWinner(){
        if (getWeightedGrade(trade.team1, prevTeam1) >= getWeightedGrade(trade.team2, prevTeam2)){
            return trade.team1;
        }
        else {
//            System.out.println(getWeightedGrade(trade.team1, prevTeam1));
//            System.out.println(getProjGrades(trade.team1,prevTeam1));
//            System.out.println(getUpsideGrades(trade.team1,prevTeam1));
//
//            System.out.println("----");
//            System.out.println(getWeightedGrade(trade.team2, prevTeam2));
//            System.out.println(getProjGrades(trade.team2,prevTeam2));
//            System.out.println(getUpsideGrades(trade.team2,prevTeam2));

            return trade.team2;
        }
    }

    private double getWeightedGrade(Team team, Team prevTeam){
        return getProjGrades(team,prevTeam) * 0.5 + getUpsideGrades(team, prevTeam) * 0.3 + getInjuryRiskGrade(team, prevTeam) * 0.2;
    }

    private double getProjGrades(Team team, Team prevTeam){
        double projDif = team.getTotalProjection() - prevTeam.getTotalProjection();
        return normalizeDif(projDif, 35);
    }

    private double normalizeDif(double projDif, int nValue){
        /* formula for normalization from [x,x] to [1,10] each player on sr has range of [-30,30]
        normalizedValue = ((value - originalMin) / (originalMax - originalMin)) * (newMax - newMin) + newMin */
        int normalizationFactor = nValue * prevTeam1.getStartingRoster().size();
        return ((projDif + normalizationFactor) / (normalizationFactor  * 2)) * 10;
    }

    private double getUpsideGrades(Team team, Team prevTeam){
        ArrayList<Player> playersGained = trade.assetsGained;
        if (team.getName().equals(trade.team2.getName())){
            playersGained = trade.assetsLost;
        }
        double upsideDif = average(playersGained) - prevTeam.getAvgProj();
        return normalizeDif(upsideDif, 25);
    }

    private double average (ArrayList<Player> p) {
        double count = 0;
        for(Player a : p) {
            count += a.getProjection();
        }
        return count/p.size();
    }

    private double getInjuryRiskGrade(Team team, Team prevTeam){
        // lower risk equals higher grade
        double injuryRisk = team.getAvgInjuryRisk() - prevTeam.getAvgInjuryRisk();
        return 10 - normalizeDif(injuryRisk, 2);
    }
}
