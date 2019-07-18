package com.zconnect.zutto.zconnect;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.itemFormats.CabPoolLocationFormat;
import com.zconnect.zutto.zconnect.adapters.CabPoolLocationRVAdapter;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.RequestTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.util.HashMap;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.R.drawable.ic_arrow_back_black_24dp;

public class CabPoolLocations extends BaseActivity {
    private DatabaseReference databaseReferenceCabPool;
    private DatabaseReference databaseReferenceCabPool2;
    private DatabaseReference mPostedByDetails;
    private RecyclerView locationRecyclerView;
    private LinearLayoutManager locationLinearLayout;
    private Vector<CabPoolLocationFormat> locationsVector = new Vector<CabPoolLocationFormat>();
    private ValueEventListener mListener;
    private CabPoolLocationRVAdapter cabPoolLocationRVAdapter;
    private ProgressBar progressBar;
    private int flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cab_pool_locations);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(ic_arrow_back_black_24dp);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));


        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));
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
        databaseReferenceCabPool2 = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("admin").child("requests");
        mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Location");

        // Set up the input

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("userType")){
                    if (dataSnapshot.child("userType").getValue().toString().equals(UsersTypeUtilities.KEY_ADMIN)){
                        flag = 1;
                    }else if (dataSnapshot.child("userType").getValue().toString().equals(UsersTypeUtilities.KEY_VERIFIED)){
                        flag = 0;
                    }
                }else{
                    fab.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag==1)
                { CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta= new HashMap<>();
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_ADD_LOCATION_OPEN);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);

                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();
                    final EditText input = new EditText(view.getContext());

                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            addLocation(input.getText().toString());
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta= new HashMap<>();
                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_ADDED_LOCATION);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);
                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();


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
                else if (flag==0){
                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta= new HashMap<>();
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_REQUEST_LOCATION_OPEN);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);

                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();
                    final EditText input = new EditText(view.getContext());

                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    builder.setTitle("Request Location");
                    builder.setPositiveButton("Request", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestLocation(input.getText().toString());
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta= new HashMap<>();
                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_ADDEDREQUEST_LOCATION);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);
                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();}
            }
        });

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationsVector.clear();
                for (DataSnapshot shot: dataSnapshot.getChildren()) {
                    CabPoolLocationFormat cabPoolLocationFormat = new CabPoolLocationFormat();
                    try {
                        cabPoolLocationFormat = shot.getValue(CabPoolLocationFormat.class);
                        cabPoolLocationFormat.setLocationUID(shot.getKey());
                        locationsVector.add(cabPoolLocationFormat);
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

        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("userType")){
                    UserItemFormat currentUser = dataSnapshot.getValue(UserItemFormat.class);
                    cabPoolLocationRVAdapter = new CabPoolLocationRVAdapter(getApplicationContext(),locationsVector,currentUser.getUserType());
                    locationRecyclerView.setAdapter(cabPoolLocationRVAdapter);
                }else {
                    cabPoolLocationRVAdapter = new CabPoolLocationRVAdapter(getApplicationContext(),locationsVector,UsersTypeUtilities.KEY_VERIFIED);
                    locationRecyclerView.setAdapter(cabPoolLocationRVAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    public void requestLocation(String location){
        final DatabaseReference newPush=databaseReferenceCabPool2.push();
        final HashMap<String, Object> requestMap = new HashMap<>();
        requestMap.put("Type", RequestTypeUtilities.TYPE_CABPOOL_LOCATION);
        requestMap.put("key", newPush.getKey());
        requestMap.put("Name", location);
        requestMap.put("PostTimeMillis", System.currentTimeMillis());
        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> postedBy = new HashMap<>();
                postedBy.put("Username", dataSnapshot.child("username").getValue().toString());
                //needs to be changed after image thumbnail is put
                postedBy.put("ImageThumb", dataSnapshot.child("imageURLThumbnail").getValue().toString());
                postedBy.put("UID", dataSnapshot.child("userUID").getValue().toString());

                requestMap.put("PostedBy", postedBy);
                newPush.setValue(requestMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
