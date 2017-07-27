package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by shubhamk on 27/7/17.
 */

public class CabListItemFormat {
    String name;
    String phonenumber;

    public CabListItemFormat(String name, String phonenumber) {
        this.name = name;
        this.phonenumber = phonenumber;
    }

    public CabListItemFormat() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
