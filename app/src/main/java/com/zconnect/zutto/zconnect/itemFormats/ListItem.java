package com.zconnect.zutto.zconnect.itemFormats;

import java.io.Serializable;

public class ListItem implements Serializable {

    private String linkURL;
    private String title;
    private int upvote;

    public ListItem(){
        this.upvote=0;
    }

    public  ListItem(String linkURL, String title){
        this.linkURL=linkURL;
        this.title=title;
        this.upvote=0;
    }


    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvote) {
        this.upvote = upvote;
    }
}
