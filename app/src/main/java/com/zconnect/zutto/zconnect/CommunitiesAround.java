package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.CommunitiesItemFormat;

import java.util.Vector;

public class CommunitiesAround extends BaseActivity {

    CommunitiesAroundAdapter adapter;
    Vector <CommunitiesItemFormat> communitiesList =new Vector<>();
    RecyclerView communitiesRecycler;
    DatabaseReference communitiesReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities_around);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        communitiesReference = FirebaseDatabase.getInstance().getReference().child("communitiesInfo");
        communitiesRecycler= (RecyclerView) findViewById(R.id.all_communities);
        communitiesRecycler.setLayoutManager(new LinearLayoutManager(this));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(CommunitiesAround.this,CreateCommunity.class);
                startActivity(i);
            }
        });

        adapter = new CommunitiesAroundAdapter(this,communitiesList);
        communitiesRecycler.setAdapter(adapter);
        communitiesReference.keepSynced(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        communitiesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                communitiesList.clear();
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    communitiesList.add(shot.getValue(CommunitiesItemFormat.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
