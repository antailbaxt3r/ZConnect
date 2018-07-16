package com.zconnect.zutto.zconnect.itemFormats;

/**
 * Created by Lokesh Garg on 08-02-2017.
 */

public class Product {
    private String ProductName, ProductDescription, Image, Key, Price, Phone_no, Category, negotiable;
    private PostedByDetails PostedBy;
    public Product() {

    }

    public Product(String productName, String productDescription, String image, String key, String price, String phone_no, String category ,String negotiable, PostedByDetails postedBy) {
        ProductName = productName;
        ProductDescription = productDescription;
        Image = image;
        Key = key;
        Price = price;
        Phone_no = phone_no;
        Category = category;
        this.negotiable= negotiable;
        PostedBy = postedBy;
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

    public void setPostedBy(PostedByDetails postedBy) { PostedBy = postedBy; }

    public PostedByDetails getPostedBy() { return PostedBy;}
}
