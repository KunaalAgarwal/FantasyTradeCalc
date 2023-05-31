package org.example;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

public class TradeCalcTest {

    HashMap<String, Integer> rosterCons;
    Player PatQB;
    Player JoshQB;
    Player GeorgeTE;
    Player TravisTE;
    Player JustinWR;
    Team team;
    Team team2;
    Trade trade;
    @BeforeEach
    public void setup(){
        rosterCons = new HashMap<>();
        PatQB = new Player ("Pat", "KC", "QB", 100.0, 1);
        JoshQB = new Player ("Josh", "BUF", "QB", 95.0,1);
        GeorgeTE = new Player ("George", "SF", "TE", 70.0, 1);
        TravisTE = new Player ("Travis", "KC", "TE", 85.0, 1);
        JustinWR = new Player ("Justin", "MIN", "WR", 110.0, 1);

        team = new Team("Test", rosterCons);
        team2 = new Team("Test2", rosterCons);

        trade = new Trade(team, team2);
    }

    @Test
    public void getTradeWinnerTest(){
        rosterCons.put("QB",1);
        team.addPlayerToRoster(PatQB); team2.addPlayerToRoster(JoshQB);

        trade.assetsLost.add(PatQB); trade.assetsGained.add(JoshQB);
        TradeCalculator tc = new TradeCalculator(trade);
        assertEquals(team2,tc.getTradeWinner());
    }

    @Test
    public void getTradeWinnerTwoPos(){
        rosterCons.put("QB",1); rosterCons.put("FLEX",1);
        team.addPlayerToRoster(PatQB); team.addPlayerToRoster(JustinWR); team.addPlayerToRoster(GeorgeTE);

        team2.addPlayerToRoster(JoshQB); team2.addPlayerToRoster(TravisTE);

        trade.assetsLost.add(JustinWR); trade.assetsGained.add(TravisTE);
        TradeCalculator tc = new TradeCalculator(trade);
        assertEquals(team2, tc.getTradeWinner());
    }

    @Test
    public void getTradeWinner2f1(){
        rosterCons.put("FLEX", 1);
        team.addPlayerToRoster(GeorgeTE); team.addPlayerToRoster(TravisTE);
        team2.addPlayerToRoster(JustinWR);
        trade.assetsGained.add(JustinWR);
        trade.assetsLost.add(GeorgeTE); trade.assetsLost.add(TravisTE);
        TradeCalculator tc = new TradeCalculator(trade);
        assertEquals(team, tc.getTradeWinner());
    }


}
