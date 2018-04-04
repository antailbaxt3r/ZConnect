package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by shubhamk on 9/2/17.
 */

public class PhonebookStudentHostelItem {
    String hostel;
    String cat;

    public PhonebookStudentHostelItem(String hostel, String cat) {
        this.hostel = hostel;
        this.cat = cat;
    }

    public PhonebookStudentHostelItem() {
    }

    public String getCat() {
        return cat;

    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getHostel() {
        return hostel;
    }

    public void setHostel(String hostel) {
        this.hostel = hostel;
    }
}


