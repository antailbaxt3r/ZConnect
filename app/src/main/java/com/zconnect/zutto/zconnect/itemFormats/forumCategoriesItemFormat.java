package com.zconnect.zutto.zconnect.itemFormats;

import java.util.Vector;

/**
 * Created by shubhamk on 9/2/17.
 */

public class forumCategoriesItemFormat {
    private String name;
    private String tabUID;
    private String catUID;
    private ChatItemFormats lastMessage;
    private String forumType;
    private PostedByDetails PostedBy;
    private Vector<UsersListItemFormat> usersListItemFormats;
    private String image;
    private String imageThumb;
    private Integer totalMessages;
    private Integer seenMessages;

    public forumCategoriesItemFormat(String name, String catUID, String tabUID, ChatItemFormats lastMessage, Vector<UsersListItemFormat> usersListItemFormats, PostedByDetails PostedBy, String imageThumb, String image, Integer totalMessages ) {

        this.name = name;
        this.tabUID = tabUID;
        this.catUID = catUID;
        this.lastMessage = lastMessage;
        this.usersListItemFormats = usersListItemFormats;
        this.image = image;
        this.imageThumb = imageThumb;
        this.PostedBy = PostedBy;
        this.totalMessages = totalMessages;
    }

    public forumCategoriesItemFormat() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTabUID() {
        return tabUID;
    }

    public void setTabUID(String tabUID) {
        this.tabUID = tabUID;
    }

    public String getCatUID() {
        return catUID;
    }

    public void setCatUID(String catUID) {
        this.catUID = catUID;
    }

    public ChatItemFormats getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatItemFormats lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Vector<UsersListItemFormat> getUsersListItemFormats() {
        return usersListItemFormats;
    }

    public void setUsersListItemFormats(Vector<UsersListItemFormat> usersListItemFormats) {
        this.usersListItemFormats = usersListItemFormats;
    }

    public String getForumType() {
        return forumType;
    }

    public void setForumType(String forumType) {
        this.forumType = forumType;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImageThumb(String imageThumb) {
        this.imageThumb = imageThumb;
    }

    public String getImageThumb() {
        return imageThumb;
    }

    public PostedByDetails getPostedBy() {
        return PostedBy;
    }

    public void setPostedBy(PostedByDetails postedBy) {
        PostedBy = postedBy;
    }

    public Integer getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(Integer totalMessages) {
        this.totalMessages = totalMessages;
    }

    public Integer getSeenMessages() {
        return seenMessages;
    }

    public void setSeenMessages(Integer seenMessages) {
        this.seenMessages = seenMessages;
    }
}