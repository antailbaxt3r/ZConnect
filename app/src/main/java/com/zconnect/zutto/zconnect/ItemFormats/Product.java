package com.zconnect.zutto.zconnect.ItemFormats;

/**
 * Created by Lokesh Garg on 08-02-2017.
 */

public class Product {
    private String ProductName, ProductDescription, Image, Key, Price, Phone_no, PostedBy, Category, negotiable;

    public Product() {

    }

    public Product(String productName, String productDescription, String image, String key, String price, String phone_no, String postedBy, String category ,String negotiable) {
        ProductName = productName;
        ProductDescription = productDescription;
        Image = image;
        Key = key;
        Price = price;
        Phone_no = phone_no;
        PostedBy = postedBy;
        Category = category;
        this.negotiable= negotiable;
    }

    public String getNegotiable() {
        return negotiable;
    }

    public void setNegotiable(String negotiable) {
        this.negotiable = negotiable;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPhone_no() {
        return Phone_no;
    }


    public String getPostedBy() {
        return PostedBy;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getKey() {
        return Key;
    }

    public String getPrice() {
        return Price;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
