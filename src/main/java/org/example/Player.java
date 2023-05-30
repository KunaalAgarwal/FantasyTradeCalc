package org.example;

public class Player {
    String name;
    String proTeam;
    String position;
    Double injuryRisk;
    private double projection;
    private int tier;

    public Player(String name, String proTeam, String position){
        this.name = name;
        this.proTeam = proTeam;
        this.position = position;
        this.injuryRisk = 0.0;
    }

    public Player(String name, String proTeam, String position, Double projection, int tier){
        this.name = name;
        this.proTeam = proTeam;
        this.position = position;
        this.projection = projection;
        this.tier = tier;
        this.injuryRisk = 0.0;
    }

    public String getName() {
        return name;
    }
    public String getProTeam() {
        return proTeam;
    }
    public String getPosition() {
        return position;
    }
    public double getProjection(){
        return projection;
    }
    public double getInjuryRisk(){
        return injuryRisk;
    }
    public void setProjection(Double projection){
        this.projection = projection;
    }
    public void setName(String name){
        this.name = name;
    }

    public int getTier() {
        return tier;
    }
    public void setTier(int tier) {
        this.tier = tier;
    }
    public void setInjuryRisk(double risk){
        this.injuryRisk = risk;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Player a)) {
            return false;
        }
        return a.name.equals(this.name);
    }

    @Override
    public String toString(){
        return name + ", " + position + ", " + proTeam;
    }

}