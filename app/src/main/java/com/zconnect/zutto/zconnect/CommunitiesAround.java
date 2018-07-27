package com.zconnect.zutto.zconnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.CommunitiesAroundAdapter;
import com.zconnect.zutto.zconnect.addActivities.CreateCommunity;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.CommunitiesItemFormat;

import java.util.Vector;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class CommunitiesAround extends BaseActivity {

    private FusedLocationProviderClient userLocationClient;
    CommunitiesAroundAdapter adapter;
    Vector<CommunitiesItemFormat> communitiesList = new Vector<>();
    RecyclerView communitiesRecycler;
    DatabaseReference communitiesReference;
    LocationManager locationManager;
    LocationListener locationListener;
    private ProgressDialog progressDialog;
    private TextView noCommunitiesTextView;

    private double lon,lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities_around);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

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

        communitiesReference = FirebaseDatabase.getInstance().getReference().child("communitiesInfo");
        communitiesRecycler = (RecyclerView) findViewById(R.id.all_communities);
        communitiesRecycler.setLayoutManager(new LinearLayoutManager(this));
        noCommunitiesTextView = (TextView) findViewById(R.id.no_community);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_activity_communities_around);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(CommunitiesAround.this, CreateCommunity.class);
                startActivity(i);
            }
        });

        adapter = new CommunitiesAroundAdapter(this, communitiesList);
        communitiesRecycler.setAdapter(adapter);
        communitiesReference.keepSynced(true);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lon = location.getLongitude();
                lat = location.getLatitude();

                loadCommunities(lon,lat);
                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(CommunitiesAround.this, "GPS Enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                if(provider.equals(LocationManager.GPS_PROVIDER)) {
                    buildAlertMessageNoGps(CommunitiesAround.this);
                }
            }
        };

        requestPermission();

    }

    public void requestPermission() {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 10);
            return;
        } else {
            progressDialog.setMessage("Searching Communities");
            progressDialog.show();

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    requestPermission();
                } else {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    progressDialog.setMessage("Searching Communities");
                    progressDialog.show();

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                }
                break;
       }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void loadCommunities(final double lon,final double lat){

        communitiesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                communitiesList.clear();
                Boolean flagNoCommunity = true;

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    CommunitiesItemFormat communitiesItemFormat = shot.getValue(CommunitiesItemFormat.class);
                   try {
                       double comLat, comLon,totalDistance;


                       comLat = shot.child("location").child("lat").getValue(Double.class);
                       comLon = shot.child("location").child("lon").getValue(Double.class);

                       totalDistance = distance(lat,comLat,lon,comLon);
                       if(totalDistance<2){
                           //Toast.makeText(CommunitiesAround.this, " " + totalDistance, Toast.LENGTH_SHORT).show();
                           communitiesList.add(communitiesItemFormat);
                           flagNoCommunity = false;
                       }

                   }catch (Exception e){

                   }
                }

                if(flagNoCommunity){
                    noCommunitiesTextView.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private  void buildAlertMessageNoGps(final Context ctx)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, you need to enable it!")
                .setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(CommunitiesAround.this, "You need to enable GPS to load communities", Toast.LENGTH_SHORT).show();

                    }
                });

        final AlertDialog alert = builder.create();

        if(!((Activity)this).isFinishing())
        {
            if(!alert.isShowing()) {
                alert.show();
                //show dialog
            }
        }

    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // in Kilometers



        return distance;
    }

}
