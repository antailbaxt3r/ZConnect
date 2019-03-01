package com.zconnect.zutto.zconnect.pools.holders;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.pools.ActivePoolDetailsActivity;
import com.zconnect.zutto.zconnect.pools.UpcomingPoolDetailsActivity;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.util.HashMap;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class PoolViewHolder extends RecyclerView.ViewHolder {


    private SimpleDraweeView poolImage;
    private TextView name, description, count, orderDeadlineTime, orderDealineSubtext, deliveryDay;
    private ImageButton btn_like;
    private Boolean isLiked;
    private String userUID;
    private Button activateBtn, orderBtn;
    private RelativeLayout countdownWrapper;
    private FrameLayout card;
    private LinearLayout orderDeadlineInfoLayout;
    private SimpleDraweeView poolBgImage;
    private ImageView timerIcon;

    private TextView countdownT1, countdownT2, countdownT3, countdownT4, countdownLabel1, countdownLabel2;

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
        orderDeadlineTime = itemView.findViewById(R.id.order_deadline_time);
        orderDealineSubtext = itemView.findViewById(R.id.order_deadline_subtext);
        deliveryDay = itemView.findViewById(R.id.delivery_day);
        btn_like = itemView.findViewById(R.id.btn_like);
        isLiked = false;
        userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        activateBtn = itemView.findViewById(R.id.activate_btn);
        orderBtn = itemView.findViewById(R.id.order_btn);
        card = itemView.findViewById(R.id.card);
        countdownT1 = itemView.findViewById(R.id.countdown_t1);
        countdownT2 = itemView.findViewById(R.id.countdown_t2);
        countdownT3 = itemView.findViewById(R.id.countdown_t3);
        countdownT4 = itemView.findViewById(R.id.countdown_t4);
        countdownLabel1 = itemView.findViewById(R.id.countdown_label1);
        countdownLabel2 = itemView.findViewById(R.id.countdown_label2);
        countdownWrapper = itemView.findViewById(R.id.countdown_wrapper_layout);
        countdownWrapper.setVisibility(View.GONE);
        orderDeadlineInfoLayout = itemView.findViewById(R.id.order_deadline_info_layout);
        poolBgImage = itemView.findViewById(R.id.pool_bg_image);
        timerIcon = itemView.findViewById(R.id.timer_icon);
    }

    public void populate(final Pool pool) {
        this.pool = pool;
        Log.d(this.getClass().getName(), "populate : " + pool.getPoolInfo().getName());
        name.setText(pool.getPoolInfo().getName());
        description.setText(pool.getPoolInfo().getDescription());
        poolImage.setImageURI(pool.getPoolInfo().getImageThumb());
        if(pool.getPoolInfo().getImageURL()!=null)
            poolBgImage.setImageURI(pool.getPoolInfo().getImageURL());
        setBackgroundColors();

        if (pool.getStatus().compareTo(Pool.STATUS_UPCOMING) == 0) {
            count.setText(String.valueOf(pool.getUpvote()));
        } else {
            count.setText(String.valueOf(pool.getTotalOrder()));
        }
        if(pool.isOrderReceivingStatus())
        {
            TimeUtilities tu = new TimeUtilities(pool.getTimestampOrderReceivingDeadline());
            String deliveryTimeText = tu.getWeekName("SHORT") + ", " + tu.getTimeInHHMMAPM();
            String deliveryDayText = tu.getMonthName("SHORT") + " " + tu.getDateTime().getDayOfMonth();
            orderDeadlineTime.setText(deliveryTimeText);
            orderDealineSubtext.setVisibility(View.VISIBLE);
            deliveryDay.setText(deliveryDayText);
        }
        else
        {
            orderDealineSubtext.setVisibility(View.GONE);
            orderDeadlineTime.setText("CLOSED");
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pool.isUpcoming()) {

                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta= new HashMap<>();
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_SHOPS_POOL_OPEN_UPCOMING);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);

                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();

                    Intent intent = new Intent(itemView.getContext(), UpcomingPoolDetailsActivity.class);
                    intent.putExtra("pool", pool);
                    itemView.getContext().startActivity(intent);
                } else if (pool.isActive()) {

                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta= new HashMap<>();
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_SHOPS_POOL_OPEN_ACTIVE);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);

                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();
                    Intent intent = new Intent(itemView.getContext(), ActivePoolDetailsActivity.class);
                    intent.putExtra("pool", pool);
                    itemView.getContext().startActivity(intent);
                }
            }
        };
        card.setOnClickListener(listener);
        orderBtn.setOnClickListener(listener);

        handleOrderReceivingStatus(pool.isOrderReceivingStatus());

        if (pool.isUpcoming()) {
            setActivateBtn();
            orderDeadlineInfoLayout.setVisibility(View.GONE);
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
            orderDeadlineInfoLayout.setVisibility(View.VISIBLE);
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

    private void setBackgroundColors() {
        switch (getAdapterPosition()%4)
        {
            case 0:
                name.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.lightgreen800), PorterDuff.Mode.SRC_ATOP);
                orderDeadlineInfoLayout.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.red700), PorterDuff.Mode.SRC_ATOP);
                //next release code
