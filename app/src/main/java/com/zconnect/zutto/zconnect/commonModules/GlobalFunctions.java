package com.zconnect.zutto.zconnect.commonModules;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.PostedByDetails;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.FeatureNamesUtilities;
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
                if(mutableData.getValue() == null){
                    mutableData.setValue(0);
                }

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

        if (audience) {

            if (!type.equals("statusNestedComment")) {
                notificationsRef = FirebaseDatabase.getInstance().getReference().child("communities").
                        child(communityReference).child("globalNotifications");
                key = NotificationIdentifierUtilities.KEY_GLOBAL;
            }else{
                key = NotificationIdentifierUtilities.KEY_PERSONAL;
            }

        } else if (uid != null && FirebaseAuth.getInstance().getUid().equals(uid)) {
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
                        DatabaseReference statusInFeatureRef = dataSnapshot.getRef().getRoot()
                                .child("communities").child(communityReference).child("features")
                                .child(FeatureNamesUtilities.KEY_STATUSES).child("inAppNotifications")
                                .child(metadata.get("key").toString());
                        for (DataSnapshot childsnap : dataSnapshot.getChildren()) {
                            if(notifiedby.getUserUID().equals(childsnap.getKey()) || FirebaseAuth.getInstance().getUid().equals(childsnap.getKey()))
                                continue;
                            Log.d("keyyyy", notifiedby.getUserUID()+"");
                            if(!notifiedby.getUserUID().equals(childsnap.getKey()) && !FirebaseAuth.getInstance().getUid().equals(childsnap.getKey())) {
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
                                //following code stores the notif key and receiver UID inside of
                                //feature/statuses/{statusKey}/{notifKey} node
                                //used to handle deletion of in app notifs upon deletion on status
                                HashMap<String, Object> _notifMap = new HashMap<>();
                                _notifMap.put("key", newNotifRef.getKey());
                                _notifMap.put("receiverUID", childsnap.getKey());
                                statusInFeatureRef.child(newNotifRef.getKey()).setValue(_notifMap);
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

            if(notifiedby.getUserUID()!=FirebaseAuth.getInstance().getUid()) {
                newNotifRef.setValue(notificationMap);
                if (metadata != null) {
                    switch (type) {
                        case "addforum":
                            Log.d("Adding", "inAppNotifications: ");
                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/forums/inAppNotifications/" + metadata.get("featurePID")).child(newNotifRef.getKey()).child("key").setValue(newNotifRef.getKey());
                            break;
                        case "productAdd":
                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/storeroom/inAppNotifications/" + metadata.get("featurePID")).child(newNotifRef.getKey()).child("key").setValue(newNotifRef.getKey());
                            break;
                        case "productShortlist":
                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/storeroom/inAppNotifications/" + metadata.get("featurePID")).child(newNotifRef.getKey()).child("key").setValue(newNotifRef.getKey());
                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/storeroom/inAppNotifications/" + metadata.get("featurePID")).child(newNotifRef.getKey()).child("receiverUID").setValue(uid);
                            break;
                        case "eventBoost":
                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/events/inAppNotifications/" + metadata.get("featurePID")).child(newNotifRef.getKey()).child("key").setValue(newNotifRef.getKey());
                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/events/inAppNotifications/" + metadata.get("featurePID")).child(newNotifRef.getKey()).child("receiverUID").setValue(uid);
                            break;
                        case "eventAdd":
                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/events/inAppNotifications/" + metadata.get("featurePID")).child(newNotifRef.getKey()).child("key").setValue(newNotifRef.getKey());
                            break;
                        case "status":
                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/statuses/inAppNotifications/" + metadata.get("featurePID")).child(newNotifRef.getKey()).child("key").setValue(newNotifRef.getKey());
                            break;
                        case "statusComment":
                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/statuses/inAppNotifications/" + metadata.get("featurePID")).child(newNotifRef.getKey()).child("key").setValue(newNotifRef.getKey());
                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("/features/statuses/inAppNotifications/" + metadata.get("featurePID")).child(newNotifRef.getKey()).child("receiverUID").setValue(uid);
                            break;


                    }
                    for (HashMap.Entry<String, Object> entry : metadata.entrySet()) {
                        newNotifRef.child("metadata").child(entry.getKey()).setValue(entry.getValue());
                    }
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

    public static Bitmap combineImages(Bitmap c, Context ctx) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;
        Bitmap s = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.share_icon);
        int width, height = 0;

        if(c.getWidth()>s.getWidth()){
            width = s.getWidth();
            int temp = (int) (c.getHeight()*((float)s.getWidth()/c.getWidth()));
            c= Bitmap.createScaledBitmap(c, s.getWidth(), temp, false);

        }else {
            width = c.getWidth();
            int temp = (int) (s.getHeight()*((float)c.getWidth()/s.getWidth()));
            s= Bitmap.createScaledBitmap(s, c.getWidth(), temp, false);
        }

        height = c.getHeight() + s.getHeight();

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, c.getHeight(),null);

        return cs;
    }

}
