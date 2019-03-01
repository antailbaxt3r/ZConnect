package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zconnect.zutto.zconnect.adapters.CommunitiesAroundAdapter;
import com.zconnect.zutto.zconnect.addActivities.CreateCommunity;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.CommunitiesItemFormat;

import java.util.Vector;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class CommunitiesAround extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    CommunitiesAroundAdapter adapter;
    Vector<CommunitiesItemFormat> communitiesList = new Vector<>();
    RecyclerView communitiesRecycler;
    DatabaseReference communitiesReference;
    LocationManager locationManager;
    LocationListener locationListener;
    private ProgressDialog progressDialog;
    private RelativeLayout noCommunitiesLayout;
    private Button turnOnGPS;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    private double lon, lat;

    private static final String TAG = CommunitiesAround.class.getSimpleName();


    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private static final int REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE = 33;
    private static final int REQUEST_PERMISSIONS_LAST_LOCATION_REQUEST_CODE = 34;
    private static final int REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE = 35;

    private FusedLocationProviderClient mFusedLocationClient;

    protected static long MIN_UPDATE_INTERVAL = 10 * 1000; // 1  minute is the minimum Android recommends, but we use 30 seconds

    protected Location mLastLocation;


    LocationRequest locationRequest;
    Location lastLocation = null;
    Location currentLocation = null;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities_around);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);

        progressDialog.setMessage("Searching Communities");
        progressDialog.show();

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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        communitiesReference = FirebaseDatabase.getInstance().getReference().child("communitiesInfo");
        communitiesRecycler = (RecyclerView) findViewById(R.id.all_communities);
        communitiesRecycler.setLayoutManager(new LinearLayoutManager(this));
        noCommunitiesLayout = (RelativeLayout) findViewById(R.id.no_communities_layout);

        noCommunitiesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CommunitiesAround.this, CreateCommunity.class);
                i.putExtra("lat",lat);
                i.putExtra("lon",lon);
                startActivity(i);
            }
        });

        turnOnGPS = (Button) findViewById(R.id.turn_on_gps);


        turnOnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_activity_communities_around);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(CommunitiesAround.this, CreateCommunity.class);
                i.putExtra("lat",lat);
                i.putExtra("lon",lon);
                startActivity(i);
            }
        });

        adapter = new CommunitiesAroundAdapter(this, communitiesList);
        communitiesRecycler.setAdapter(adapter);
        communitiesReference.keepSynced(true);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(7500); //use a value fo about 10 to 15s for a real app
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //Google services client for location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    loadCommunities(location.getLongitude(), location.getLatitude());
                }
            }
        };

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION:

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission was granted do nothing and carry on

                } else {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_SHORT).show();
                }

                break;

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkForLocationSettings();
        } else {

            mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        loadCommunities(location.getLongitude(), location.getLatitude());
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        startLocationUpdates();
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (result != ConnectionResult.SUCCESS && result != ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            Toast.makeText(this, "Are you running in Emulator ? try a real device.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
            }
        }

    }



    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationPermissionRequest(int requestCode) {
        ActivityCompat.requestPermissions(CommunitiesAround.this, new String[]{ACCESS_COARSE_LOCATION}, requestCode);
    }


    public void checkForLocationSettings() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.addLocationRequest(locationRequest);
            SettingsClient settingsClient = LocationServices.getSettingsClient(CommunitiesAround.this);

            settingsClient.checkLocationSettings(builder.build())
                    .addOnSuccessListener(CommunitiesAround.this, new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {


                            //Setting is success...
                            //Toast.makeText(CommunitiesAround.this, "Enabled the Location successfully. Now you can press the buttons..", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(CommunitiesAround.this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                                    try {
                                        // Show the dialog by calling startResolutionForResult(), and check the
                                        // result in onActivityResult().
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(CommunitiesAround.this, REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE);
                                    } catch (IntentSender.SendIntentException sie) {
                                        sie.printStackTrace();
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    Toast.makeText(CommunitiesAround.this, "Setting change is not available.Try in another device.", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadCommunities(final double lon,final double lat){

        this.lon = lon;
        this.lat = lat;

        communitiesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                communitiesList.clear();
                Boolean flagNoCommunity = true;

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    CommunitiesItemFormat communitiesItemFormat = shot.getValue(CommunitiesItemFormat.class);
                   try {
                       double comLat, comLon,totalDistance;
                       Integer radius;

                       radius = communitiesItemFormat.getRadius();

                       comLat = shot.child("location").child("lat").getValue(Double.class);
                       comLon = shot.child("location").child("lon").getValue(Double.class);

                       totalDistance = distance(lat,comLat,lon,comLon);
                       if(totalDistance<radius){

                           communitiesList.add(communitiesItemFormat);
                           flagNoCommunity = false;
                       }

                   }catch (Exception e){

                   }
                }

                if(flagNoCommunity){
                    noCommunitiesLayout.setVisibility(View.VISIBLE);
                }else{
                    noCommunitiesLayout.setVisibility(View.GONE);
                }
                    progressDialog.dismiss();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_communities_around, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_sign_out){
            if (!isNetworkAvailable(getApplicationContext())) {
                Snackbar snack = Snackbar.make(communitiesRecycler, "No Internet. Can't Log Out.", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                snack.show();
            } else {
                logoutAndSendToLogin();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void logoutAndSendToLogin(){

        mAuth = FirebaseAuth.getInstance();

        FirebaseMessaging.getInstance().unsubscribeFromTopic(mAuth.getCurrentUser().getUid());
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(communityReference);
        }catch (Exception e){}
        mAuth.signOut();
        SharedPreferences preferences = getSharedPreferences("communityName", 0);
        preferences.edit().remove("communityReference").commit();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        Intent loginIntent = new Intent(CommunitiesAround.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
