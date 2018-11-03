package com.zconnect.zutto.zconnect.itemFormats;

public class LeaderBoardItemFormat {
    private String rank,name, points, userUID, image;

    public LeaderBoardItemFormat(String rank, String name, String points, String userUID, String image) {
        this.rank = rank;
        this.name = name;
        this.points = points;
        this.userUID = userUID;
        this.image = image;
    }

    public LeaderBoardItemFormat() {
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

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
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
