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
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;

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
        String key;
        HashMap<String, Object> notificationMap = new HashMap<>();
        HashMap<String, Boolean> seenmap = new HashMap<>();
        /*
        audience true - Community specific notifications
        audience false - User specific notifications
        */
        if(!notifiedby.getUserUID().equals(uid)){
        if (audience) {
            notificationsRef = FirebaseDatabase.getInstance().getReference().child("communities").
                    child(communityReference).child("globalNotifications");
            key = NotificationIdentifierUtilities.KEY_GLOBAL;

        } else {
            notificationsRef = FirebaseDatabase.getInstance().getReference().child("communities").
                    child(communityReference).child("Users1").child(uid)
                    .child("notifications");

            key = NotificationIdentifierUtilities.KEY_PERSONAL;
        }
        seenmap.put(FirebaseAuth.getInstance().getUid(),false);
        DatabaseReference newNotifRef = notificationsRef.push();
        notificationMap.put("scope",key);
        notificationMap.put("title", title);
        notificationMap.put("desc", desc);
        notificationMap.put("PostTimeMillis", System.currentTimeMillis());
        notificationMap.put("seen", seenmap);
        notificationMap.put("type", type);
        notificationMap.put("key", newNotifRef.getKey());
        notificationMap.put("notifiedBy", notifiedby);

        newNotifRef.setValue(notificationMap);
        if (metadata != null) {
            for (HashMap.Entry<String, Object> entry : metadata.entrySet()) {
                newNotifRef.child("metadata").child(entry.getKey()).setValue(entry.getValue());
            }

        }
    }
    }
}
