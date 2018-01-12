package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by shubhamk on 8/2/17.
 */

public class PhonebookDisplayItem {
    private String imageurl;
    private String name;
    private String desc;
    private String number;
    private String category;
    private String email;
    private String hostel;
    private String skills;
    private String Uid;

    public PhonebookDisplayItem(String imageurl, String name, String desc, String number,
                                String category, String email, String hostel, String skills, String Uid) {
        this.imageurl = imageurl;
        this.name = name;
        this.desc = desc;
        this.number = number;
        this.category = category;
        this.email = email;
        this.hostel = hostel;
        this.skills=skills;
        this.Uid=Uid;
    }

    public PhonebookDisplayItem() {
    }

    public  String getUid(){return Uid;};

    public  void setUid(String uid){
        this.Uid=uid;
    }
    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getHostel() {
        return hostel;
    }

    public void setHostel(String hostel) {
        this.hostel = hostel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
