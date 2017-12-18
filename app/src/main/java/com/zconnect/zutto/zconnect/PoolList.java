package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class PoolList extends AppCompatActivity {
    RecyclerView poolrv;
    DatabaseReference pool = FirebaseDatabase.getInstance().getReference().child("Cab");
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Phonebook");
    Query query = pool.orderByChild("time");
    TextView defaultmsg;
    String source,destination,date,time;
    Vector<CabItemFormat> cabItemFormatVector = new Vector<>();
    Vector<CabItemFormat> cabItemFormats = new Vector<>();
    CabPoolRVAdapter adapter;
    ValueEventListener newListener;
    String name, number, email;
    ProgressBar progressBar;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cab");
    private FirebaseAuth mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PoolList.this, AddCabPool.class);
                startActivity(intent);
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
        mUser = FirebaseAuth.getInstance();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {

                    PhonebookDisplayItem phonebookDisplayItem = shot.getValue(PhonebookDisplayItem.class);
                    if (phonebookDisplayItem == null)
                        return;
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
        try {
            source = getIntent().getStringExtra("source");
            destination = getIntent().getStringExtra("destination");
            date = getIntent().getStringExtra("date");
            time = getIntent().getStringExtra("time");
        } catch (Exception e) {

        }

        poolrv = (RecyclerView) findViewById(R.id.poolrv);
        defaultmsg = (TextView) findViewById(R.id.pool_errorMessage1);
        progressBar = (ProgressBar) findViewById(R.id.content_pool_progress);
        defaultmsg.setVisibility(View.INVISIBLE);
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
                for(i=0;i<cabItemFormatVector.size();i++){
                    if (source != null && destination != null && date != null && cabItemFormatVector != null) {
                        if (cabItemFormatVector.get(i).getSource() != null && cabItemFormatVector.get(i).getDestination() != null
                                && cabItemFormatVector.get(i).getDate() != null) {
                            if (cabItemFormatVector.get(i).getSource().equals(source) &&
                                    cabItemFormatVector.get(i).getDestination().equals(destination) &&
                                    cabItemFormatVector.get(i).getDate().equals(date)) {
                                cabItemFormats.add(cabItemFormatVector.get(i));
                            }
                        }
                    }
                }
                if (cabItemFormats.isEmpty()) {
                    defaultmsg.setVisibility(View.VISIBLE);
                    poolrv.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(PoolList.this);
                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("No CabPools found . \n Would you like to add one ?")
                            .setTitle("Alert");

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (isNetworkAvailable(getApplicationContext())) {
                                if (name != null && number != null) {
                                    CabListItemFormat cabListItemFormat = new CabListItemFormat();
                                    cabListItemFormat.setName(name);
                                    cabListItemFormat.setPhonenumber(number);
                                    ArrayList<CabListItemFormat> cabListItemFormats = new ArrayList<CabListItemFormat>();
                                    cabListItemFormats.add(cabListItemFormat);
                                    DatabaseReference newPost = databaseReference.push();
                                    String key = newPost.getKey();
                                    newPost.child("key").setValue(key);
                                    newPost.child("source").setValue(String.valueOf(source));
                                    newPost.child("destination").setValue(String.valueOf(destination));
                                    newPost.child("time").setValue(String.valueOf(time));
                                    newPost.child("date").setValue(date);
                                    newPost.child("cabListItemFormats").setValue(cabListItemFormats);
                                    Toast.makeText(getApplicationContext(), "CabPool Added", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                Snackbar snack = Snackbar.make(fab, "Please add your contact to Infone before adding a pool.", Snackbar.LENGTH_LONG);
                                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                snackBarText.setTextColor(Color.WHITE);
                                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                                snack.show();
                            } else {
                                Snackbar snack = Snackbar.make(fab, "No internet. Please try again later.", Snackbar.LENGTH_LONG);
                                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                snackBarText.setTextColor(Color.WHITE);
                                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                                snack.show();
                            }
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    android.app.AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
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
        if (mUser.getCurrentUser() != null)
            email = mUser.getCurrentUser().getEmail();


    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
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
