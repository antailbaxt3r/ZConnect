package com.zconnect.zutto.zconnect.itemFormats;

public class InternshipsItemFormat {
    private String description;
    private String duration;
    private String organization;
    private String question;
    private String role;
    private Long stipend;

    public InternshipsItemFormat() { }

    public InternshipsItemFormat(String description,String duration,String organization,String question,String role,Long stipend)
    {
        this.description = description;
        this.duration = duration;
        this.organization = organization;
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

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
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

    public Long getStipend() {
        return stipend;
    }

    public void setStipend(Long stipend) {
        this.stipend = stipend;
    }
}
