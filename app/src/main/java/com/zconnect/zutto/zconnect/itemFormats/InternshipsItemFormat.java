package com.zconnect.zutto.zconnect.itemFormats;

public class InternshipsItemFormat {
    private String description;
    private String duration;
    private String organisation;
    private String question;
    private String role;
    private String stipend;

    public InternshipsItemFormat(String description,String duration,String organisation,String question,String role,String stipend)
    {
        this.description = description;
        this.duration = duration;
        this.organisation = organisation;
        this.question = question;
        this.role = role;
        this.stipend = stipend;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStipend() {
        return stipend;
    }

    public void setStipend(String stipend) {
        this.stipend = stipend;
    }
}
