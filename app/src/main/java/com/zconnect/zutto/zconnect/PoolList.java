package com.zconnect.zutto.zconnect;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.CabItemFormat;

import java.util.Vector;

public class PoolList extends AppCompatActivity {
    RecyclerView poolrv;
    DatabaseReference pool=  FirebaseDatabase.getInstance().getReference().child("Cab");
    Query query = pool.orderByChild("time");
    TextView defaultmsg;
    String source,destination,date,time;
    Vector<CabItemFormat> cabItemFormatVector = new Vector<>();
    Vector<CabItemFormat>cabItemFormats = new Vector<>();
    CabPoolRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        try {
            source = getIntent().getStringExtra("source");
            destination = getIntent().getStringExtra("destination");
            date = getIntent().getStringExtra("date");
            time = getIntent().getStringExtra("time");
        }
        catch (Exception e){

        }

        poolrv = (RecyclerView) findViewById(R.id.poolrv);
        defaultmsg = (TextView) findViewById(R.id.pool_errorMessage1);


        adapter = new CabPoolRVAdapter(this, cabItemFormats);
        poolrv.setHasFixedSize(true);
        poolrv.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        poolrv.setAdapter(adapter);
        query.keepSynced(true);
    }
    @Override
    protected void onStart() {
        super.onStart();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                defaultmsg.setVisibility(View.INVISIBLE);
                poolrv.setVisibility(View.VISIBLE);
                cabItemFormatVector.clear();

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    CabItemFormat cabItemFormat = shot.getValue(CabItemFormat.class);

                     cabItemFormatVector.add(cabItemFormat);
                }


                // Need to add empty search result log message
                if (cabItemFormatVector.isEmpty()) {
                    defaultmsg.setVisibility(View.VISIBLE);
                    poolrv.setVisibility(View.INVISIBLE);

                } else {
                    int i;
                    for(i=0;i<cabItemFormatVector.size();i++){
                        if(cabItemFormatVector.get(i).getSource().equals(source)&&
                                cabItemFormatVector.get(i).getDestination().equals(destination) &&
                                cabItemFormatVector.get(i).getDate().equals(date)){
                            cabItemFormats.add(cabItemFormatVector.get(i));
                        }
                    }
                    poolrv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
               
            }
        });

    }

}
