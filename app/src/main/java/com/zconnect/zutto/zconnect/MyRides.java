package com.zconnect.zutto.zconnect;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.zconnect.zutto.zconnect.ItemFormats.CabListItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.util.ArrayList;
import java.util.Vector;

public class MyRides extends AppCompatActivity {
    RecyclerView poolrv;
    DatabaseReference pool = FirebaseDatabase.getInstance().getReference().child("Cab");
    Query query = pool.orderByChild("time");
    TextView defaultmsg;
    Vector<CabItemFormat> cabItemFormatVector = new Vector<>();
    Vector<CabItemFormat> cabItemFormats = new Vector<>();
    CabPoolRVAdapter adapter;
    ValueEventListener newListener;
    String name, number, email;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Phonebook");
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
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    PhonebookDisplayItem phonebookDisplayItem = shot.getValue(PhonebookDisplayItem.class);
                    if (phonebookDisplayItem == null || phonebookDisplayItem.getEmail() == null || phonebookDisplayItem.getNumber() == null) {
                        shot.getRef().removeValue();
                        continue;
                    }
                    if (email != null) {
                        if (phonebookDisplayItem.getEmail() != null) {
                            if (phonebookDisplayItem.getEmail().equals(email)) {
                                name = phonebookDisplayItem.getName();
                                number = phonebookDisplayItem.getNumber();
                            }
                        }
                    }


                }

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
                    CabItemFormat cabItemFormat = shot.getValue(CabItemFormat.class);

                    cabItemFormatVector.add(cabItemFormat);
                }
                int i;
                for (i = 0; i < cabItemFormatVector.size(); i++) {
                    CabItemFormat cabItemFormat = cabItemFormatVector.get(i);
                    ArrayList<CabListItemFormat> cabListItemFormats = cabItemFormat.getCabListItemFormats();
                    if(cabListItemFormats==null)
                        cabListItemFormats = new ArrayList<>();
                    int j;
                    for (j = 0; j < cabListItemFormats.size(); j++) {
                        if (name != null && number != null) {
                            if (cabListItemFormats.get(j) != null) {
                            if (name.equals(cabListItemFormats.get(j).getName()) && number.equals(cabListItemFormats.get(j).getPhonenumber())) {
                                cabItemFormats.add(cabItemFormat);
                            }
                            }
                        }
                    }
                }
                if (cabItemFormats.isEmpty()) {
                    defaultmsg.setVisibility(View.VISIBLE);
                    poolrv.setVisibility(View.INVISIBLE);

                } else {
                    poolrv.setAdapter(adapter);
                    //   Toast.makeText(getApplicationContext(), String.valueOf(cabItemFormats.size()), Toast.LENGTH_SHORT).show();
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
