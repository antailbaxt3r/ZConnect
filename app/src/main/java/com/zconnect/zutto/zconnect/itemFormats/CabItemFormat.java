package com.zconnect.zutto.zconnect.itemFormats;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

/**
 * Created by shubhamk on 26/7/17.
 */

public class CabItemFormat {
    private String source;
    private String destination;
    private String date;
    private String details;
    private String time;
    private String key;
    private String DT;

    public String getForumUID() {

        return forumUID;
    }

    public void setForumUID(String forumUID) {
        this.forumUID = forumUID;
    }

    private String forumUID;
    private int from;
    private int to;
    private HashMap<String, UsersListItemFormat> usersListItemFormats;
    private PostedByDetails PostedBy;
    private long PostTimeMillis;



    public CabItemFormat(String source, HashMap<String, UsersListItemFormat> usersListItemFormats, PostedByDetails PostedBy, String destination, String date, String details, String time, String key, String DT, int from, int to, long PostTimeMillis) {

        this.source = source;
        this.destination = destination;
        this.date = date;
        this.details = details;
        this.time = time;
        this.key = key;
        this.DT=DT;
        this.from=from;
        this.to=to;
        this.PostTimeMillis = PostTimeMillis;
        this.PostedBy = PostedBy;
        this.usersListItemFormats = usersListItemFormats;

    }

    public CabItemFormat() {
    }

    public PostedByDetails getPostedBy() {
        return PostedBy;
    }

    public void setPostedBy(PostedByDetails postedBy) {
        PostedBy = postedBy;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public String getDT() {
        return DT;
    }

    public void setDT(String DT) {
        this.DT=DT;
    }

    public int getFrom(){return from;}

    public void setFrom(int from){
        this.from=from;
    }

    public int getTo(){return to;}

    public void setTo(int to){
        this.to=to;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getPostTimeMillis() { return this.PostTimeMillis; }

    public void setPostTimeMillis(long PostTimeMillis) { this.PostTimeMillis = PostTimeMillis; }

    public HashMap<String, UsersListItemFormat> getUsersListItemFormats() {
        if(usersListItemFormats!=null) {
            return usersListItemFormats;
        }else{
            return new HashMap<String, UsersListItemFormat>();
        }
    }

    public void setUsersListItemFormats(HashMap<String, UsersListItemFormat> usersListItemFormats) {
        this.usersListItemFormats = usersListItemFormats;
    }
//
//    public Vector<UsersListItemFormat> getCabListItemFormats() {
//        return cabListItemFormats;
//    }
//
//    public void setCabListItemFormats(Vector<UsersListItemFormat> cabListItemFormats) {
//        this.cabListItemFormats = cabListItemFormats;
//    }
}
