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
            System.out.println("Your team's overall trade grade: "  + getWeightedGrade(trade.team1, prevTeam1) + "/10");
            System.out.println("Your team's projection trade grade: " + getProjGrades(trade.team1,prevTeam1) + "/10");
            System.out.println("Your team's upside trade grade: " + getUpsideGrades(trade.team1,prevTeam1) + "/10");
            if (!Double.isNaN(getInjuryRiskGrade(trade.team1,prevTeam1))) {
                System.out.println("Your team's injury risk trade grade: " + getInjuryRiskGrade(trade.team1, prevTeam1) + "/10");
            }
            System.out.println("---------");

            System.out.println("Trade partner's overall trade grade: "  + getWeightedGrade(trade.team2, prevTeam2) + "/10");
            System.out.println("Trade partner's projection trade grade: " + getProjGrades(trade.team2,prevTeam2) + "/10");
            System.out.println("Trade partner's upside trade grade: " + getUpsideGrades(trade.team2,prevTeam2) + "/10");
            if (!Double.isNaN(getInjuryRiskGrade(trade.team2,prevTeam2))) {
                System.out.println("Your team's injury risk trade grade: " + getInjuryRiskGrade(trade.team2, prevTeam2) + "/10");
            }
            System.out.println(" ");
            System.out.println("Winner: ");
            return trade.team2;
        }
    }

    private double getWeightedGrade(Team team, Team prevTeam){
        if (Double.isNaN(getInjuryRiskGrade(team, prevTeam))){
            return getProjGrades(team,prevTeam) * 0.6 + getUpsideGrades(team, prevTeam) * 0.4;
        }
        return getProjGrades(team,prevTeam) * 0.5 + getUpsideGrades(team, prevTeam) * 0.3 + getInjuryRiskGrade(team, prevTeam) * 0.2;
    }

    private double getProjGrades(Team team, Team prevTeam){
        double projDif = team.getTotalProjection() - prevTeam.getTotalProjection();
        return normalizeDif(projDif, 50);
    }


    private double getUpsideGrades(Team team, Team prevTeam){
        ArrayList<Player> playersGained = trade.assetsGained;
        if (team.getName().equals(trade.team2.getName())){
            playersGained = trade.assetsLost;
        }
        double upsideDif = average(playersGained) - prevTeam.getAvgProj();
        return normalizeDif(upsideDif, 35);
    }

    private double average (ArrayList<Player> p) {
        double count = 0;
        for(Player a : p) {
            count += a.getProjection();
        }
        return count/p.size();
    }
    private double normalizeDif(double projDif, int nValue){
        /* formula for normalization from [x,x] to [1,10] each player on sr has range of [-30,30]
        normalizedValue = ((value - originalMin) / (originalMax - originalMin)) * (newMax - newMin) + newMin */
        int normalizationFactor = nValue * prevTeam1.getStartingRoster().size();
        double normalizedValue = ((projDif + normalizationFactor) / (normalizationFactor * 2.0)) * 9.0 + 1.0;
        return Math.max(1.0, Math.min(10.0, normalizedValue)); // Clamp the value within [1, 10]
    }

    private double getInjuryRiskGrade(Team team, Team prevTeam){
        // lower risk equals higher grade
        double injuryRisk = team.getAvgInjuryRisk() - prevTeam.getAvgInjuryRisk();
        double grade = 10 - normalizeDif(injuryRisk, 2);
        if (grade == 0.0){
            return 1.0;
        }
        return grade;
    }
}
