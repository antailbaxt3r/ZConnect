package com.zconnect.zutto.zconnect.itemFormats;

public class NewUserItemFormat {

    private String idImageURL;
    private String about;
    private String statusCode;
    private String UID;
    private String name;
    private PostedByDetails approvedRejectedBy;


    public NewUserItemFormat(){

    }

    public NewUserItemFormat(String idImageURL, String about, String statusCode, String UID, PostedByDetails approvedRejectedBy, String name) {
        this.idImageURL = idImageURL;
        this.about = about;
        this.statusCode = statusCode;
        this.UID = UID;
        this.approvedRejectedBy = approvedRejectedBy;
        this.name = name;
    }

    public String getIdImageURL() {
        return idImageURL;
    }

    public void setIdImageURL(String idImageURL) {
        this.idImageURL = idImageURL;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public PostedByDetails getApprovedRejectedBy() {
        return approvedRejectedBy;
    }

    public void setApprovedRejectedBy(PostedByDetails approvedRejectedBy) {
        this.approvedRejectedBy = approvedRejectedBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
