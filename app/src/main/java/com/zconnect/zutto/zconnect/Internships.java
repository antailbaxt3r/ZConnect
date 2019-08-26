package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.CabPoolRVAdapter;
import com.zconnect.zutto.zconnect.adapters.InternshipsRVAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.InternshipsItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;

import java.util.HashMap;
import java.util.Vector;

public class Internships extends BaseActivity {

    private RecyclerView recyclerView;
    private TextView noInternshipsText;
    private InternshipsRVAdapter internshipsRVAdapter;
    Vector<InternshipsItemFormat> internshipsList = new Vector<InternshipsItemFormat>();
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internships);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setToolbar();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

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

        recyclerView = (RecyclerView) findViewById(R.id.internships_main_rv);
        noInternshipsText = (TextView) findViewById(R.id.no_internships_available_message);
        internshipsRVAdapter = new InternshipsRVAdapter(Internships.this, internshipsList,communityReference);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        recyclerView.setAdapter(internshipsRVAdapter);
        recyclerView.setVisibility(View.INVISIBLE);

        databaseReference = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("internships").child("opportunities");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                internshipsList.clear();
                for (DataSnapshot shot: dataSnapshot.getChildren())
                {
                    InternshipsItemFormat internshipsItemFormat = shot.getValue(InternshipsItemFormat.class);
                    internshipsList.add(internshipsItemFormat);
                }
                if (internshipsList.size()==0)
                {
                    recyclerView.setVisibility(View.GONE);
                    noInternshipsText.setVisibility(View.VISIBLE);
                }
                else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noInternshipsText.setVisibility(View.GONE);
                    recyclerView.setAdapter(internshipsRVAdapter);
                    internshipsRVAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_internships, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(getApplicationContext(), MyInternships.class));

        CounterItemFormat counterItemFormat = new CounterItemFormat();
        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
        counterItemFormat.setUniqueID(CounterUtilities.KEY_INTERNSHIPS_MY_INTERNSHIPS_OPEN);
        counterItemFormat.setTimestamp(System.currentTimeMillis());
        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
        counterPush.pushValues();

        return super.onOptionsItemSelected(item);
    }
}
