package com.zconnect.zutto.zconnect.commonModules;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;

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
                Log.d("PSYCHO", "postTransaction:onComplete:" + databaseError);
            }
        });
    }

    public static void pushNotifications(String title, String desc, boolean audience, int type, HashMap<String, String> metadata) {
        String notificationsUID = notificationsRef.push().getKey();

        /*
        audience true - Community specific notifications
        audience false - User specific notifications
        */
        if(audience) {
            notificationsRef = FirebaseDatabase.getInstance().getReference().child("communities").
                    child(communityReference).child("notifications").child(notificationsUID);;

        }
        else {
            notificationsRef = FirebaseDatabase.getInstance().getReference().child("communities").
                    child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getUid())
                    .child("notifications").child(notificationsUID);
        }

        notificationsRef.child("title").setValue(title);
        notificationsRef.child("desc").setValue(desc);
        notificationsRef.child("count").setValue(0);
        notificationsRef.child("type").setValue(type);
        notificationsRef.child("timestamp").setValue(ServerValue.TIMESTAMP);
        for(HashMap.Entry<String, String> entry : metadata.entrySet()) {
            notificationsRef.child("metadata").child(entry.getKey()).setValue(entry.getValue());
        }
    }
}
