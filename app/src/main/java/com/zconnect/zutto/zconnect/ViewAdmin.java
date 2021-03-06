package com.zconnect.zutto.zconnect;


import android.content.SharedPreferences;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.content.ContextCompat;


import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.ViewAdminsRVAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import java.util.Vector;



public class ViewAdmin extends BaseActivity {
    Vector<String> admname = new Vector<>();
    Vector<String> admimg = new Vector<>();
    Vector<String> adminUID = new Vector<>();
    FirebaseAuth mAuth;
    FirebaseUser user;
DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ViewAdminsRVAdapter viewAdminsRVAdapter;
    TextView error;


    private SharedPreferences communitySP;
    public String communityReference;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_admins);
        Toolbar toolbar = (Toolbar) findViewById(R.id.view_admins_app_bar_home);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
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
            getSupportActionBar().setTitle("Community Admins");
        }


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

        communitySP = ViewAdmin.this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("admins");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot childsnapshot: dataSnapshot.getChildren()){
                    Log.d((String) childsnapshot.child("Username").getValue(), "onDataChange: ");
                    admname.add((String) childsnapshot.child("Username").getValue());

                    adminUID.add((String)childsnapshot.getKey().toString());
                    Log.d("ADMIN UID",adminUID.toString());

                    if(!("").equals(childsnapshot.child("ImageThumb").getValue())) {
                        admimg.add((String) childsnapshot.child("ImageThumb").getValue());
                    }
                    else{
                        //TODO (Arjun) Change required: default profile pic instead of random image URL
                        admimg.add("https://firebasestorage.googleapis.com/v0/b/zconnectmulticommunity.appspot.com/o/defaultProfile.png?alt=media&token=4d4ec5db-41bd-4d04-9be5-12fc5a1bdd6d");
                    }
                }
                recyclerView = (RecyclerView) findViewById(R.id.view_admins_rv);
                viewAdminsRVAdapter = new ViewAdminsRVAdapter(ViewAdmin.this,admimg,admname,adminUID,communityReference);
                recyclerView.setLayoutManager(new GridLayoutManager(ViewAdmin.this,2));
                recyclerView.setAdapter(viewAdminsRVAdapter);
                recyclerView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_cab_pool_main, container, false);


    }
}