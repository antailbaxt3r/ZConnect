package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by Lokesh Garg on 01-02-2018.
 */

public class CommunitiesItemFormat {

    String name, image, email,code;

    public CommunitiesItemFormat(String Name, String Image, String email, String code) {
        this.name=Name;
        this.image=Image;
        this.email=email;
        this.code=code;

    }

    public CommunitiesItemFormat() {

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
