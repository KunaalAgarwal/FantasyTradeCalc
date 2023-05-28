package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ApiParser extends UrlReader{
    private JSONObject json;
    private final String[] posList = {"QB", "RB", "WR", "TE"};
    private ArrayList<Player> playerList;

    public ApiParser(){
        fillPlayerList();
    }

    private void fillPlayerList(){
        getPlayerProjections();
        getPlayerTiers();
        getPlayerInjuryRisk();
    }

    public ArrayList<Player> getPlayerList(){
        return playerList;
    }

    private void configure(String url){
        try {
            URL projectionUrl = new URL(url);
            json = convertToJSON(projectionUrl);
        }
        catch (IOException e){
            System.out.println("Invalid URL");
        }
    }

    private void getPlayerProjections(){
        configure("https://api.fantasynerds.com/v1/nfl/ros?apikey=TEST");
        JSONObject playerPos = json.getJSONObject("projections");
        ArrayList<Player> players = new ArrayList<>();
        for (String pos : posList){
            JSONArray playersByPos = playerPos.getJSONArray(pos);
            for (int i = 0; i < playersByPos.length(); i++){
                JSONObject p = playersByPos.getJSONObject(i);
                Double projection = Double.parseDouble(p.getString("proj_pts"));
                Player player = new Player(p.getString("name"), p.getString("team"), p.getString("position"));
                player.setProjection(projection);
                player.setTier(0);
                players.add(player);
            }
        }
        playerList = players;
    }

    private void getPlayerTiers(){
        configure("https://api.fantasynerds.com/v1/nfl/tiers?apikey=TEST&format=");
        JSONObject playerTier = json.getJSONObject("tiers");
        for (String pos : posList){
            JSONObject playersByTier = playerTier.getJSONObject(pos);
            for (int i = 1; i < playersByTier.names().length() + 1; i++){
                JSONArray playersInTier = playersByTier.getJSONArray(String.valueOf(i));
                for (int k = 0; k < playersInTier.length(); k++){
                    JSONObject p = playersInTier.getJSONObject(k);
                    Player player = new Player(p.getString("name"), p.getString("team"),p.getString("position"));
                    int tier = Integer.parseInt(playersInTier.getJSONObject(k).getString("tier"));
                    int index = playerList.indexOf(player);
                    playerList.get(index).setTier(tier);
                }
            }
        }
    }

    private void getPlayerInjuryRisk(){
        configure("https://api.fantasynerds.com/v1/nfl/draft-rankings?apikey=TEST&format=");
        JSONArray players = json.getJSONArray("players");
        for (int i = 0; i < players.length(); i++){
            JSONObject p = players.getJSONObject(i);
            double injuryRisk = 0;
            Player player = new Player(p.getString("name"), p.getString("team"), p.getString("position"));
            switch (p.getString("injury_risk")) {
                case "high" -> injuryRisk = 5.0;
                case "medium" -> injuryRisk = 2.5;
                case "low" -> injuryRisk = 1.0;
            }
            int index = playerList.indexOf(player);
            if (index >= 0) {
                playerList.get(index).setInjuryRisk(injuryRisk);
            }
        }
    }
}
