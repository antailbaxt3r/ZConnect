package com.zconnect.zutto.zconnect;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Campus_Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private DatabaseReference mDatabaseMapLocation;
    private String centerLatS, centerLongS;
    private double centerLat, centerLong, nameLat, nameLong, rightTopLat, rightTopLong,
            leftBottomLat, leftBottomLong;

    public static void setTranslucentStatusBar(Window window) {
        if (window == null) return;
        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.LOLLIPOP) {
            setTranslucentStatusBarLollipop(window);
        } else if (sdkInt >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatusBarKiKat(window);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void setTranslucentStatusBarLollipop(Window window) {
        window.setStatusBarColor(
                window.getContext()
                        .getResources()
                        .getColor(android.R.color.transparent));
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void setTranslucentStatusBarKiKat(Window window) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus__map);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        getLatLong(map);

        /*
        LatLng leftBottomBound = new LatLng(15.385178, 73.868207);
        LatLng rightTopBound = new LatLng(15.394957, 73.885857);
        LatLng centre = new LatLng(15.389557, 73.876974);
        // Add a marker in Sydney and move the camera
        LatLng bits = new LatLng(15.3911, 73.8782);
        map.moveCamera(CameraUpdateFactory.newLatLng(bits));
        LatLngBounds bounds = new LatLngBounds(leftBottomBound, rightTopBound);

        map.setLatLngBoundsForCameraTarget(bounds);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centre, 16f));
        LatLng marker;
        marker = new LatLng(15.39244, 73.87893);
        map.addMarker(new MarkerOptions().position(marker).title("Post Box"));
        marker = new LatLng(15.39241, 73.87885);
        map.addMarker(new MarkerOptions().position(marker).title("Cobbler"));
        marker = new LatLng(15.3924, 73.8789);
        map.addMarker(new MarkerOptions().position(marker).title("Tailor"));
        marker = new LatLng(15.38811, 73.87663);
        map.addMarker(new MarkerOptions().position(marker).title("Visitor's Guest House"));
        marker = new LatLng(15.3869, 73.8709);
        map.addMarker(new MarkerOptions().position(marker).title("Nursery"));
        marker = new LatLng(15.3917, 73.87605);
        map.addMarker(new MarkerOptions().position(marker).title("Medical Centre"));
        marker = new LatLng(15.39208, 73.8756);
        map.addMarker(new MarkerOptions().position(marker).title("Student Activity Centre"));
        marker = new LatLng(15.39199, 73.87626);
        map.addMarker(new MarkerOptions().position(marker).title("SBI Branch"));
        marker = new LatLng(15.39202, 73.87619);
        map.addMarker(new MarkerOptions().position(marker).title("SBI ATM"));
        marker = new LatLng(15.39218, 73.87621);
        map.addMarker(new MarkerOptions().position(marker).title("Red Chillies"));
        marker = new LatLng(15.392070, 73.876426);
        map.addMarker(new MarkerOptions().position(marker).title("Shopping Complex"));
        marker = new LatLng(15.38701, 73.87329);
        map.addMarker(new MarkerOptions().position(marker).title("Children's Park"));
        marker = new LatLng(15.38743, 73.87574);
        map.addMarker(new MarkerOptions().position(marker).title("Main gate"));
        marker = new LatLng(15.39024, 73.87511);
        map.addMarker(new MarkerOptions().position(marker).title("Children's Park"));
        marker = new LatLng(15.38899, 73.87638);
        map.addMarker(new MarkerOptions().position(marker).title("Crossroads"));
        marker = new LatLng(15.3921, 73.87963);
        map.addMarker(new MarkerOptions().position(marker).title("Flag Lawns"));
        marker = new LatLng(15.39362, 73.87911);
        map.addMarker(new MarkerOptions().position(marker).title("Workshop"));
        marker = new LatLng(15.39304, 73.88051);
        map.addMarker(new MarkerOptions().position(marker).title("Auditorium"));
        marker = new LatLng(15.391248, 73.880643);
        map.addMarker(new MarkerOptions().position(marker).title("FoodKing"));
        marker = new LatLng(15.39295, 73.87635);
        map.addMarker(new MarkerOptions().position(marker).title("Cricket Ground"));
        marker = new LatLng(15.39266, 73.87725);
        map.addMarker(new MarkerOptions().position(marker).title("Volleyball Courts"));
        marker = new LatLng(15.39336, 73.87732);
        map.addMarker(new MarkerOptions().position(marker).title("Football Field"));
        marker = new LatLng(15.39408, 73.8768);
        map.addMarker(new MarkerOptions().position(marker).title("Tennis Courts"));
        marker = new LatLng(15.39293, 73.87557);
        map.addMarker(new MarkerOptions().position(marker).title("Cricket Nets"));
        marker = new LatLng(15.39258, 73.87533);
        map.addMarker(new MarkerOptions().position(marker).title("Basketball Courts"));
        marker = new LatLng(15.39209, 73.87531);
        map.addMarker(new MarkerOptions().position(marker).title("Badminton Court"));
        marker = new LatLng(15.39233, 73.87557);
        map.addMarker(new MarkerOptions().position(marker).title("Squash Court"));
        marker = new LatLng(15.392558, 73.878976);
        map.addMarker(new MarkerOptions().position(marker).title("Ice n Spice"));
        marker = new LatLng(15.391435, 73.879045);
        map.addMarker(new MarkerOptions().position(marker).title("Monginis"));
        marker = new LatLng(15.39191, 73.8764);
        map.addMarker(new MarkerOptions().position(marker).title("Borkars Super Store"));
        marker = new LatLng(15.39223, 73.87637);
        map.addMarker(new MarkerOptions().position(marker).title("Laxmi Laundry"));
        marker = new LatLng(15.39221, 73.8764);
        map.addMarker(new MarkerOptions().position(marker).title("Beauty Parlour"));
        marker = new LatLng(15.39216, 73.87647);
        map.addMarker(new MarkerOptions().position(marker).title("Power Laundry"));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(centre)
                .zoom(16f)
                .tilt(50)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
*/

    }

    void getLatLong(final GoogleMap map) {

        mDatabaseMapLocation = FirebaseDatabase.getInstance().getReference().child("Maps");

        mDatabaseMapLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                DataSnapshot child = snapshot.child("Center");


                String lat = child.child("centerLat").getValue(String.class);
                String log = child.child("centerLong").getValue(String.class);
                String latName = child.child("nameLat").getValue(String.class);
                String logName = child.child("nameLong").getValue(String.class);
                String latLB = child.child("leftBottomBoundLat").getValue(String.class);
                String logLB = child.child("leftBottomBoundLong").getValue(String.class);
                String latRT = child.child("rightTopBoundLat").getValue(String.class);
                String logRT = child.child("rightTopBoundLong").getValue(String.class);
                String placeName = child.child("name").getValue(String.class);

                if (log!= null && lat!= null && latName!= null&& latLB!= null&& latRT!= null &&
                        logLB!=null && logRT!=null && logName!=null) {

                    centerLong=Double.parseDouble(log);
                    centerLat=Double.parseDouble(lat);

                    nameLat=Double.parseDouble(latName);
                    nameLong=Double.parseDouble(logName);
                    rightTopLat=Double.parseDouble(latRT);
                    rightTopLong=Double.parseDouble(logRT);
                    leftBottomLat=Double.parseDouble(latLB);
                    leftBottomLong=Double.parseDouble(logLB);
                }

                LatLng leftBottomBound = new LatLng(leftBottomLat, leftBottomLong);
                LatLng rightTopBound = new LatLng(rightTopLat, rightTopLong);
                LatLng centre = new LatLng(centerLat, centerLong);
                // Add a marker in Sydney and move the camera
                LatLng bits = new LatLng(nameLat, nameLong);
                map.moveCamera(CameraUpdateFactory.newLatLng(bits));
                LatLngBounds bounds = new LatLngBounds(leftBottomBound, rightTopBound);

                map.setLatLngBoundsForCameraTarget(bounds);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(centre, 16f));

                LatLng marker;
                marker = new LatLng(nameLat, nameLong);
                map.addMarker(new MarkerOptions().position(marker).title(placeName));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(centre)
                        .zoom(16f)
                        .tilt(50)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.e("Campus_Map", " Maps center etc database fetch Cancelled");
                Toast.makeText(Campus_Map.this, "Need Internet Connection for Maps", Toast.LENGTH_SHORT).show();

            }
        });

    }
}
