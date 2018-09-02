package com.zconnect.zutto.zconnect.commonModules;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;

import java.util.HashMap;
import java.util.Vector;

public class CounterPush extends BaseActivity{
    public DatabaseReference ref;
    CounterItemFormat counterItemFormat;
    String communityRef;

    public CounterPush(CounterItemFormat counterItemFormat, String communityRef) {
        this.counterItemFormat = counterItemFormat;
        this.communityRef = communityRef;
    }

    public void pushValues(){
        ref = FirebaseDatabase.getInstance().getReference().child("communities").child(communityRef).child("counters");
        String key = ref.push().getKey();
        ref.child(key).child("userID").setValue(counterItemFormat.getUserID());
        ref.child(key).child("timestamp").setValue(counterItemFormat.getTimestamp());
        ref.child(key).child("uniqueID").setValue(counterItemFormat.getUniqueID());
        HashMap<String, String> hashMap = counterItemFormat.getMeta();
        ref.child(key).child("meta").setValue(hashMap);
    }
}