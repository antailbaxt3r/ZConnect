package com.zconnect.zutto.zconnect.holders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;

import com.zconnect.zutto.zconnect.CabPoolLocations;

import java.util.HashMap;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class newRequestViewHolder extends RecyclerView.ViewHolder {

    public TextView newRequestName;
    public Button acceptUserButton, declineUserButton;

    public newRequestViewHolder(View itemView)
    {
        super(itemView);
        newRequestName = (TextView) itemView.findViewById(R.id.name_new_request);
        acceptUserButton = (Button) itemView.findViewById(R.id.accept_new_request);
        declineUserButton = (Button) itemView.findViewById(R.id.decline_new_request);
    }

    public void setAcceptDeclineButtonForLocations(final String key) {

        acceptUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference requestedLocationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("admin").child("requests");
                final DatabaseReference addNewLocation=FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("locations").child(key);

                /*Long postTimeMillis = System.currentTimeMillis();
                addNewLocation.child("PostTimeMillis").setValue(postTimeMillis);*/

                final HashMap<String, Object> map = new HashMap<>();
                map.put("PostTimeMillis", System.currentTimeMillis());

                requestedLocationDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {

                            /*addNewLocation.child("locationName").setValue(dataSnapshot.child(key).child("locationName").getValue().toString());
                            addNewLocation.child("PostedBy").child("Username").setValue(dataSnapshot.child(key).child("PostedBy").child("Username").getValue().toString());
                            addNewLocation.child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child(key).child("PostedBy").child("ImageThumb").getValue().toString());*/

                            map.put("locationName",dataSnapshot.child(key).child("locationName").getValue().toString());

                            HashMap<String, Object> postedBy = new HashMap<>();
                            postedBy.put("Username",dataSnapshot.child(key).child("PostedBy").child("Username").getValue().toString());
                            postedBy.put("ImageThumb",dataSnapshot.child(key).child("PostedBy").child("ImageThumb").getValue().toString());

                            map.put("PostedBy",postedBy);
                            addNewLocation.setValue(map);
                        }
                        catch (Exception e){}
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                requestedLocationDatabaseReference.child(key).removeValue();
            }
        });

        declineUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference requestedLocationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("admin").child("requests");
                requestedLocationDatabaseReference.child(key).removeValue();

            }
        });
    }

    public void setAcceptDeclineButtonForForumTabs(final String key) {

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
            }
        });

        declineUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestForumTabs.child(key).removeValue();
            }
        });

    }
}