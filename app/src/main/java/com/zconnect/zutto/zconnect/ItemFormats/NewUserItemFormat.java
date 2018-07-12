package com.zconnect.zutto.zconnect.ItemFormats;

public class NewUserItemFormat {

    private String idImageURL;
    private String about;
    private String statusCode;
    private String UID;
    private PostedByDetails approvedBy;

    public NewUserItemFormat(String idImageURL, String about, String statusCode, String UID, PostedByDetails approvedBy) {
        this.idImageURL = idImageURL;
        this.about = about;
        this.statusCode = statusCode;
        this.UID = UID;
        this.approvedBy = approvedBy;
    }

    public NewUserItemFormat(){

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

    public PostedByDetails getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(PostedByDetails approvedBy) {
        this.approvedBy = approvedBy;
    }

}
