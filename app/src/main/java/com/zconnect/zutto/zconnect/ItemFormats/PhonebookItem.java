package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by shubhamk on 8/2/17.
 */

public class PhonebookItem {
    String imgurl;
    String name;
    String number;
    String Uid;
    PhonebookDisplayItem phonebookDisplayItem;

    public PhonebookItem(String imgurl, String name, String number, String Uid, PhonebookDisplayItem phonebookDisplayItem) {
        this.imgurl = imgurl;
        this.name = name;
        this.number = number;
        this.phonebookDisplayItem = phonebookDisplayItem;
        this.Uid = Uid;
    }

    public PhonebookItem() {
    }

    public String getUid(){
        if (phonebookDisplayItem != null) return phonebookDisplayItem.getUid();
        else return "";
    }
    public PhonebookDisplayItem getPhonebookDisplayItem() {
        return phonebookDisplayItem;
    }

    public void setPhonebookDisplayItem(PhonebookDisplayItem phonebookDisplayItem) {
        this.phonebookDisplayItem = phonebookDisplayItem;
    }

    public String getImgurl() {
        if (phonebookDisplayItem != null) return phonebookDisplayItem.getImageurl();
        else return "";
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getName() {
        if (phonebookDisplayItem != null) return phonebookDisplayItem.getName();
        else return "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        if (phonebookDisplayItem != null) return phonebookDisplayItem.getNumber();
        else return "";
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
