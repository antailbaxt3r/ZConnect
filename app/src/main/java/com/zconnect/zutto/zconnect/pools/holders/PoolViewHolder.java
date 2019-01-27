package com.zconnect.zutto.zconnect.pools.holders;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.pools.AddPoolItemActivity;
import com.zconnect.zutto.zconnect.pools.PoolItemDetailActivity;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

import java.util.Date;

public class PoolViewHolder extends RecyclerView.ViewHolder {


    private SimpleDraweeView poolImage;
    private TextView name, description, count, deliveryTime;
    private ImageButton btn_like;
    private Boolean isLiked;
    private String userUID;
    private Button activateBtn;

    private Pool pool;

    public PoolViewHolder(View itemView) {
        super(itemView);


        attachID();
    }

    private void attachID() {
        name = itemView.findViewById(R.id.pool_name);
        poolImage = itemView.findViewById(R.id.pool_logo);
        description = itemView.findViewById(R.id.pool_description);
        count = itemView.findViewById(R.id.pool_count);
        deliveryTime = itemView.findViewById(R.id.delivery_time);
        btn_like = itemView.findViewById(R.id.btn_like);
        isLiked = false;
        userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        activateBtn = itemView.findViewById(R.id.activate_btn);
    }

    public void populate(final Pool pool) {
        this.pool = pool;
        Log.d(this.getClass().getName(), "populate : " + pool.getName());
        name.setText(pool.getName());
        description.setText(pool.getDescription());
        poolImage.setImageURI(pool.getImageURL());
        if (pool.getStatus().compareTo(Pool.STATUS_UPCOMING) == 0) {
            count.setText(String.valueOf(pool.getUpvote()));
        } else {
            count.setText(String.valueOf(pool.getTotalOrder()));
        }
        Date d = new Date();
        d.setTime(pool.getDeliveryTime());
        deliveryTime.setText(d.toString());

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pool.isUpcoming()) {
                    Intent intent = new Intent(itemView.getContext(), PoolItemDetailActivity.class);
                    intent.putExtra("newPool", pool.getBundle());
                    itemView.getContext().startActivity(intent);
                } else if (pool.isActive()) {
                    Intent intent = new Intent(itemView.getContext(), AddPoolItemActivity.class);
                    intent.putExtra("newPool", pool.getBundle());
                    itemView.getContext().startActivity(intent);
                }
            }
        });

        if (pool.isUpcoming()) {
            setActivateBtn();
            DatabaseReference userReference= FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(userUID);

            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserItemFormat tempUser = dataSnapshot.getValue(UserItemFormat.class);
                    if(dataSnapshot.hasChild("userType")){
                        if(tempUser.getUserType().equals(UsersTypeUtilities.KEY_ADMIN)){
                            activateBtn.setVisibility(View.VISIBLE);
                        }else {
                            activateBtn.setVisibility(View.GONE);
                        }
                    }else {
                        activateBtn.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            activateBtn.setVisibility(View.VISIBLE);
            if (pool.getUpvoteList().containsKey(userUID)) {
                isLiked = true;
            } else {
                isLiked = false;
            }
            setLikeView();
            btn_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleLike();
                }
            });
        } else {
            activateBtn.setVisibility(View.GONE);
            btn_like.setVisibility(View.GONE);
        }


    }

    private void toggleLike() {
        btn_like.setEnabled(false);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(Pool.URL_POOL_UP_VOTE,communityReference , pool.getPoolPushID(), userUID));
        if (isLiked) {
            ref.setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    btn_like.setEnabled(true);
                    isLiked = false;
                    setLikeView();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    btn_like.setEnabled(true);
                    //TODO toast error on like
                }
            });
        } else {
            ref.setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    btn_like.setEnabled(true);
                    isLiked = true;
                    setLikeView();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    btn_like.setEnabled(true);
                    //TODO toast error on like

                }
            });
        }
    }

    private void setLikeView() {
        Drawable red = itemView.getContext().getResources().getDrawable(R.drawable.ic_favorite_red_24dp);
        Drawable black = itemView.getContext().getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp);
        if (isLiked) {
            btn_like.setImageDrawable(red);
        } else {
            btn_like.setImageDrawable(black);
        }
    }

    private void setActivateBtn(){

        activateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference(String.format(Pool.URL_POOL,communityReference)).child(pool.getPoolPushID()).child("status").setValue("active");
            }
        });
    }

}
