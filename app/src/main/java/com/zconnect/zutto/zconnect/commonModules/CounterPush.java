package com.zconnect.zutto.zconnect.commonModules;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;

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
        Vector<Vector<String>> vector = counterItemFormat.getMeta();
        ref.child(key).child("meta").setValue(vector);
        /*for(int i=0; i<vector.size(); i++){
            ref.child(key).child("meta").child(vector.get(i).elementAt(0)).child(vector.get(i).elementAt(1));
        }*/

    }
}
