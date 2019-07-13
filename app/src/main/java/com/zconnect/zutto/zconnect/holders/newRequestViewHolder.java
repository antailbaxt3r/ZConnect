package com.zconnect.zutto.zconnect.holders;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;

import com.zconnect.zutto.zconnect.CabPoolLocations;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;

import java.util.HashMap;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;


public class newRequestViewHolder extends RecyclerView.ViewHolder {
    public TextView newRequestName, postedByNameInLocation;
    public Button acceptUserButton, declineUserButton;
    public SimpleDraweeView postedByImageLocation;

    public newRequestViewHolder(View itemView) {
        super(itemView);
        newRequestName = itemView.findViewById(R.id.name_new_request);
        acceptUserButton = itemView.findViewById(R.id.accept_new_request);
        declineUserButton = itemView.findViewById(R.id.decline_new_request);
        postedByImageLocation = itemView.findViewById(R.id.postedByImageLocation);
        postedByNameInLocation = itemView.findViewById(R.id.postedByInLocation);
    }

    public void setAcceptDeclineButtonForLocations(final String key, final String uid, final UserItemFormat userItemFormat) {
        acceptUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference requestedLocationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("admin").child("requests");
                final DatabaseReference addNewLocation=FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("locations").child(key);

                /*Long postTimeMillis = System.currentTimeMillis(); addNewLocation .child("PostTimeMillis").setValue(postTimeMillis);*/

                final HashMap<String, Object> map = new HashMap<>();
                map.put("PostTimeMillis", System.currentTimeMillis());

                requestedLocationDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            addNewLocation.child(key).child("locationName").setValue(dataSnapshot.child(key).child("locationName").getValue());
                            addNewLocation.child(key).child("PostedBy").child("Username").setValue(dataSnapshot.child(key).child("PostedBy").child("Username").getValue());
                            addNewLocation.child(key).child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child(key).child("PostedBy").child("ImageThumb").getValue());
                        } catch (Exception e) {
                        }

                        //todo : Implement push notification for the admin request feature
                        //todo : add location , postedBy details to database, currently only posttime millis is getting stored in the database
                        GlobalFunctions.inAppNotifications("has accepted your cabpool request", "Your cabpool location "+dataSnapshot.child(key).child("locationName").getValue()+ " is now approved", userItemFormat, false, "adminrequest", null, uid);
                        requestedLocationDatabaseReference.child(key).removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        declineUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference requestedLocationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("admin").child("requests");
                requestedLocationDatabaseReference.child(key).removeValue();
                requestedLocationDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        GlobalFunctions.inAppNotifications("has declined your cabpool request", "Your cabpool location "+dataSnapshot.child(key).child("locationName").getValue()+" is rejected", userItemFormat, false, "adminrequest", null, uid);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    public void setAcceptDeclineButtonForForumTabs(final String key, final String uid, final UserItemFormat userItemFormat) {

        final DatabaseReference requestForumTabs = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/admin/requests");
        final DatabaseReference forumTab = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/forums/tabs");

        acceptUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestForumTabs.child(key).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        /*forumTab.child(dataSnapshot.getValue().toString().trim()).child("name").setValue(dataSnapshot.getValue().toString().trim().toLowerCase());
                        forumTab.child(dataSnapshot.getValue().toString().trim()).child("UID").setValue(dataSnapshot.getValue().toString().trim());*/

                        final HashMap<String, Object> map = new HashMap<>();
                        map.put("name",dataSnapshot.getValue().toString().trim().toLowerCase());
                        map.put("UID",dataSnapshot.getValue().toString().trim());

                        forumTab.child(dataSnapshot.getValue().toString().trim()).setValue(map);
                        requestForumTabs.child(key).removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                GlobalFunctions.inAppNotifications("has accepted your forum tab request", "You forum tab request has been approved", userItemFormat, false, "acceptforum", null, uid);
            }
        });
        declineUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference requestForumTabs = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/admin/requests");
                requestForumTabs.child(key).removeValue();
                GlobalFunctions.inAppNotifications("has declined your forum tab request", "You forum tab request has been rejected", userItemFormat, false, "declineforum", null, uid);
            }
        });
    }
}