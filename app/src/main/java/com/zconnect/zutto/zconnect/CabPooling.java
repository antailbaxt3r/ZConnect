package com.zconnect.zutto.zconnect;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.CustomSpinner;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CabPooling extends BaseActivity {

    Button done;
    CustomSpinner source, destination, time_to, time_from;
    TextView calender;
    int year, month, day;
    Object Source,Destination,Time_From,Time_To;
    ArrayList<String> locations= new ArrayList<String>();
    ArrayAdapter<String> locationsSpinnerAdapter;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReferenceCabPool;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_pooling);


        setToolbar();
        setSupportActionBar(toolbar);
        toolbar.setTitle("Search For Rides");
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
        setActionBarTitle("Search Pools");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading details..");
        progressDialog.setCancelable(false);
        progressDialog.show();
        databaseReferenceCabPool = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("locations");

        databaseReferenceCabPool.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    try {
                        locations.add(shot.child("locationName").getValue().toString());
                    }catch (Exception e){

                    }
                }
                locationsSpinnerAdapter.notifyDataSetChanged();
                if(progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        locationsSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locations); //selected item will look like a spinner set from XML
        locationsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        source = (CustomSpinner) findViewById(R.id.spinner_source);
        destination = (CustomSpinner) findViewById(R.id.spinner_destination);
        time_to = (CustomSpinner) findViewById(R.id.spinner_time_to);
        time_from = (CustomSpinner) findViewById(R.id.spinner_time_from);
        done = (Button) findViewById(R.id.done);

        source.setAdapter(locationsSpinnerAdapter);
        destination.setAdapter(locationsSpinnerAdapter);

        calender = (TextView) findViewById(R.id.calender);
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CabPooling.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                calender.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });



        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if((String.valueOf(source.getSelectedItem()).equals("Anywhere"))){
                Source=null;
            }else{
                Source=source.getSelectedItem();
            }
            if((String.valueOf(destination.getSelectedItem()).equals("Anywhere"))){
                Destination=null;
            }else{
                Destination=destination.getSelectedItem();
            }
            if((String.valueOf(time_from.getSelectedItem()).equals("Anytime"))){
                Time_From=null;
            }else{
                Time_From=time_from.getSelectedItem();
            }
            if((String.valueOf(time_to.getSelectedItem()).equals("Anytime"))){
                Time_To=null;
            }else{
                Time_To=time_to.getSelectedItem();
            }

            Intent intent = new Intent(CabPooling.this, CapPoolSearchList.class);

            if (Source == null && Destination == null && Time_From == null && Time_To == null && calender.getText().equals("Click to choose")) {
                //check if all fields are not null
                Snackbar snack = Snackbar.make(done, "All fields can't be simultaneously empty", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(CabPooling.this, R.color.colorPrimaryDark));
                snack.show();

            } else {
                if (Source!=null &&Destination!=null&&Source == Destination) {
                    Snackbar snack = Snackbar.make(done, "Source and Destination can't be same", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(CabPooling.this, R.color.colorPrimaryDark));
                    snack.show();

                } else {

                    if (Source != null) {
                        intent.putExtra("source", String.valueOf(Source));
                    }

                    if (Destination != null) {
                        intent.putExtra("destination", String.valueOf(Destination));
                    }

                    if (!calender.getText().toString().equals("Click to choose")) {
                        intent.putExtra("date", calender.getText().toString());
                    }

                    if ((Time_From != null && Time_To == null) || (Time_From == null && Time_To != null)) {

                        //show snack time interval cant be this
                        Snackbar snack = Snackbar.make(done, "Mention proper time intervals", Snackbar.LENGTH_LONG);
                        TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        snackBarText.setTextColor(Color.WHITE);
                        snack.getView().setBackgroundColor(ContextCompat.getColor(CabPooling.this, R.color.colorPrimaryDark));
                        snack.show();
                    } else {

                        if (Time_To==Time_From && Time_To!=null){

                            //show snack time cant be equal
                            Snackbar snack = Snackbar.make(done, "Mention proper time intervals", Snackbar.LENGTH_LONG);
                            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.WHITE);
                            snack.getView().setBackgroundColor(ContextCompat.getColor(CabPooling.this, R.color.colorPrimaryDark));
                            snack.show();

                        }else{

                            if (Time_To != null) {
                                intent.putExtra("time_to", String.valueOf(Time_To));
                            }

                            if (Time_From != null) {
                                intent.putExtra("time_from", String.valueOf(Time_From));
                            }

                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta= new HashMap<>();

                            meta.put("type",Source.toString());


                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_SEARCH_RESULT);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);

                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();
                            startActivity(intent);
                            finish();

                        }
                    }
                }
            }
            }
        });


        Typeface customFont = Typeface.createFromAsset(CabPooling.this.getAssets(), "fonts/Raleway-Regular.ttf");
        Typeface customFont2 = Typeface.createFromAsset(CabPooling.this.getAssets(), "fonts/Raleway-SemiBold.ttf");

        done.setTypeface(customFont2);
        calender.setTypeface(customFont2);

        TextView from = (TextView) findViewById(R.id.from);
        TextView destination = (TextView) findViewById(R.id.destination);
        TextView date = (TextView) findViewById(R.id.date);
        TextView timeslot = (TextView) findViewById(R.id.timeslot);
        TextView timeslot2 = (TextView) findViewById(R.id.timeslot2);
        from.setTypeface(customFont);
        destination.setTypeface(customFont);
        date.setTypeface(customFont);
        timeslot.setTypeface(customFont);
        timeslot2.setTypeface(customFont);
        calender.setTypeface(customFont);

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the Menu; this adds items to the action bar if it is present.
        SharedPreferences sharedPref = CabPooling.this.getSharedPreferences("guestMode", Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);
        if (!status){
            inflater.inflate(R.menu.myrides, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.rides) {
            startActivity(new Intent(CabPooling.this, MyRides.class));
        }

        return super.onOptionsItemSelected(item);
    }

}
