package com.zconnect.zutto.zconnect;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.CabItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;

import java.util.Vector;

public class MyRides extends BaseActivity {
    RecyclerView poolrv;
    DatabaseReference pool = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("allCabs");
    Query query = pool.orderByChild("time");
    TextView defaultmsg;
    Vector<CabItemFormat> cabItemFormatVector = new Vector<>();
    Vector<CabItemFormat> cabItemFormats = new Vector<>();
    CabPoolRVAdapter adapter;
    ValueEventListener newListener;
    String name, number, email;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);
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
        poolrv = (RecyclerView) findViewById(R.id.ridesrv);
        defaultmsg = (TextView) findViewById(R.id.rides_errorMessage1);
        mAuth = FirebaseAuth.getInstance();
        ref.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                name = userItemFormat.getUsername();
                email = userItemFormat.getEmail();
                number = userItemFormat.getMobileNumber();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        adapter = new CabPoolRVAdapter(this, cabItemFormats);
        poolrv.setHasFixedSize(true);
        poolrv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        poolrv.setAdapter(adapter);
        query.keepSynced(true);
        newListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                defaultmsg.setVisibility(View.INVISIBLE);
                poolrv.setVisibility(View.VISIBLE);
                cabItemFormatVector.clear();
                cabItemFormats.clear();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    final CabItemFormat cabItemFormat = shot.getValue(CabItemFormat.class);
                    pool.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                cabItemFormats.add(cabItemFormat);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                if (cabItemFormats.isEmpty()) {
                    defaultmsg.setVisibility(View.VISIBLE);
                    poolrv.setVisibility(View.INVISIBLE);

                } else {
                    poolrv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        email = mAuth.getCurrentUser().getEmail();

    }

    @Override
    protected void onResume() {
        super.onResume();

        query.addValueEventListener(newListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        query.removeEventListener(newListener);
    }
}
