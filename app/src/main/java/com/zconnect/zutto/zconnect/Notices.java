package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.NoticeRVAdapter;
import com.zconnect.zutto.zconnect.addActivities.AddNotices;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.ExpiryDateItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NoticeItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

public class Notices extends BaseActivity {
    private DatabaseReference noticesRef, mUserDetails;
    private RecyclerView noticesRV;
    private FirebaseAuth mAuth;
    private Vector<NoticeItemFormat> noticesItemFormats = new Vector<>();
    private NoticeRVAdapter noticeRVAdapter;
    private ValueEventListener mListener;
    private FloatingActionButton add_photo;
    String fetchedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);

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
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        mUserDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        noticesRV =(RecyclerView)findViewById(R.id.photos);
        noticesRV.setLayoutManager(new GridLayoutManager(this, 2));
        noticesRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("notices").child("activeNotices");
        add_photo =(FloatingActionButton)findViewById(R.id.add_photo);
        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notices.this.startActivity(new Intent(Notices.this,AddNotices.class));
            }
        });

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                noticesItemFormats.clear();

                for(DataSnapshot shot: dataSnapshot.getChildren()) {
                    try{
                        NoticeItemFormat singleObject;
                        singleObject = shot.getValue(NoticeItemFormat.class);

                        if(singleObject.getTitle()!=null && singleObject.getImageThumbURL()!=null && singleObject.getImageURL()!=null) {
                            if (singleObject.getExpiryDate()==null)
                            noticesItemFormats.add(singleObject);
                            else
                            {
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat output = new SimpleDateFormat("yyyyMMdd");
                                String date = output.format(c.getTime());

                                String yEAR = singleObject.getExpiryDate().getYear() + "";
                                String mONTH = (singleObject.getExpiryDate().getMonth()+1) + "";
                                if (mONTH.length()<2)
                                    mONTH = "0"+mONTH;
                                String dAY = singleObject.getExpiryDate().getDay() + "";
                                if (dAY.length()<2)
                                    dAY = "0"+dAY;

                                fetchedDate = yEAR + mONTH + dAY;

                                if (date.compareTo(fetchedDate)<=0)
                                    noticesItemFormats.add(singleObject);
                                else
                                    continue;
                            }
                        }
                    }catch (Exception e){}
                }

                /*Calendar c = Calendar.getInstance();
                SimpleDateFormat output = new SimpleDateFormat("yyyyMMdd");
                String date = output.format(c.getTime());*/

                //for (int i = 0; i < noticesItemFormats.size(); i++) {}

                mUserDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       UserItemFormat currentUser = dataSnapshot.getValue(UserItemFormat.class);
                       noticeRVAdapter = new NoticeRVAdapter(noticesItemFormats,getApplicationContext(),currentUser.getUserType());
                       noticesRV.setAdapter(noticeRVAdapter);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
            };

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            };
        };



    }

    @Override
    protected void onStart() {
        super.onStart();
        noticesRef.addValueEventListener(mListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        noticesRef.removeEventListener(mListener);
    }
}
