package com.zconnect.zutto.zconnect.commonModules;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.PostedByDetails;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.MessageTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.util.Calendar;
import java.util.HashMap;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class GlobalFunctions {

    private static DatabaseReference pointsRef, notificationsRef;

    public GlobalFunctions() {
    }

    public static void addPoints(final Integer morePoints){

        pointsRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getUid()).child("userPointsNum");
        pointsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                if(mutableData == null){
                    mutableData.setValue(0);
                }
//                String temp = mutableData.getValue(String.class);
                Integer existingPoints = mutableData.getValue(Integer.class);
                Integer newPoints;

                newPoints = morePoints + existingPoints;
                mutableData.setValue(newPoints);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
            }
        });
    }

    public static void inAppNotifications(String title, String desc, final UserItemFormat notifiedby, boolean audience, String type, HashMap<String, Object> metadata, String uid) {
        //uid variable is uid of receiver
        String key;
        HashMap<String, Object> notificationMap = new HashMap<>();
        HashMap<String, Boolean> seenmap = new HashMap<>();

        Log.d("dddinggg", "productShortlistNotification: ");
        /*
        audience true - Community specific notifications
        audience false - User specific notifications
        */
        Log.d("SASASA", uid + "");
        if (audience) {
            if (!type.equals("statusNestedComment")) {
                notificationsRef = FirebaseDatabase.getInstance().getReference().child("communities").
                        child(communityReference).child("globalNotifications");
                key = NotificationIdentifierUtilities.KEY_GLOBAL;
            }else{
                key = NotificationIdentifierUtilities.KEY_PERSONAL;
            }

        } else if (notifiedby.getUserUID().equals(uid)) {
            //for personal in app notifs return void if the notified by is same as the current user.
            return;
        } else {
                notificationsRef = FirebaseDatabase.getInstance().getReference().child("communities").
                        child(communityReference).child("Users1").child(uid)
                        .child("notifications");
            key = NotificationIdentifierUtilities.KEY_PERSONAL;
        }

        if (type.equals("statusNestedComment")) {
            Log.d("commentKey", String.valueOf(metadata.get("key")));
            Log.d("referencekey", String.valueOf(metadata.get("ref")));
            String commentKey = String.valueOf(metadata.get("key"));
            String referenceKey = String.valueOf(metadata.get("ref"));
            HashMap<String,Object> metamap = new HashMap<>();
            metamap.put("key",commentKey);
            metamap.put("ref",referenceKey);
            DatabaseReference dbref = (DatabaseReference) metadata.get("ref");
            Log.d("databaseref", String.valueOf(dbref));
                dbref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot childsnap : dataSnapshot.getChildren()) {
                            Log.d("keyyyy", notifiedby.getUserUID()+"");
                            if(!notifiedby.getUserUID().equals(childsnap.getKey())) {
                                notificationsRef = FirebaseDatabase.getInstance().getReference().child("communities").
                                        child(communityReference).child("Users1").child(childsnap.getKey())
                                        .child("notifications");
                                seenmap.put(FirebaseAuth.getInstance().getUid(), false);
                                DatabaseReference newNotifRef = notificationsRef.push();
                                notificationMap.put("scope", key);
                                notificationMap.put("title", title);
                                notificationMap.put("desc", desc);
                                notificationMap.put("PostTimeMillis", System.currentTimeMillis());
                                notificationMap.put("seen", seenmap);
                                notificationMap.put("type", type);
                                notificationMap.put("key", newNotifRef.getKey());
                                notificationMap.put("metadata",metamap);
                                notificationMap.put("notifiedBy", notifiedby);
                                newNotifRef.setValue(notificationMap);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        } else {
            Log.d("aaaaaaaaddddinggg", "productShortlistNotification: ");
            seenmap.put(FirebaseAuth.getInstance().getUid(), false);
            DatabaseReference newNotifRef = notificationsRef.push();
            notificationMap.put("scope", key);
            notificationMap.put("title", title);
            notificationMap.put("desc", desc);
            notificationMap.put("PostTimeMillis", System.currentTimeMillis());
            notificationMap.put("seen", seenmap);
            notificationMap.put("type", type);
            notificationMap.put("key", newNotifRef.getKey());
            notificationMap.put("notifiedBy", notifiedby);
            newNotifRef.setValue(notificationMap);
            if (metadata != null) {
                switch (type) {
                    case "addforum":
                        Log.d("Adding", "inAppNotifications: ");
                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/forums/inAppNotifications/" + metadata.get("featurePID")).child("key").setValue(newNotifRef.getKey());
                        break;
                    case "productAdd":
                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/storeroom/inAppNotifications/" + metadata.get("featurePID")).child("key").setValue(newNotifRef.getKey());
                        break;
                    case "productShortlist":
                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/storeroom/inAppNotifications/" + metadata.get("featurePID")).child("key").setValue(newNotifRef.getKey());
                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/storeroom/inAppNotifications/" + metadata.get("featurePID")).child("receiverUID").setValue(uid);
                        break;
                    case "eventBoost":
                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/events/inAppNotifications/" + metadata.get("featurePID")).child("key").setValue(newNotifRef.getKey());
                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/events/inAppNotifications/" + metadata.get("featurePID")).child("receiverUID").setValue(uid);
                        break;
                    case "eventAdd":
                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/events/inAppNotifications/" + metadata.get("featurePID")).child("key").setValue(newNotifRef.getKey());
                        break;
                }
                for (HashMap.Entry<String, Object> entry : metadata.entrySet()) {
                    newNotifRef.child("metadata").child(entry.getKey()).setValue(entry.getValue());
                }
            }
        }
    }
    //ONLY TO CREATE FORUM IN OTHERS TAB
    public static  void createForumWithDetails(final String catName,
                                                 final String forumUID,
                                                 final UserItemFormat userItem,
                                                 final String tabUid,
                                                 final String firstMessage,
                                                 final String mImageURL)
    {
        final DatabaseReference databaseReferenceCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories");
        final DatabaseReference databaseReferenceTabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(tabUid);
        long calendarTime = Calendar.getInstance().getTimeInMillis();

        final DatabaseReference newPush = databaseReferenceCategories.child(forumUID);
        DatabaseReference mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        newPush.child("name").setValue(catName);
        Long postTimeMillis = System.currentTimeMillis();
        newPush.child("PostTimeMillis").setValue(postTimeMillis);
        newPush.child("UID").setValue(newPush.getKey());
        newPush.child("tab").setValue(tabUid);
        databaseReferenceTabsCategories.child(newPush.getKey()).child("verified").setValue(false);


        databaseReferenceTabsCategories.child(newPush.getKey()).child("name").setValue(catName);
        databaseReferenceTabsCategories.child(newPush.getKey()).child("catUID").setValue(newPush.getKey());
        databaseReferenceTabsCategories.child(newPush.getKey()).child("tabUID").setValue(tabUid);

        CounterItemFormat counterItemFormat = new CounterItemFormat();
        HashMap<String, String> meta = new HashMap<>();
        meta.put("catID", tabUid);
        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
        counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_FORUM_CREATED);
        counterItemFormat.setTimestamp(System.currentTimeMillis());
        counterItemFormat.setMeta(meta);
        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
        counterPush.pushValues();

        //ADD ADIMIN IN USERS
        UsersListItemFormat userDetails = new UsersListItemFormat();
        userDetails.setImageThumb(userItem.getImageURLThumbnail());
        userDetails.setName(userItem.getUsername());
        userDetails.setPhonenumber(userItem.getMobileNumber());
        userDetails.setUserUID(userItem.getUserUID());
        userDetails.setUserType(ForumsUserTypeUtilities.KEY_ADMIN);

        databaseReferenceTabsCategories.child(newPush.getKey()).child("users").child(userItem.getUserUID()).setValue(userDetails);


        newPush.child("PostedBy").child("Username").setValue(userItem.getUsername());
        newPush.child("PostedBy").child("ImageThumb").setValue(userItem.getImageURLThumbnail());

        ChatItemFormats message = new ChatItemFormats();
        message.setTimeDate(calendarTime);
        message.setUuid(userItem.getUserUID());
        message.setName(userItem.getUsername());
        message.setImageThumb(userItem.getImageURLThumbnail());
        message.setMessage("\"" + firstMessage + "\"");
        message.setMessageType(MessageTypeUtilities.KEY_MESSAGE_STR);
        newPush.child("Chat").push().setValue(message);

        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(tabUid).child(newPush.getKey()).child("lastMessage").setValue(message);

        newPush.child("image").setValue(mImageURL);
        newPush.child("imageThumb").setValue(mImageURL);
        databaseReferenceTabsCategories.child(newPush.getKey()).child("imageThumb").setValue(mImageURL);
        databaseReferenceTabsCategories.child(newPush.getKey()).child("image").setValue(mImageURL);


    }

}
