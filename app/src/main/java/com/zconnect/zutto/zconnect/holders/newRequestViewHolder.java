package com.zconnect.zutto.zconnect.holders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;

import com.zconnect.zutto.zconnect.CabPoolLocations;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class newRequestViewHolder extends RecyclerView.ViewHolder {

    public TextView newRequestName, postedByNameInLocation;
    public Button acceptUserButton, declineUserButton;
    public SimpleDraweeView postedByImageLocation;

    public newRequestViewHolder(View itemView)
    {
        super(itemView);
        newRequestName = itemView.findViewById(R.id.name_new_request);
        acceptUserButton = itemView.findViewById(R.id.accept_new_request);
        declineUserButton = itemView.findViewById(R.id.decline_new_request);
        postedByImageLocation = itemView.findViewById(R.id.postedByImageLocation);
        postedByNameInLocation = itemView.findViewById(R.id.postedByInLocation);
    }

    public void setAcceptDeclineButton(final String key) {

        acceptUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference requestedLocationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("admin").child("requests").child("requestedLocations");

                final DatabaseReference newPush=FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("locations").push();

                Long postTimeMillis = System.currentTimeMillis();
                newPush.child("PostTimeMillis").setValue(postTimeMillis);

                requestedLocationDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try { newPush.child("locationName").setValue(dataSnapshot.child(key).child("locationName").getValue().toString());
                            newPush.child("PostedBy").child("Username").setValue(dataSnapshot.child(key).child("PostedBy").child("Username").getValue().toString());
                            newPush.child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child(key).child("PostedBy").child("ImageThumb").getValue().toString()); }
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

                DatabaseReference requestedLocationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("admin").child("requests").child("requestedLocations");
                requestedLocationDatabaseReference.child(key).removeValue();

            }
        });
    }
}