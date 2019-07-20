package com.zconnect.zutto.zconnect.holders;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;

import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;

import java.util.HashMap;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;


public class newRequestViewHolder extends RecyclerView.ViewHolder {
    public TextView newRequestName, postedByNameInLocation;
    private Button acceptUserButton, declineUserButton;
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
        acceptUserButton.setOnClickListener(view -> {
            final DatabaseReference requestedLocationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("admin").child("requests");
            final DatabaseReference addNewLocation=FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("locations");


            requestedLocationDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        addNewLocation.child(key).child("locationName").setValue(dataSnapshot.child(key).child("Name").getValue());
                        addNewLocation.child(key).child("PostTimeMillis").setValue(System.currentTimeMillis());
                        addNewLocation.child(key).child("PostedBy").child("Username").setValue(dataSnapshot.child(key).child("PostedBy").child("Username").getValue());
                        addNewLocation.child(key).child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child(key).child("PostedBy").child("ImageThumb").getValue());
                    } catch (Exception e) {
                        Log.d("ERROR",e.toString());
                    }

                    //todo : Implement push notification for the admin request feature
                    //todo : add location , postedBy details to database, currently only posttime millis is getting stored in the database
                    GlobalFunctions.inAppNotifications("has accepted your cabpool request", "Your cabpool location "+dataSnapshot.child(key).child("Name").getValue()+ " is now approved", userItemFormat, false, "adminrequest", null, uid);
                    requestedLocationDatabaseReference.child(key).removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        declineUserButton.setOnClickListener(view -> {

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

        });
    }

    public void setAcceptDeclineButtonForForumTabs(final String key, final String uid, final UserItemFormat userItemFormat) {

        final DatabaseReference requestForumTabs = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/admin/requests");
        final DatabaseReference forumTab = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/forums/tabs");

        acceptUserButton.setOnClickListener(view -> {
            requestForumTabs.child(key).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    /*forumTab.child(dataSnapshot.getValue().toString().trim()).child("name").setValue(dataSnapshot.getValue().toString().trim().toLowerCase());
                    forumTab.child(dataSnapshot.getValue().toString().trim()).child("UID").setValue(dataSnapshot.getValue().toString().trim());*/
                    try {
                        final HashMap<String, Object> map = new HashMap<>();
                        map.put("name", dataSnapshot.getValue().toString().trim().toLowerCase());
                        map.put("UID", dataSnapshot.getValue().toString().trim());

                        forumTab.child(dataSnapshot.getValue().toString().trim()).setValue(map);
                        requestForumTabs.child(key).removeValue();
                    }
                    catch (Exception e){
                        Log.d("Error",e.toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            GlobalFunctions.inAppNotifications("has accepted your forum tab request", "You forum tab request has been approved", userItemFormat, false, "acceptforum", null, uid);
        });
        declineUserButton.setOnClickListener(view -> {

            DatabaseReference requestForumTabs1 = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/admin/requests");
            requestForumTabs1.child(key).removeValue();
            GlobalFunctions.inAppNotifications("has declined your forum tab request", "You forum tab request has been rejected", userItemFormat, false, "declineforum", null, uid);
        });
    }
    public void setAcceptDeclineButtonForLinks(final String key, final String uid, final UserItemFormat userItemFormat) {

        final DatabaseReference requestLinks = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/admin/requests");
        final DatabaseReference linksRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/links");

        acceptUserButton.setOnClickListener(view -> {
            requestLinks.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    /*forumTab.child(dataSnapshot.getValue().toString().trim()).child("name").setValue(dataSnapshot.getValue().toString().trim().toLowerCase());
                    forumTab.child(dataSnapshot.getValue().toString().trim()).child("UID").setValue(dataSnapshot.getValue().toString().trim());*/
                    try {
                        final HashMap<String, Object> map = new HashMap<>();
                        map.put("linkURL", dataSnapshot.child("link").getValue().toString().trim());
                        map.put("title", dataSnapshot.child("Name").getValue().toString().trim());
                        map.put("UID",dataSnapshot.getKey());

                        linksRef.child(dataSnapshot.getKey()).setValue(map);
                        requestLinks.child(key).removeValue();
                    }
                    catch (Exception e){
                        Log.d("Error",e.toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
//            GlobalFunctions.inAppNotifications("has accepted your Links request", "You Link request has been approved", userItemFormat, false, "acceptforum", null, uid);
        });
        declineUserButton.setOnClickListener(view -> {

            DatabaseReference requestForumTabs1 = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/admin/requests");
            requestForumTabs1.child(key).removeValue();
//            GlobalFunctions.inAppNotifications("has declined your forum tab request", "You forum tab request has been rejected", userItemFormat, false, "declineforum", null, uid);
        });
    }
}