package com.zconnect.zutto.zconnect;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_CABPOOL;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_EVENT;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_OFFERS;
import static com.zconnect.zutto.zconnect.KeyHelper.KEY_STOREROOM;

public class NotificationSettings extends AppCompatActivity {
    Switch switch_events;
    Switch switch_cabPool;
    Switch switch_storeroom;
    Switch switch_offers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);
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
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }


        switch_cabPool=(Switch) findViewById(R.id.switch_cabpool);
        switch_events=(Switch) findViewById(R.id.switch_events);
        switch_storeroom=(Switch) findViewById(R.id.switch_storeroom);
        switch_offers=(Switch) findViewById(R.id.switch_offers);

        String Uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(Uid).child("NotificationChannels");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Setting data=dataSnapshot.getValue(Setting.class);

                switch_cabPool.setChecked(data.getAddCabPool());
                switch_events.setChecked(data.getAddEvent());
                switch_offers.setChecked(data.getOffers());
                switch_storeroom.setChecked(data.getStoreRoom());

                if(data.getAddCabPool()){
                        FirebaseMessaging.getInstance().subscribeToTopic(KEY_CABPOOL);
                }else{
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(KEY_CABPOOL);
                }

                if(data.getAddEvent()){
                    FirebaseMessaging.getInstance().subscribeToTopic(KEY_EVENT);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(KEY_EVENT);
                }

                if(data.getOffers()){
                    FirebaseMessaging.getInstance().subscribeToTopic(KEY_OFFERS);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(KEY_OFFERS);
                }

                if(data.getStoreRoom()){
                    FirebaseMessaging.getInstance().subscribeToTopic(KEY_STOREROOM);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(KEY_STOREROOM);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        switch_events.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    FirebaseMessaging.getInstance().subscribeToTopic(KEY_EVENT);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(KEY_EVENT);
                }
                databaseReference.child(KEY_EVENT).setValue(isChecked);

            }
        });

        switch_storeroom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseMessaging.getInstance().subscribeToTopic(KEY_STOREROOM);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(KEY_STOREROOM);
                }
                databaseReference.child(KEY_STOREROOM).setValue(isChecked);

            }
        });

        switch_offers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseMessaging.getInstance().subscribeToTopic(KEY_OFFERS);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(KEY_OFFERS);
                }
                databaseReference.child(KEY_OFFERS).setValue(isChecked);

            }
        });


        switch_cabPool.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseMessaging.getInstance().subscribeToTopic(KEY_CABPOOL);
                }else{
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(KEY_CABPOOL);
                }
                databaseReference.child(KEY_CABPOOL).setValue(isChecked);

            }
        });

    }
}


 class Setting{

    private Boolean AddCabPool;
    private Boolean AddEvent;
    private Boolean StoreRoom;
    private Boolean Offers;

    public Setting(){

    }


    public Setting(Boolean AddCabPool,Boolean AddEvent,Boolean EventBoosted,Boolean StoreRoom,Boolean Offers ){
        this.AddCabPool=AddCabPool;
        this.AddEvent=AddEvent;
        this.StoreRoom=StoreRoom;
        this.Offers=Offers;
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
}