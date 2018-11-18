package com.zconnect.zutto.zconnect.commonModules;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class GlobalFunctions {

    private static DatabaseReference pointsRef;

    public GlobalFunctions() {
    }

    public static void addPoints(final Integer morePoints){

        pointsRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getUid()).child("userPoints");
        pointsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                String temp = mutableData.getValue(String.class);
                Integer existingPoints = Integer.parseInt(temp);
                Integer newPoints;

                newPoints = morePoints + existingPoints;
                mutableData.setValue(newPoints.toString());
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

}
