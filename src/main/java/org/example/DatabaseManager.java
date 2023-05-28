package org.example;

import java.io.IOException;
import java.sql.*;


public class DatabaseManager {
    private static final String dbName = "FantasyTradeCalc.sqlite3";
    private static final String dbUrl = "jdbc:sqlite:" + dbName;
    private final Connection connection;

    public DatabaseManager() throws SQLException, IOException {
        connection = createConnection();
        initializeDatabase();
    }

    private Connection createConnection() throws SQLException{
        return DriverManager.getConnection(dbUrl);
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    public PlayerDAO createPlayerDAO(){
        return new PlayerDAO(connection);
    }

    private void initializeDatabase() {
        try {
            Statement statement = connection.createStatement();
            createPlayerTable(statement);
            statement.close();
        } catch (SQLException e) {
            System.out.println("Database could not be created.");
        }
    }
    private void createPlayerTable(Statement stmt) throws SQLException{
        String createPlayersSQL = "CREATE TABLE IF NOT EXISTS Players(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "Name VARCHAR(255) NOT NULL," +
                "Team VARCHAR(3) NOT NULL," +
                "Position VARCHAR(2) NOT NULL, " +
                "Season_Projection REAL NOT NULL," +
                "Tier INTEGER NOT NULL," +
                "Injury_Risk REAL)";
        stmt.executeUpdate(createPlayersSQL);
    }

    public static void main(String[] args){
        try{
            DatabaseManager dm = new DatabaseManager();
            PlayerDAO playerDAO = dm.createPlayerDAO();
            playerDAO.addAllPlayers();
            System.out.println(playerDAO.getPlayerByName("Patrick Mahomes"));
            dm.closeConnection();
        }
        catch(SQLException | IOException e){
            e.printStackTrace();
        }
    }
}