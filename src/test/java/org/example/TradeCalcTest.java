package org.example;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;


import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
public class TradeCalcTest {
    Team team;
    Team team2;
    ApiParser parser;
    Player playerA;
    Player playerB;
    Player playerC;
    Player playerD;
    Player playerE;
    HashMap<String, Integer> rosterCons;

    @BeforeEach
    public void setup(){
        rosterCons = new HashMap<>();
//        rosterCons.put("QB",1); rosterCons.put("TE",1); rosterCons.put("FLEX",1);

        playerA = new Player ("Pat", "KC", "QB", 100.0, 1);
        playerB = new Player ("Josh", "BUF", "QB", 95.0,1);
        playerC = new Player ("George", "SF", "TE", 70.0, 1);
        playerD = new Player ("Travis", "KC", "TE", 85.0, 1);
        playerE = new Player ("Justin", "MIN", "WR", 110.0, 1);

        team = new Team("Test", rosterCons); team2 = new Team("Test2", rosterCons);

        team.addPlayerToRoster(playerA); team.addPlayerToRoster(playerB);
        team.addPlayerToRoster(playerC); team.addPlayerToRoster(playerD);
    }

    public static void main (String[] args){
        HashMap<String, Integer> rosterCon = new HashMap<>();
        rosterCon.put("QB",1);
        Team team1 = new Team("1",rosterCon);
        Team team2 = new Team("2",rosterCon);

        Player playerA = new Player ("Pat", "KC", "QB", 416.7, 1);
        Player playerB = new Player ("Josh", "BUF", "QB", 401.7,1);
        playerB.setInjuryRisk(2.5); playerA.setInjuryRisk(2.5);
        team1.addPlayerToRoster(playerA);
        team2.addPlayerToRoster(playerB);

        Trade trade = new Trade(team1,team2);

        trade.assetsLost.add(playerA); trade.assetsGained.add(playerB);
        TradeCalculator tc = new TradeCalculator(trade);
        System.out.println(tc.getTradeWinner());
    }

    @Test
    public void testProjParser(){
        parser = new ApiParser();
        ArrayList<Player> players = parser.getPlayerList();
        Player a = new Player ("Patrick Mahomes", "KC", "QB");
        assertEquals(a,players.get(0));
    }

    @Test
    public void testTierParser() {
        parser = new ApiParser();
        ArrayList<Player> players = parser.getPlayerList();
        assertEquals(1,players.get(0).getTier());
    }
    @Test
    public void testInjuryParser(){
        parser = new ApiParser();
        ArrayList<Player> players = parser.getPlayerList();
        assertEquals(2.5,players.get(0).getInjuryRisk());
    }

    @Test
    public void sortRoster(){
        List<Player> testSortedQBs = team.sortRoster("QB");
        List<Player> actualSortedQBs = new ArrayList<>();
        actualSortedQBs.add(playerA); actualSortedQBs.add(playerB);
        assertIterableEquals(testSortedQBs, actualSortedQBs);
    }

    @Test
    public void sortRosterFLEX(){
        List<Player> testSortedTeam = team.sortRoster("FLEX");
        List<Player> actualSortedTeam = new ArrayList<>();
        actualSortedTeam.add(playerD); actualSortedTeam.add(playerC);
        assertIterableEquals(testSortedTeam,actualSortedTeam);
    }

    @Test
    public void fillStartingRoster(){
        rosterCons.put("QB", 1);
        team2.addPlayerToRoster(playerA);
        team2.addPlayerToRoster(playerB);
        ArrayList<Player> actualSr = new ArrayList<>();
        actualSr.add(playerA);
        assertIterableEquals(actualSr,team2.getStartingRoster());
    }

    @Test
    public void fillSROneQb(){
        rosterCons.put("QB",1);
        team2.addPlayerToRoster(playerA);
        ArrayList<Player> actualSr = new ArrayList<>();
        actualSr.add(playerA);
        assertIterableEquals(actualSr,team2.getStartingRoster());
    }

    @Test
    public void fillStartingRosterWithFLEX(){
        rosterCons.put("QB",1); rosterCons.put("TE",1);
        List<Player> actualStartingRoster = new ArrayList<>();
        actualStartingRoster.add(playerA); actualStartingRoster.add(playerD);
        assertIterableEquals(actualStartingRoster,team.getStartingRoster());
    }

    @Test
    public void executeTradeTest(){
        team2.addPlayerToRoster(playerE);
        Trade trade = new Trade(team,team2);
        trade.assetsGained.add(playerE); //team 1 gains justin jefferson
        trade.assetsLost.add(playerD); //team 1 loses kelce
        trade.executeTrade();
        ArrayList<Player> team2PostTrade = new ArrayList<>();
        team2PostTrade.add(playerD);
        assertTrue(team.getRoster().contains(playerE));
        assertIterableEquals(team2PostTrade, trade.team2.getRoster());
    }

    @Test
    public void getTradeWinnerTest(){
        rosterCons.put("QB",1);
        team.getRoster().clear(); team2.getRoster().clear();
        team.addPlayerToRoster(playerA); team2.addPlayerToRoster(playerB);
        Trade trade = new Trade(team,team2);

        trade.assetsLost.add(playerA); trade.assetsGained.add(playerB);
        TradeCalculator tc = new TradeCalculator(trade);
        team.getStartingRoster(); team2.getStartingRoster();

        assertEquals(team2,tc.getTradeWinner());
    }
}