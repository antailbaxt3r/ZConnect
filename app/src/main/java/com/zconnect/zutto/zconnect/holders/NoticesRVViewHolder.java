package com.zconnect.zutto.zconnect.holders;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.itemFormats.NoticeItemFormat;

import static android.content.Context.MODE_PRIVATE;

public class NoticesRVViewHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private ImageView image;
    View mView;

    public View.OnClickListener mListener;

    private SharedPreferences communitySP;
    private String communityReference;

    SharedPreferences sharedPref;
    Boolean status;
    private DatabaseReference Notices;
    public Boolean flag;

    private FirebaseAuth mAuth;

    public NoticesRVViewHolder(final View itemView) {
        super(itemView);
        mView = itemView;
        communitySP = itemView.getContext().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
        Notices = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("Notices");
        sharedPref = itemView.getContext().getSharedPreferences("guestMode", MODE_PRIVATE);
        status = sharedPref.getBoolean("mode", false);
        name = (TextView) itemView.findViewById(R.id.name);
        image = (ImageView) itemView.findViewById(R.id.image);
    }

    public void setImage(Uri imageuri)
    {
        image.setImageURI(imageuri);
    }
    public void setText(String text) {
        name.setText(text);
    }






    }





