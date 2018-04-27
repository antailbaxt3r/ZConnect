package com.zconnect.zutto.zconnect;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.CabPoolLocationFormat;

import java.util.Vector;

public class CabPoolLocations extends BaseActivity {
    private DatabaseReference databaseReferenceCabPool;
    private DatabaseReference mPostedByDetails;
    private RecyclerView locationRecyclerView;
    private LinearLayoutManager locationLinearLayout;
    private Vector<CabPoolLocationFormat> locationsVector = new Vector<CabPoolLocationFormat>();
    private ValueEventListener mListener;
    private CabPoolLocationRVAdapter cabPoolLocationRVAdapter;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cab_pool_locations);
        toolbar=(Toolbar) findViewById(R.id.toolbar);
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

        setActionBarTitle("Locations");


        locationRecyclerView = (RecyclerView) findViewById(R.id.location_recycler_view);
        locationLinearLayout = new LinearLayoutManager(getApplicationContext());
        progressBar = (ProgressBar) findViewById(R.id.cab_pool_locations_progress_circle);
        progressBar.setVisibility(View.VISIBLE);
        locationRecyclerView.setVisibility(View.INVISIBLE);


        locationRecyclerView.setLayoutManager(locationLinearLayout);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseReferenceCabPool = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("locations");
        mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Location");

        // Set up the input

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(view.getContext());

                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addLocation(input.getText().toString());

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                builder.show();
            }
        });

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationsVector.clear();
                for (DataSnapshot shot: dataSnapshot.getChildren()) {
                    try {
                        locationsVector.add(shot.getValue(CabPoolLocationFormat.class));
                    }catch (Exception e){}

                }
                cabPoolLocationRVAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                locationRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                locationRecyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(CabPoolLocations.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        };

        cabPoolLocationRVAdapter = new CabPoolLocationRVAdapter(this,locationsVector);
        locationRecyclerView.setAdapter(cabPoolLocationRVAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseReferenceCabPool.addValueEventListener(mListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReferenceCabPool.removeEventListener(mListener);
    }

    public void addLocation(String Location){
        final DatabaseReference newPush=databaseReferenceCabPool.push();

        newPush.child("locationName").setValue(Location);
        Long postTimeMillis = System.currentTimeMillis();
        newPush.child("PostTimeMillis").setValue(postTimeMillis);
        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newPush.child("PostedBy").child("Username").setValue(dataSnapshot.child("username").getValue().toString());
                //needs to be changed after image thumbnail is put
                newPush.child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child("imageURLThumbnail").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
