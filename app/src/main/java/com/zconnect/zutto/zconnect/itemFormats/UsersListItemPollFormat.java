package com.zconnect.zutto.zconnect.itemFormats;

public class UsersListItemPollFormat {
    private String optionSelected;

    public UsersListItemPollFormat() {

    }

    public UsersListItemPollFormat(String optionSelected) {
        this.optionSelected = optionSelected;
    }

    public void setOptionSelected(String optionSelected) {
        this.optionSelected = optionSelected;
    }

    public String getOptionSelected() {
        return optionSelected;
    }
}
