package com.zconnect.zutto.zconnect.itemFormats;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.StorageReference;
import com.linkedin.android.spyglass.mentions.Mentionable;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.utilities.UserMentionsLoader;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class UserMentionsFormat implements Mentionable {

    private String username, userUID;

    public void setUsername(String username) {
        Log.d("MENTIONS",username);
        this.username = username;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    private String userImage;

    public UserMentionsFormat() {}

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public UserMentionsFormat(String username, String userUID, String userImage) {
        this.userImage = userImage;
        this.username = username;
        this.userUID = userUID;
    }

    public String getUsername() {
        return username;
    }

    public String getUserUID() {
        return userUID;
    }

    @NonNull
    @Override
    public String getTextForDisplayMode(MentionDisplayMode mode) {
        switch (mode) {
            case FULL:
                return getUsername();
            case PARTIAL:
                String[] words = getUsername().split(" ");
                return (words.length > 1) ? words[0] : "";
            case NONE:
            default:
                return "";
        }
    }

    @NonNull
    @Override
    public MentionDeleteStyle getDeleteStyle() {
        // People support partial deletion
        // i.e. "John Doe" -> DEL -> "John" -> DEL -> ""
        return MentionDeleteStyle.PARTIAL_NAME_DELETE;
    }

    @Override
    public int getSuggestibleId() {
        return getUsername().hashCode();
    }

    @Override
    public String getSuggestiblePrimaryText() {
        return getUsername();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(userUID);
        dest.writeString(userImage);
    }

    public UserMentionsFormat(Parcel in) {
        username = in.readString();
        userUID = in.readString();
        userImage = in.readString();
    }

    public static final Parcelable.Creator<UserMentionsFormat> CREATOR
            = new Parcelable.Creator<UserMentionsFormat>() {
        public UserMentionsFormat createFromParcel(Parcel in) {
            return new UserMentionsFormat(in);
        }

        public UserMentionsFormat[] newArray(int size) {
            return new UserMentionsFormat[size];
        }
    };

    // --------------------------------------------------
    // PersonLoader Class (loads people from JSON file)
    // --------------------------------------------------

    public static class MentionsLoader extends UserMentionsLoader<UserMentionsFormat> {
        private static final String TAG = MentionsLoader.class.getSimpleName();

        public MentionsLoader(ArrayList<UserMentionsFormat> joinedUsersList) {
            super(joinedUsersList);
        }

        @Override
        public UserMentionsFormat[] loadData(ArrayList<UserMentionsFormat> arr) {
            UserMentionsFormat[] data = new UserMentionsFormat[arr.size()];
            try {
                for (int i = 0; i < arr.size(); i++) {
                    UserMentionsFormat obj = arr.get(i);
                    String username = obj.getUsername();
                    String userUID = obj.getUserUID();
                    String userImage = obj.getUserImage();
                    data[i] = new UserMentionsFormat(username, userUID, userImage);
                }
            } catch (Exception e) {
                Log.e(TAG, "Unhandled exception while parsing person DataSnapshot", e);
            }

            return data;
        }

        // Modified to return suggestions based on both first and last name
        @Override
        public List<UserMentionsFormat> getSuggestions(QueryToken queryToken) {
            String[] namePrefixes = queryToken.getKeywords().toLowerCase().split(" ");
            List<UserMentionsFormat> suggestions = new ArrayList<>();
            if (mData != null) {
                for (UserMentionsFormat suggestion : mData) {
                    String firstName = suggestion.getUsername().toLowerCase();
//                    String lastName = suggestion.getLastName().toLowerCase();
                    if (namePrefixes.length == 2) {
                        if (firstName.startsWith(namePrefixes[0])) {
                            suggestions.add(suggestion);
                        }
                    } else {
                        if (firstName.startsWith(namePrefixes[0])) {
                            suggestions.add(suggestion);
                        }
                    }
                }
            }
            return suggestions;
        }
    }
}
