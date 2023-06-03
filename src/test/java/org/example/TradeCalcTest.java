package org.example;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class TradeCalcTest {

    HashMap<String, Integer> rosterCons;
    Player PatQB;
    Player JoshQB;
    Player GeorgeTE;
    Player TravisTE;
    Player TyreekWR;
    Team team;
    Team team2;
    Trade trade;
    PlayerDAO pd;
    static DatabaseManager dm;
    @BeforeEach
    public void setup() throws SQLException, IOException {
        dm = new DatabaseManager();
        pd = dm.createPlayerDAO();
        rosterCons = new HashMap<>();
        PatQB = pd.getPlayerByName("Patrick Mahomes");
        JoshQB = pd.getPlayerByName("Josh Allen");
        GeorgeTE = pd.getPlayerByName("George Kittle");
        TravisTE = pd.getPlayerByName("Travis Kelce");
        TyreekWR = pd.getPlayerByName("Tyreek Hill");
        team = new Team("Test", rosterCons);
        team2 = new Team("Test2", rosterCons);

        trade = new Trade(team, team2);
    }

    @AfterAll
    public static void closeDb() throws SQLException {
        dm.closeConnection();
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
        team.addPlayerToRoster(PatQB); team.addPlayerToRoster(TyreekWR); team.addPlayerToRoster(GeorgeTE);

        team2.addPlayerToRoster(JoshQB); team2.addPlayerToRoster(TravisTE);

        trade.assetsLost.add(TyreekWR); trade.assetsGained.add(TravisTE);
        TradeCalculator tc = new TradeCalculator(trade);
        assertEquals(team2, tc.getTradeWinner());
    }

    @Test
    public void getTradeWinner2f1(){
        rosterCons.put("FLEX", 1);
        team.addPlayerToRoster(GeorgeTE); team.addPlayerToRoster(TravisTE);
        team2.addPlayerToRoster(TyreekWR);
        trade.assetsGained.add(TyreekWR);
        trade.assetsLost.add(GeorgeTE); trade.assetsLost.add(TravisTE);
        TradeCalculator tc = new TradeCalculator(trade);
        assertEquals(team, tc.getTradeWinner());
    }

    @Test
    public void getTradeWinner2flex2f1(){
        rosterCons.put("FLEX", 2);
        team.addPlayerToRoster(GeorgeTE); team.addPlayerToRoster(TravisTE); team.addPlayerToRoster(TyreekWR);
        team2.addPlayerToRoster(pd.getPlayerByName("Deandre Hopkins"));
        team2.addPlayerToRoster(pd.getPlayerByName("Saquon Barkley"));
        team2.addPlayerToRoster(pd.getPlayerByName("Derrick Henry"));
        trade.assetsGained.add(pd.getPlayerByName("Deandre Hopkins"));
        trade.assetsLost.add(GeorgeTE);
        TradeCalculator tc = new TradeCalculator(trade);
        tc.getTradeWinner();
    }

    @Test
    public void fillSrFLEXExecuteTrade(){
        rosterCons.put("FLEX",2);
        team.addPlayerToRoster(GeorgeTE);
        team.addPlayerToRoster(TravisTE);
        team.addPlayerToRoster(TyreekWR);

        team2.addPlayerToRoster(pd.getPlayerByName("Deandre Hopkins"));
        team2.addPlayerToRoster(pd.getPlayerByName("Saquon Barkley"));
        team2.addPlayerToRoster(pd.getPlayerByName("Derrick Henry"));

        trade.assetsGained.add(pd.getPlayerByName("Deandre Hopkins"));
        trade.assetsLost.add(TyreekWR);
        trade.executeTrade();

        ArrayList<Player> actualSR = new ArrayList<>();
        actualSR.add(TravisTE); actualSR.add(pd.getPlayerByName("Deandre Hopkins"));
        assertIterableEquals(actualSR,team.fillStartingRoster());
    }
}
