package com.zconnect.zutto.zconnect.commonModules;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.utilities.FeatureDBName;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class NumberNotificationForFeatures {
    private String TAG = NumberNotificationForFeatures.class.getSimpleName();
    private String featureName;
    private long count;
    private String nodeForAllItemsOfTheFeature;
    private DatabaseReference ref;

    public NumberNotificationForFeatures(String featureName)
    {
        this.featureName = featureName;
    }

    public void setCount() {
        ref = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child(featureName).child("count");
        if(featureName.equals(FeatureDBName.KEY_ADMIN_PANEL))
        {
            ref = ref.getParent().getParent().getParent().child("newUsersCount");
            nodeForAllItemsOfTheFeature = "newUsers";
        }
        else if(featureName.equals(FeatureDBName.KEY_STOREROOM))
        {
            nodeForAllItemsOfTheFeature = "products";
        }
        else if(featureName.equals(FeatureDBName.KEY_CABPOOL))
        {
            nodeForAllItemsOfTheFeature = "allCabs";
        }
        else if(featureName.equals(FeatureDBName.KEY_EVENTS))
        {
            nodeForAllItemsOfTheFeature = ("activeEvents");
        }
        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull final MutableData mutableData) {
                Long count = mutableData.getValue(Long.class);
                Log.d(TAG, "value of count retrieved is " + count);
                if(count == null)
                {
                    ref.getParent().child(nodeForAllItemsOfTheFeature).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.d(TAG, "node does not exist. Value of count is " + dataSnapshot.getChildrenCount());
                            ref.setValue(dataSnapshot.getChildrenCount());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    mutableData.setValue(count + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }

    public void getCount(final MyCallBack myCallBack)
    {
        ref = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child(featureName).child("count");
        if(featureName.equals(FeatureDBName.KEY_ADMIN_PANEL))
        {
            ref = ref.getParent().getParent().getParent().child("newUsersCount");
        }
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        count = dataSnapshot.getValue(Long.class);
                        Log.d(TAG, "value of count inside listener is " + String.valueOf(count));
                        myCallBack.onCallBack(count);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


    public interface MyCallBack
    {
        public void onCallBack(long value);
    }
}
