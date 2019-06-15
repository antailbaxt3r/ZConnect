package com.zconnect.zutto.zconnect.addActivities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zconnect.zutto.zconnect.CabPoolAll;
import com.zconnect.zutto.zconnect.OpenEventDetail;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.CustomSpinner;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.commonModules.NumberNotificationForFeatures;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.FeatureDBName;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import static com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities.KEY_CABPOOL;

import static java.lang.Integer.valueOf;

public class AddCabPool extends BaseActivity {
    Button done;
    CustomSpinner source, destination, time_from, time_to;
    TextView date, calender;
    String email, name, number, goingTime, returnTime, imageThumb, userUID;
    String s_year, s_monthOfYear, s_dayOfMonth;
    double T1, T2;
    DatabaseReference mFeaturesStats;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
    DatabaseReference mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child("allCabs");
    private DatabaseReference homeReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");
    private DatabaseReference databaseReferenceCabPool;
    private FirebaseUser mUser;
    private long postTimeMillis;
    private ArrayList<String> locations= new ArrayList<String>();
    private ArrayAdapter<String> locationsSpinnerAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cab_pool);
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
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }


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
                setDefaults();
                locationsSpinnerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        locationsSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locations); //selected item will look like a spinner set from XML
        locationsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        source = (CustomSpinner) findViewById(R.id.spinner_source);
        destination = (CustomSpinner) findViewById(R.id.spinner_destination);
        time_from = (CustomSpinner) findViewById(R.id.spinner_time_from);
        time_to = (CustomSpinner) findViewById(R.id.spinner_time_to);

        done = (Button) findViewById(R.id.done);
