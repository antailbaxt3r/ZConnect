package com.zconnect.zutto.zconnect.holders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

/**
 * Created by Lokesh Garg on 02-04-2018.
 */

public class CabPoolLocationRVViewHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private ImageButton deleteButton;
    private DatabaseReference locationReference,archivedLocation;
    private Boolean flag;

    public CabPoolLocationRVViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.location_name);
        deleteButton = (ImageButton) itemView.findViewById(R.id.delete_location);

    }

    public void setLocationName(String locationName){
        name.setText(locationName);
    }

    public void setDeleteButton(final String locationUID, final String locationName){
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlert(locationUID,locationName);
            }
        });
    }

    public void setAlert(String locationUID, final String locationName){

        locationReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("locations").child(locationUID);
        archivedLocation = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("archivedLocations").child(locationUID);
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(itemView.getContext());
        builder.setMessage("Are you sure you want to delete \"" +locationName+"\" from locations")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        flag=false;
                        locationReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!flag) {
                                    flag=true;
                                    archivedLocation.setValue(dataSnapshot.getValue());
                                    locationReference.removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        final android.app.AlertDialog dialog = builder.create();

        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(itemView.getContext().getResources().getColor(R.color.colorHighlight));

    }
}


