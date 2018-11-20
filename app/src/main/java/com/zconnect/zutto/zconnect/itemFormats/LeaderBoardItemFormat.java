package com.zconnect.zutto.zconnect.itemFormats;

public class LeaderBoardItemFormat {
    private String rank,name, userPoints, userUID, image;

    Integer points;

    public LeaderBoardItemFormat(String rank, String name, String userPoints, String userUID, String image) {
        this.rank = rank;
        this.name = name;
        this.userPoints = userPoints;
        this.userUID = userUID;
        this.image = image;
    }

    public LeaderBoardItemFormat() {
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserPoints() {
        return userPoints;
    }

    public void setUserPoints(String userPoints) {
        this.userPoints = userPoints;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
