package com.zconnect.zutto.zconnect;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;

import static com.zconnect.zutto.zconnect.R.drawable.ic_arrow_back_black_24dp;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_CABPOOL;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_EVENT;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_FORUMS_ADD;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_OFFERS;
import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_STOREROOM;

public class NotificationSettings extends BaseActivity {

    Switch switch_events;
    Switch switch_cabPool;
    Switch switch_storeroom;
    Switch switch_offers;
    Switch switch_forums;
    DatabaseReference databaseReference;
    String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        setTitle("Notification Settings");
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(ic_arrow_back_black_24dp);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }


        switch_cabPool=(Switch) findViewById(R.id.switch_cabpool);
        switch_events=(Switch) findViewById(R.id.switch_events);
        switch_storeroom=(Switch) findViewById(R.id.switch_storeroom);
        switch_offers=(Switch) findViewById(R.id.switch_offers);
        switch_forums=(Switch) findViewById(R.id.switch_forums);

        Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(Uid).child("NotificationChannels");

        initialiseNotifications();

        switch_events.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_ADD + communityReference);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_ADD + communityReference);
                }
                databaseReference.child(KEY_EVENT).setValue(isChecked);
            }
        });

        switch_storeroom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD + communityReference);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD + communityReference);
                }
                databaseReference.child(KEY_STOREROOM).setValue(isChecked);

            }
        });

        switch_offers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_OFFERS_ADD + communityReference);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_OFFERS_ADD + communityReference);
                }
                databaseReference.child(KEY_OFFERS).setValue(isChecked);

            }
        });


        switch_cabPool.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD + communityReference);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD + communityReference);
                }
                databaseReference.child(KEY_CABPOOL).setValue(isChecked);

            }
        });

        switch_forums.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD + communityReference);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD + communityReference);
                }
                databaseReference.child(KEY_FORUMS_ADD).setValue(isChecked);

            }
        });
    }

    public void initialiseNotifications(){

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    Setting data = dataSnapshot.getValue(Setting.class);

                    switch_cabPool.setChecked(data.getAddCabPool());
                    switch_events.setChecked(data.getAddEvent());
                    switch_offers.setChecked(data.getOffers());
                    switch_storeroom.setChecked(data.getStoreRoom());
                    switch_forums.setChecked(data.getAddForum());


                    if (data.getAddCabPool()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD + communityReference);
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD + communityReference);
                    }

                    if (data.getAddEvent()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_ADD + communityReference);
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_ADD + communityReference);
                    }

                    if (data.getOffers()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_OFFERS_ADD + communityReference);
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_OFFERS_ADD + communityReference);
                    }

                    if (data.getStoreRoom()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD + communityReference);
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_PRODUCT_ADD + communityReference);
                    }

                    if (data.getAddForum()) {
                        FirebaseMessaging.getInstance().subscribeToTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD + communityReference);
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(NotificationIdentifierUtilities.KEY_NOTIFICATION_FORUM_ADD + communityReference);
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}



 class Setting{

    private Boolean AddCabPool;
    private Boolean AddEvent;
    private Boolean StoreRoom;
    private Boolean Offers;
    private Boolean AddForum;

    public Setting(){

    }


    public Setting(Boolean AddCabPool,Boolean AddEvent,Boolean StoreRoom,Boolean Offers,Boolean AddForum ){
        this.AddCabPool=AddCabPool;
        this.AddEvent=AddEvent;
        this.StoreRoom=StoreRoom;
        this.Offers=Offers;
        this.AddForum = AddForum;
    }

     public Boolean getAddEvent(){
            return AddEvent;
     }


     public Boolean getStoreRoom(){
         return StoreRoom;
     }

     public Boolean getOffers(){
         return Offers;
     }

     public Boolean getAddCabPool(){
         return AddCabPool;
     }

     public void setAddCabPool(Boolean AddCabPool){
         this.AddCabPool=AddCabPool;
     }

     public void setAddEvent(Boolean AddEvent){
         this.AddEvent=AddEvent;
     }

     public void setStoreRoom(Boolean StoreRoom){
         this.StoreRoom=StoreRoom;
     }


     public void setOffers(Boolean Offers){
         this.Offers=Offers;
     }


     public Boolean getAddForum() {
         return AddForum;
     }

     public void setAddForum(Boolean addForum) {
         AddForum = addForum;
     }
}