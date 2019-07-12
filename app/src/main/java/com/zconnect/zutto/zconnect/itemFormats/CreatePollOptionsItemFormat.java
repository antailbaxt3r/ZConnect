package com.zconnect.zutto.zconnect.itemFormats;

public class CreatePollOptionsItemFormat {
    private String optionA,optionB,optionC;
    private int optionACount,optionBCount,optionCCount;

    public CreatePollOptionsItemFormat(){}

    public CreatePollOptionsItemFormat(String optionA,int optionACount,String optionB,int optionBCount,String optionC,int optionCCount){
        this.optionA = optionA;
        this.optionACount = optionACount;
        this.optionB = optionB;
        this.optionBCount = optionBCount;
        this.optionC = optionC;
        this.optionCCount = optionCCount;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }


    public int getOptionACount() {
        return optionACount;
    }

    public void setOptionACount(int optionACount) {
        this.optionACount = optionACount;
    }

    public int getOptionBCount() {
        return optionBCount;
    }

    public void setOptionBCount(int optionBCount) {
        this.optionBCount = optionBCount;
    }

    public int getOptionCCount() {
        return optionCCount;
    }

    public void setOptionCCount(int optionCCount) {
        this.optionCCount = optionCCount;
    }
}
