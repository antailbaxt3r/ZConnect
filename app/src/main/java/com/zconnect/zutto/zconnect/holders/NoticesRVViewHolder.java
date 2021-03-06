package com.zconnect.zutto.zconnect.holders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.OpenProductDetails;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.viewImage;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ExpiryDateItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NoticeItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class NoticesRVViewHolder extends RecyclerView.ViewHolder {

    private TextView name;
    private SimpleDraweeView image;
    private ImageButton deleteButton;
    private TextView expiryDateTV;

    private FrameLayout expiryDateFL;
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
        image = (SimpleDraweeView) itemView.findViewById(R.id.image);
        deleteButton = (ImageButton) itemView.findViewById(R.id.delete_notice);
        expiryDateFL = itemView.findViewById(R.id.notice_expiry_date_layout);
        expiryDateTV = itemView.findViewById(R.id.notice_expiry_date_tv);
    }

    public void setImage(final String imageThumbURL, final String imageURL, final String title)
    {
        image.setImageURI(imageThumbURL);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta = new HashMap<>();

                meta.put("type","fromFeature");

                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_NOTICES_OPEN_NOTICE);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                ProgressDialog mProgress = new ProgressDialog(itemView.getContext());
                mProgress.setMessage("Loading...");
                mProgress.show();
                animate((Activity) itemView.getContext(), title, imageURL, image);
                mProgress.dismiss();
            }
        });
    }
    public void setText(String title) {
        name.setText(title);
    }

    public void setDeleteButton(String userType,final String key){
        if(userType.equals(UsersTypeUtilities.KEY_ADMIN)){
            deleteButton.setVisibility(View.VISIBLE);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta = new HashMap<>();

                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_NOTICES_DELETE_NOTICE);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);

                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();

                    deleteDialog(key);
                }
            });
        }
    }

    private void deleteDialog(final String key){

        final DatabaseReference activeNotices = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("notices").child("activeNotices").child(key);
        final DatabaseReference deletedNotices = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("notices").child("deletedNotices");

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(itemView.getContext());
        builder.setMessage("Are you sure you want to delete this message?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activeNotices.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                deletedNotices.child(key).setValue(dataSnapshot.getValue());
                                activeNotices.removeValue();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        final android.app.AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(itemView.getResources().getColor(R.color.colorHighlight));

    }
    public void animate(final Activity activity, final String name, String url, ImageView productImage) {
        final Intent i = new Intent(itemView.getContext(), viewImage.class);
        i.putExtra("currentEvent", name);
        i.putExtra("eventImage", url);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, productImage, itemView.getResources().getString(R.string.transition_string));

        itemView.getContext().startActivity(i, optionsCompat.toBundle());
    }

    public void setExpiryDate(ExpiryDateItemFormat expiryDateItemFormat) {

        String expiryDateText = expiryDateItemFormat.getDay() + "/" + (expiryDateItemFormat.getMonth()+1) + "/" + expiryDateItemFormat.getYear()%100;
        expiryDateFL.setVisibility(View.VISIBLE);
        expiryDateTV.setText(expiryDateText);
    }

    public void hideExpiryDateLayout() {
        expiryDateFL.setVisibility(View.GONE);
    }
}





