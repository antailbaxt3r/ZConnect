package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by Lokesh Garg on 01-02-2018.
 */

public class CommunitiesItemFormat {

    String name, image, email;


    public CommunitiesItemFormat(String Name, String Image, String email) {
        this.name=Name;
        this.image=Image;
        this.email=email;

    }

    public CommunitiesItemFormat() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
