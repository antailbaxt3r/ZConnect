package com.zconnect.zutto.zconnect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class CommunitiesAround extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener  {
//implements GoogleApiClient.OnConnectionFailedListener
    private FusedLocationProviderClient userLocationClient;
    CommunitiesAroundAdapter adapter;
    Vector<CommunitiesItemFormat> communitiesList = new Vector<>();
    RecyclerView communitiesRecycler;
    DatabaseReference communitiesReference;
    LocationManager locationManager;
    LocationListener locationListener;
    private ProgressDialog progressDialog;
    private TextView noCommunitiesTextView;
    private Button turnOnGPS;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    private double lon,lat;

    private static final String TAG = CommunitiesAround.class.getSimpleName();

    private static final int REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE = 33;
    private static final int REQUEST_PERMISSIONS_LAST_LOCATION_REQUEST_CODE = 34;
    private static final int REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE = 35;

    private FusedLocationProviderClient mFusedLocationClient;

    protected static long MIN_UPDATE_INTERVAL = 10 * 1000; // 1  minute is the minimum Android recommends, but we use 30 seconds

    protected Location mLastLocation;

//    private TextView resultTextView;
    LocationRequest locationRequest;
    Location lastLocation = null;
    Location currentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communities_around);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);

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

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        communitiesReference = FirebaseDatabase.getInstance().getReference().child("communitiesInfo");
        communitiesRecycler = (RecyclerView) findViewById(R.id.all_communities);
        communitiesRecycler.setLayoutManager(new LinearLayoutManager(this));
        noCommunitiesTextView = (TextView) findViewById(R.id.no_community);
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
                startActivity(i);
            }
        });
        // TODO: Consider calling
        // ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        // public void onRequestPermissionsResult(int requestCode, String[] permissions,
        // int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.

        adapter = new CommunitiesAroundAdapter(this, communitiesList);
        communitiesRecycler.setAdapter(adapter);
        communitiesReference.keepSynced(true);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        checkForLocationRequest();
        checkForLocationSettings();



//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(Location location) {
//
//                lon = location.getLongitude();
//                lat = location.getLatitude();
//
//                Toast.makeText(CommunitiesAround.this, lon + " " + lat, Toast.LENGTH_SHORT).show();

//                loadCommunities(lon,lat);
//                locationManager.removeUpdates(this);
//            }

//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//                Toast.makeText(CommunitiesAround.this, "GPS Enabled", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//                if(provider.equals(LocationManager.GPS_PROVIDER)) {
//                    buildAlertMessageNoGps(CommunitiesAround.this);
//                }
//            }
//        };
//
//        requestPermission();

    }

//    public void requestPermission() {
//
//        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 10);
//            return;
//        } else {
//            progressDialog.setMessage("Searching Communities");
//            progressDialog.show();
//
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();

        callCurrentLocation();

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (result != ConnectionResult.SUCCESS && result != ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            Toast.makeText(this, "Are you running in Emulator ? try a real device.", Toast.LENGTH_SHORT).show();
        }
    }

//    public void callLastKnownLocation(View view) {
//        try {
//            if (
//                    ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                            ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    ) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                requestPermissions(REQUEST_PERMISSIONS_LAST_LOCATION_REQUEST_CODE);
//                return;
//            }
//
//            getLastLocation();
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public void callCurrentLocation() {
        try {
            if (
                    ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ) {
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                requestPermissions(REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE);
                return;
            }

            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    currentLocation = (Location) locationResult.getLastLocation();

//                    String result = "Current Location Latitude is " +
//                            currentLocation.getLatitude() + "\n" +
//                            "Current location Longitude is " + currentLocation.getLongitude();
                    loadCommunities(currentLocation.getLongitude(), currentLocation.getLatitude());
//                    resultTextView.setText(result);
                }
            }, Looper.myLooper());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {

        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLastLocation = task.getResult();

                            String result = "Last known Location Latitude is " +
                                    mLastLocation.getLatitude() + "\n" +
                                    "Last known longitude Longitude is " + mLastLocation.getLongitude();

                            loadCommunities(mLastLocation.getLongitude(),mLastLocation.getLatitude());
//                            resultTextView.setText(result);
                        } else {
                            showSnackbar("No Last known location found. Try current location..!");
                        }
                    }
                });
    }

    private void showSnackbar(final String text) {
        View container = findViewById(R.id.container);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showSnackbar(final String mainTextString, final String actionString,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                mainTextString,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(actionString, listener).show();
    }

    private void startLocationPermissionRequest(int requestCode) {
        ActivityCompat.requestPermissions(CommunitiesAround.this, new String[]{ACCESS_COARSE_LOCATION}, requestCode);
    }

    private void requestPermissions(final int requestCode) {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            showSnackbar("Permission is must to find the location", "Ok",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest(requestCode);
                        }
                    });

        } else {
            startLocationPermissionRequest(requestCode);
        }
    }

    public void checkForLocationRequest(){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(MIN_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
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
                            progressDialog.setMessage("Searching Communities");
                            progressDialog.show();

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

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_LAST_LOCATION_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            }
        }

        if (requestCode == REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callCurrentLocation();
            }
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

        final AlertDialog dialog = builder.create();

        if(!((Activity)this).isFinishing())
        {
            if(!dialog.isShowing()) {
                dialog.setCancelable(false);
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorHighlight));
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


//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }
}
