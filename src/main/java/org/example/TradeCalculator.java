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
        double postTotalProj = team.getTotalProjection();
        double prevTotalProj = prevTeam.getTotalProjection();
        if (Double.isNaN(postTotalProj)){
            postTotalProj = 0;
        }
        else if (Double.isNaN(prevTotalProj)){
            prevTotalProj = 0;
        }
        double projDif = postTotalProj - prevTotalProj;
        return normalizeDif(projDif, 40);
    }

    private double getUpsideGrades(Team team, Team prevTeam){
        ArrayList<Player> playersGained = trade.assetsGained;
        if (team.getName().equals(trade.team2.getName())){
            playersGained = trade.assetsLost;
        }
        double playersGainedUpside = average(playersGained);
        double prevTeamUpside = prevTeam.getAvgProj();
        if (Double.isNaN(playersGainedUpside)){
            playersGainedUpside = 0;
        }
        else if (Double.isNaN(prevTeamUpside)){
            prevTeamUpside = 0;
        }
        double upsideDif = playersGainedUpside - prevTeamUpside;
        return normalizeDif(upsideDif, 25);
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        double team1WeightedGrade = getWeightedGrade(trade.team1, prevTeam1);
        double team2WeightedGrade = getWeightedGrade(trade.team2, prevTeam2);

        sb.append("Your team's overall trade grade: ").append(team1WeightedGrade).append("/10\n");
        sb.append("Your team's projection trade grade: ").append(getProjGrades(trade.team1, prevTeam1)).append("/10\n");
        sb.append("Your team's upside trade grade: ").append(getUpsideGrades(trade.team1, prevTeam1)).append("/10\n");

        double team1InjuryRiskGrade = getInjuryRiskGrade(trade.team1, prevTeam1);
        if (!Double.isNaN(team1InjuryRiskGrade)) {
            sb.append("Your team's injury risk trade grade: ").append(team1InjuryRiskGrade).append("/10\n");
        }

        sb.append("---------\n");

        sb.append("Trade partner's overall trade grade: ").append(team2WeightedGrade).append("/10\n");
        sb.append("Trade partner's projection trade grade: ").append(getProjGrades(trade.team2, prevTeam2)).append("/10\n");
        sb.append("Trade partner's upside trade grade: ").append(getUpsideGrades(trade.team2, prevTeam2)).append("/10\n");

        double team2InjuryRiskGrade = getInjuryRiskGrade(trade.team2, prevTeam2);
        if (!Double.isNaN(team2InjuryRiskGrade)) {
            sb.append("Trade partner's injury risk trade grade: ").append(team2InjuryRiskGrade).append("/10\n");
        }

        sb.append("\nWinner: ").append(getTradeWinner());

        return sb.toString();
    }
}
