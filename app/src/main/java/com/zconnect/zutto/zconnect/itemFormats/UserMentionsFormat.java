package com.zconnect.zutto.zconnect.itemFormats;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.linkedin.android.spyglass.mentions.Mentionable;

public class UserMentionsFormat {

    private String username, userUID;

    public UserMentionsFormat() {}
    public UserMentionsFormat(String username, String userUID) {
        this.username = username;
        this.userUID = userUID;
    }

    public String getUsername() {
        return username;
    }

    public String getUserUID() {
        return userUID;
    }
}