//        Typeface customFont = Typeface.createFromAsset(AddCabPool.this.getAssets(), "fonts/Raleway-SemiBold.ttf");
//        done.setTypeface(customFont);
        calender = (TextView) findViewById(R.id.calender);


        source.setAdapter(locationsSpinnerAdapter);
        destination.setAdapter(locationsSpinnerAdapter);



        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);
                name = user.getUsername();
                number =user.getMobileNumber();
                imageThumb = user.getImageURLThumbnail();
                userUID = user.getUserUID();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser == null)
            finish();
        final Calendar c = Calendar.getInstance();
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Present dates
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(AddCabPool.this,

                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //formatting day and month to double digits
                                DecimalFormat formatter = new DecimalFormat("00");
                                String month = formatter.format(monthOfYear + 1);
                                String day = formatter.format(dayOfMonth);

                                //setting date to textview
                                calender.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                                //setting selected date to global int
                                s_year = String.valueOf(year);
                                s_dayOfMonth = String.valueOf(day);
                                s_monthOfYear = month;
                            }
                        }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                datePickerDialog.show();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!calender.getText().equals("Click to choose") && !source.getSelectedItem().toString().equals("Anywhere") && !destination.getSelectedItem().toString().equals("Anywhere") && !time_from.getSelectedItem().toString().equals("Anytime") && !time_to.getSelectedItem().toString().equals("Anytime")) {
                    if (isNetworkAvailable(getApplicationContext())) {
                        if (name != null && number != null) {

                            goingTime = String.valueOf(time_from.getSelectedItem());
                            returnTime = String.valueOf(time_to.getSelectedItem());
                            T1 = Integer.valueOf(goingTime.substring(0, 2));
                            T2 = Integer.valueOf(returnTime.substring(0, 2));

                            if (source.getSelectedItem() == destination.getSelectedItem()) {
                                Snackbar snack = Snackbar.make(done, "Source and destination can't be same", Snackbar.LENGTH_LONG);
                                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                snackBarText.setTextColor(Color.WHITE);
                                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                                snack.show();

                            } else {
                                if (T1 == T2) {
                                    Snackbar snack = Snackbar.make(done, "Please select a valid interval", Snackbar.LENGTH_LONG);
                                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                    snackBarText.setTextColor(Color.WHITE);
                                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                                    snack.show();


                                } else {
                                    if (T1 - T2 < 0) {
                                        String time = getTimeOld();

                                        UsersListItemFormat usersListItemFormat = new UsersListItemFormat();
                                        usersListItemFormat.setName(name);
                                        usersListItemFormat.setPhonenumber(number);
                                        usersListItemFormat.setImageThumb(imageThumb);
                                        usersListItemFormat.setUserUID(userUID);
//                                        ArrayList<UsersListItemFormat> usersListItemFormats = new ArrayList<UsersListItemFormat>();
//                                        usersListItemFormats.add(usersListItemFormat);


                                        //writing new added pool to database
                                        DatabaseReference newPost = databaseReference.push();
                                        final DatabaseReference postedBy = newPost.child("PostedBy");
                                        postTimeMillis = System.currentTimeMillis();
                                        String key = newPost.getKey();
                                        newPost.child("key").setValue(key);
                                        newPost.child("source").setValue(String.valueOf(source.getSelectedItem()));
                                        newPost.child("destination").setValue(String.valueOf(destination.getSelectedItem()));
                                        newPost.child("time").setValue(time);
                                        newPost.child("date").setValue(calender.getText().toString());
                                        newPost.child("DT").setValue(s_year + s_monthOfYear + s_dayOfMonth + " " + getTime());
                                        newPost.child("from").setValue(T1);
                                        newPost.child("to").setValue(T2);
                                        newPost.child("usersListItemFormats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(usersListItemFormat);
                                        newPost.child("PostTimeMillis").setValue(postTimeMillis);
                                        postedBy.setValue(null);
                                        postedBy.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                postedBy.child("Username").setValue(dataSnapshot.child("username").getValue().toString());
                                                //needs to be changed after image thumbnail is put
                                                postedBy.child("ImageThumb").setValue(dataSnapshot.child("imageURLThumbnail").getValue().toString());
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });



                                        NumberNotificationForFeatures numberNotificationForFeatures = new NumberNotificationForFeatures(FeatureDBName.KEY_CABPOOL);
                                        numberNotificationForFeatures.setCount();
                                        Log.d("NumberNoti setting for ", FeatureDBName.KEY_CABPOOL);


                                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                                        HashMap<String, String> meta= new HashMap<>();

                                        meta.put("source",String.valueOf(source.getSelectedItem()));
                                        meta.put("destination",String.valueOf(destination.getSelectedItem()));

                                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                        counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_ADDED);
                                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                                        counterItemFormat.setMeta(meta);

                                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                        counterPush.pushValues();


                                        //writing to database for recent items
                                        DatabaseReference newPost2 = homeReference.child(key);
                                        final DatabaseReference newPost2PostedBy = newPost2.child("PostedBy");
                                        newPost2.child("name").setValue("Cabpool to " + destination.getSelectedItem().toString());
                                        newPost2.child("desc").setValue("Hey! a friend is asking for a cabpool from " + source.getSelectedItem().toString() + " to " + destination.getSelectedItem().toString() + " on " + calender.getText().toString() + " between " + time + ". Do you want to join?");
                                        newPost2.child("imageurl").setValue("https://blog.grabon.in/wp-content/uploads/2016/09/Cab-Services.jpg");
                                        newPost2.child("feature").setValue("CabPool");
                                        newPost2.child("id").setValue(key);
                                        newPost2.child("Key").setValue(key);
                                        newPost2.child("desc2").setValue("");
                                        newPost2.child("DT").setValue(s_year + s_monthOfYear + s_dayOfMonth + " " + getTime());
                                        newPost2.child("cabpoolSource").setValue(String.valueOf(source.getSelectedItem()));
                                        newPost2.child("cabpoolDestination").setValue(String.valueOf(destination.getSelectedItem()));
                                        newPost2.child("cabpoolDate").setValue(calender.getText().toString());
                                        newPost2.child("cabpoolTimeFrom").setValue(T1);
                                        newPost2.child("cabpoolTimeTo").setValue(T2);
                                        newPost2.child("cabpoolTime").setValue(time);
                                        newPost2.child("cabpoolNumPeople").setValue(1);
                                        newPost2.child("PostTimeMillis").setValue(postTimeMillis);
                                        newPost2PostedBy.setValue(null);
                                        newPost2PostedBy.child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                newPost2PostedBy.child("Username").setValue(dataSnapshot.child("username").getValue().toString());
                                                //needs to be changed after image thumbnail is put
                                                newPost2PostedBy.child("ImageThumb").setValue(dataSnapshot.child("imageURLThumbnail").getValue().toString());
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        //writing uid of cabpool to homePosts node in Users1.uid for handling data conistency
                                        mPostedByDetails.child("homePosts").child(key).setValue(true);

                                        // Adding stats
                                        mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("Stats");
                                        mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                Object o = dataSnapshot.child("TotalCabpools").getValue();
                                                if (o == null)
                                                    o = "0";
                                                Integer TotalCabpools = Integer.parseInt(o.toString());
                                                TotalCabpools = TotalCabpools + 1;
                                                DatabaseReference newPost = mFeaturesStats;
                                                Map<String, Object> taskMap = new HashMap<>();
                                                taskMap.put("TotalCabpools", TotalCabpools);
                                                newPost.updateChildren(taskMap);
                                            }


                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });


                                        Snackbar snack = Snackbar.make(done, "Added", Snackbar.LENGTH_LONG);
                                        TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                        snackBarText.setTextColor(Color.WHITE);
                                        snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                                        snack.show();
//
//                                        NotificationSender notificationSender=new NotificationSender(null,null,null,null,null,null,null,KEY_CABPOOL,true,false,getApplicationContext());
//                                        notificationSender.execute();
                                        FirebaseMessaging.getInstance().subscribeToTopic(key);
                                        NotificationSender notificationSender = new NotificationSender(AddCabPool.this,FirebaseAuth.getInstance().getCurrentUser().getUid());

                                        NotificationItemFormat cabAddNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_ADD,FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        cabAddNotification.setCommunityName(communityTitle);
                                        notificationSender.execute(cabAddNotification);

                                        GlobalFunctions.addPoints(10);

                                        Intent intent = new Intent(AddCabPool.this,CabPoolAll.class);

                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        //AddCabPool.this.startActivity(new Intent(AddCabPool.this, CabPoolAll.class));

                                        finish();

                                    } else {
                                        Snackbar snack = Snackbar.make(done, "Add pool for a single day", Snackbar.LENGTH_LONG);
                                        TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                        snackBarText.setTextColor(Color.WHITE);
                                        snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                                        snack.show();


                                    }

                                }
                            }

                        } else {
                            Snackbar snack = Snackbar.make(done, "Please add your contact to Infone before adding a pool", Snackbar.LENGTH_LONG);
                            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.WHITE);
                            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                            snack.show();
                        }
                    } else {
                        Snackbar snack = Snackbar.make(done, "No Internet. Try later", Snackbar.LENGTH_LONG);
                        TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        snackBarText.setTextColor(Color.WHITE);
                        snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                        snack.show();
                    }

                } else {
                    Snackbar snack = Snackbar.make(done, "Fields are empty", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                    snack.show();
                }


            }
        });


    }

    public void setDefaults(){
        try {

            String source = getIntent().getStringExtra("source");
            Log.e("TAG", source);

            String[] time = getResources().getStringArray(R.array.time);

            for (int i = 0; i < locations.size(); i++) {
                if (locations.get(i).equals(source)) {
                    this.source.setSelection(i);
                    Log.e("TAG", "hua");
                    break;
                }
            }

            String destination = getIntent().getStringExtra("destination");
            for (int i = 0; i < locations.size(); i++) {
                if (locations.get(i).equals(destination)) {
                    this.destination.setSelection(i);
                    break;
                }
            }

            String date = getIntent().getStringExtra("date");
            if(!date.equals("null")){
                SimpleDateFormat abc=new SimpleDateFormat("dd/M/yyyy");
                Date a=abc.parse(date);
                Log.e("msg",String.valueOf(a));
                s_dayOfMonth= (new SimpleDateFormat("dd")).format(a);
                s_monthOfYear= (new SimpleDateFormat("MM")).format(a);
                s_year= (new SimpleDateFormat("yyyy")).format(a);
                Log.e("msg",s_dayOfMonth);
                Log.e("msg",s_monthOfYear);
                Log.e("msg",s_year);
                this.calender.setText(date);
            }

            String time_to = getIntent().getStringExtra("time_to");
            for (int i = 0; i < time.length; i++) {
                if (time[i].equals(time_to)) {
                    this.time_to.setSelection(i);
                    break;
                }
            }

            String time_from = getIntent().getStringExtra("time_from");
            for (int i = 0; i < time.length; i++) {
                if (time[i].equals(time_from)) {
                    this.time_from.setSelection(i);
                    break;
                }
            }

            progressDialog.dismiss();
        } catch (Exception e) {

            progressDialog.dismiss();
            finish();
            Toast.makeText(this, "Network Problem", Toast.LENGTH_SHORT).show();
        }
    }

    DecimalFormat decimalFormat = new DecimalFormat("00");

    public String getTimeOld() {

        double Av = (T1 + T2) / 2;

        return (decimalFormat.format((int) Av)) + ":00 to " + (decimalFormat.format((int) Av + 1) + ":00");
    }

    public String getTime() {

        double Av=(T1+T2)/2;
        Log.e("ABC",String.valueOf(Av));
        Log.e("ABC",String.valueOf((int)Av));
        if(Av==(int)Av){

            String str = decimalFormat.format((int) Av - 00) + ":00";
            return str;
        } else {

            String str = (decimalFormat.format((int) Av - 00) + ":30");
            return str;
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        email = mUser.getEmail();


    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
