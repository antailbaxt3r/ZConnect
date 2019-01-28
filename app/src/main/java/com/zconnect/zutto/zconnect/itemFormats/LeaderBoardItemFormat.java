package com.zconnect.zutto.zconnect.itemFormats;

public class LeaderBoardItemFormat {
    private String rank,name, userUID, image;
    private int userPointsNum;

    public LeaderBoardItemFormat(String rank, String name, int userPointsNum, String userUID, String image) {
        this.rank = rank;
        this.name = name;
        this.userPointsNum = userPointsNum;
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

    public int getUserPointsNum() {
        return userPointsNum;
    }

    public void setUserPointsNum(int userPointsNum) {
        this.userPointsNum = userPointsNum;
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