//                countdownWrapper.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.red700));
//                countdownT1.setTextColor(itemView.getContext().getResources().getColor(R.color.red700));
//                countdownT2.setTextColor(itemView.getContext().getResources().getColor(R.color.red700));
//                countdownT3.setTextColor(itemView.getContext().getResources().getColor(R.color.red700));
//                countdownT4.setTextColor(itemView.getContext().getResources().getColor(R.color.red700));
//                countdownT1.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.red200));
//                countdownT2.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.red200));
//                countdownT3.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.red200));
//                countdownT4.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.red200));
//                countdownLabel1.setTextColor(itemView.getContext().getResources().getColor(R.color.red200));
//                countdownLabel2.setTextColor(itemView.getContext().getResources().getColor(R.color.red200));
                break;
            case 1:
                name.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.indigo800), PorterDuff.Mode.SRC_ATOP);
                orderDeadlineInfoLayout.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.green700), PorterDuff.Mode.SRC_ATOP);
                //next release code
//                countdownWrapper.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.green700));
//                countdownT1.setTextColor(itemView.getContext().getResources().getColor(R.color.green700));
//                countdownT2.setTextColor(itemView.getContext().getResources().getColor(R.color.green700));
//                countdownT3.setTextColor(itemView.getContext().getResources().getColor(R.color.green700));
//                countdownT4.setTextColor(itemView.getContext().getResources().getColor(R.color.green700));
//                countdownT1.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.green200));
//                countdownT2.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.green200));
//                countdownT3.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.green200));
//                countdownT4.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.green200));
//                countdownLabel1.setTextColor(itemView.getContext().getResources().getColor(R.color.green200));
//                countdownLabel2.setTextColor(itemView.getContext().getResources().getColor(R.color.green200));
                break;
            case 2:
                name.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.red400), PorterDuff.Mode.SRC_ATOP);
                orderDeadlineInfoLayout.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.yellow700), PorterDuff.Mode.SRC_ATOP);
                //next release code
//                countdownWrapper.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.yellow700));
//                countdownT1.setTextColor(itemView.getContext().getResources().getColor(R.color.yellow700));
//                countdownT2.setTextColor(itemView.getContext().getResources().getColor(R.color.yellow700));
//                countdownT3.setTextColor(itemView.getContext().getResources().getColor(R.color.yellow700));
//                countdownT4.setTextColor(itemView.getContext().getResources().getColor(R.color.yellow700));
//                countdownT1.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.yellow200));
//                countdownT2.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.yellow200));
//                countdownT3.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.yellow200));
//                countdownT4.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.yellow200));
//                countdownLabel1.setTextColor(itemView.getContext().getResources().getColor(R.color.yellow200));
//                countdownLabel2.setTextColor(itemView.getContext().getResources().getColor(R.color.yellow200));
                break;
            case 3:
                name.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.lightblue600), PorterDuff.Mode.SRC_ATOP);
                orderDeadlineInfoLayout.getBackground().setColorFilter(itemView.getContext().getResources().getColor(R.color.deeppurple700), PorterDuff.Mode.SRC_ATOP);
                //next release code
//                countdownWrapper.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.deeppurple700));
//                countdownT1.setTextColor(itemView.getContext().getResources().getColor(R.color.deeppurple700));
//                countdownT2.setTextColor(itemView.getContext().getResources().getColor(R.color.deeppurple700));
//                countdownT3.setTextColor(itemView.getContext().getResources().getColor(R.color.deeppurple700));
//                countdownT4.setTextColor(itemView.getContext().getResources().getColor(R.color.deeppurple700));
//                countdownT1.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.deeppurple200));
//                countdownT2.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.deeppurple200));
//                countdownT3.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.deeppurple200));
//                countdownT4.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.deeppurple200));
//                countdownLabel1.setTextColor(itemView.getContext().getResources().getColor(R.color.deeppurple200));
//                countdownLabel2.setTextColor(itemView.getContext().getResources().getColor(R.color.deeppurple200));
                break;
        }
    }

    private void handleOrderReceivingStatus(boolean flag) {
        if(flag)
        {
            timerIcon.setVisibility(View.VISIBLE);
            deliveryDay.setVisibility(View.VISIBLE);
        }
        else
        {
            timerIcon.setVisibility(View.GONE);
            deliveryDay.setVisibility(View.GONE);
            View.OnClickListener closedListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(itemView.getContext(),"This pool is no longer accepting orders.",Toast.LENGTH_SHORT).show();
                }
            };
            card.setOnClickListener(closedListener);
            orderBtn.setOnClickListener(closedListener);
        }
    }

}
