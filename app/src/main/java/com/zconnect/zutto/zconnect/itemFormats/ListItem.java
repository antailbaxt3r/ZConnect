package com.zconnect.zutto.zconnect.itemFormats;

import java.io.Serializable;

public class ListItem implements Serializable {

    private String linkURL;
    private String linkTitle;
    private int upvote;
    private PostedByDetails PostedBy;
    private long PostTimeMillis;

    public ListItem(){
        this.upvote=0;
    }

    public ListItem(String linkURL, String linkTitle, PostedByDetails PostedBy, long PostTimeMillis){
        this.linkURL=linkURL;
        this.linkTitle=linkTitle;
        this.upvote=0;
        this.PostedBy = PostedBy;
        this.PostTimeMillis = PostTimeMillis;
    }
    public ListItem(String linkURL, String linkTitle, int upvote, PostedByDetails PostedBy, long PostTimeMillis){
        this.linkURL=linkURL;
        this.linkTitle=linkTitle;
        this.upvote=upvote;
        this.PostedBy = PostedBy;
        this.PostTimeMillis = PostTimeMillis;
    }

    public long getPostTimeMillis() {
        return PostTimeMillis;
    }

    public void setPostTimeMillis(long postTimeMillis) {
        PostTimeMillis = postTimeMillis;
    }

    public void setPostedBy(PostedByDetails postedBy) {
        PostedBy = postedBy;
    }

    public PostedByDetails getPostedBy() {
        return PostedBy;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
    }

    public int getUpvote() {
        return upvote;
    }

    public void setUpvote(int upvote) {
        this.upvote = upvote;
    }
}
