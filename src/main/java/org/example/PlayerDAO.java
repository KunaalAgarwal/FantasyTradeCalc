package org.example;

import java.sql.*;

public class PlayerDAO {
    private final Connection con;
    private final ApiParser parser;
    public PlayerDAO(Connection connection){
        this.con = connection;
        parser = new ApiParser();
    }

    public Player getPlayerByName(String playerName){
        PreparedStatement stmt;
        ResultSet rs;
        Player player = null;
        try {
            stmt = con.prepareStatement("SELECT * FROM Players WHERE Name = ?");
            stmt.setString(1, playerName);
            rs = stmt.executeQuery();
            if (rs.next()){
                player = new Player(rs.getString("Name"),rs.getString("Team"),
                        rs.getString("Position"),rs.getDouble("Season_Projection"),
                        rs.getInt("TIER"));
                player.setInjuryRisk(rs.getDouble("Injury_Risk"));
            }
            rs.close();
            stmt.close();
        }catch (SQLException e){
            System.out.println("Player was not found in the database");
        }
        return player;
    }

    public void addAllPlayers(){
        for (Player p : parser.getPlayerList()){
            save(p);
        }
    }

    public void save(Player player){
        PreparedStatement stmt;
        if (playerInDatabase(player)){
            throw new IllegalArgumentException("Player already present in database");
        }
        try {
            stmt = con.prepareStatement("INSERT INTO Players(Name, Team, Position, Season_Projection, Tier, Injury_Risk) VALUES(?,?,?,?,?,?)");
            stmt.setString(1,player.getName());
            stmt.setString(2,player.getProTeam());
            stmt.setString(3,player.getPosition());
            stmt.setDouble(4,player.getProjection());
            stmt.setInt(5,player.getTier());
            stmt.setDouble(6,player.getInjuryRisk());
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e){
            System.out.println("An error occurred in the database");
        }
    }

    public void delete(Player player){
        PreparedStatement stmt;
        if (!playerInDatabase(player)){
            throw new IllegalArgumentException("Player isn't in the database");
        }
        try{
            stmt = con.prepareStatement("DELETE FROM Players WHERE Name = ?");
            stmt.setString(1,player.getName());
        }
        catch (SQLException e) {
            System.out.println("An error occurred in the database");
        }
    }

    private boolean playerInDatabase(Player player){
        PreparedStatement stmt;
        ResultSet rs;
        try {
            stmt = con.prepareStatement("SELECT * FROM Players WHERE Name = ?");
            stmt.setString(1, player.getName());
            rs = stmt.executeQuery();
            if (rs.next()){
                return true;
            }
            else {
                stmt.close();
                rs.close();
            }
        }
        catch (SQLException e){
            System.out.println("An error occurred in the database");
        }
        return false;
    }
}
