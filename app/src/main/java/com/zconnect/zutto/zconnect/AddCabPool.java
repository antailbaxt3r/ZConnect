package com.zconnect.zutto.zconnect;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zconnect.zutto.zconnect.ItemFormats.CabItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.CabListItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static java.lang.Integer.valueOf;

public class AddCabPool extends BaseActivity {
    Button done;
    CustomSpinner source, destination, time_from, time_to;
    TextView date, calender;
    String email, name, number,goingTime,returnTime;
    String s_year,s_monthOfYear,s_dayOfMonth;
    double T1,T2;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Phonebook");
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Cab");
    private FirebaseUser mUser;

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
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        source = (CustomSpinner) findViewById(R.id.spinner_source);
        destination = (CustomSpinner) findViewById(R.id.spinner_destination);
        time_from = (CustomSpinner) findViewById(R.id.spinner_time_from);
        time_to = (CustomSpinner) findViewById(R.id.spinner_time_to);

        done = (Button) findViewById(R.id.done);
        calender = (TextView) findViewById(R.id.calender);


        try {

            String source = getIntent().getStringExtra("source");
            Log.e("TAG",source);

            String[] location=getResources().getStringArray(R.array.location);
            String[] time=getResources().getStringArray(R.array.time);
            for(int i=0;i<location.length;i++){
                if(location[i].equals(source)){
                    Log.e("TAG",location[i]);

                    this.source.setSelection(i);
                    Log.e("TAG","hua");
                    break;
                }
            }

            String destination = getIntent().getStringExtra("destination");
            for(int i=0;i<location.length;i++){
                if(location[i].equals(destination)){
                    this.destination.setSelection(i);
                    break;
                }
            }

            String date = getIntent().getStringExtra("date");
            if(!date.equals("null")){
                this.calender.setText(date);
            }

            String time_to = getIntent().getStringExtra("time_to");
            for(int i=0;i<time.length;i++){
                if(time[i].equals(time_to)){
                    this.time_to.setSelection(i);
                    break;
                }
            }

            String time_from = getIntent().getStringExtra("time_from");
            for(int i=0;i<time.length;i++){
                if(time[i].equals(time_from)){
                    this.time_from.setSelection(i);
                    break;
                }
            }

        } catch (Exception e) {

        }

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
                                DecimalFormat formatter=new DecimalFormat("00");
                                String month =formatter.format(monthOfYear+1);
                                String day =formatter.format(dayOfMonth);

                                //setting date to textview
                                calender.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);

                                //setting selected date to global int
                                s_year=String.valueOf (year);
                                s_dayOfMonth= String.valueOf(day);
                                s_monthOfYear= month;
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

                            goingTime=String.valueOf(time_from.getSelectedItem());
                            returnTime=String.valueOf(time_to.getSelectedItem());
                            T1= Integer.valueOf(goingTime.substring(0,2));
                            T2= Integer.valueOf(returnTime.substring(0,2));

                        if(source.getSelectedItem()==destination.getSelectedItem()){
                            Snackbar snack = Snackbar.make(done,"Source and destination can't be same" , Snackbar.LENGTH_LONG);
                            TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                            snackBarText.setTextColor(Color.WHITE);
                            snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                            snack.show();

                        }else{
                                if(T1==T2) {
                                    Snackbar snack = Snackbar.make(done,"Please select a valid interval" , Snackbar.LENGTH_LONG);
                                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                    snackBarText.setTextColor(Color.WHITE);
                                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                                    snack.show();



                                }else{

                                    if (T1-T2<0) {


                                        String time = getTimeOld();

                                        CabListItemFormat cabListItemFormat = new CabListItemFormat();
                                        cabListItemFormat.setName(name);
                                        cabListItemFormat.setPhonenumber(number);
                                        ArrayList<CabListItemFormat> cabListItemFormats = new ArrayList<CabListItemFormat>();
                                        cabListItemFormats.add(cabListItemFormat);


                                            //writing new added pool to database
                                        DatabaseReference newPost = databaseReference.push();
                                        String key = newPost.getKey();
                                        newPost.child("key").setValue(key);
                                        newPost.child("source").setValue(String.valueOf(source.getSelectedItem()));
                                        newPost.child("destination").setValue(String.valueOf(destination.getSelectedItem()));
                                        newPost.child("time").setValue(time);
                                        newPost.child("date").setValue(calender.getText().toString());
                                        newPost.child("DT").setValue(s_year + s_monthOfYear + s_dayOfMonth + " " + getTime());
                                        newPost.child("from").setValue(T1);
                                        newPost.child("to").setValue(T2);
                                        newPost.child("cabListItemFormats").setValue(cabListItemFormats);

                                        CounterManager.createPool(String.valueOf(destination.getSelectedItem()));
                                        FirebaseMessaging.getInstance().subscribeToTopic(key);
                                        FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid()).child("Topics").push().setValue(key);

                                          //writing to database for recent items
                                        DatabaseReference newPost2 = FirebaseDatabase.getInstance().getReference().child("home").push();

                                        newPost2.child("name").setValue("Cabpool to "+ destination.getSelectedItem().toString());
                                        newPost2.child("desc").setValue("Hey! a friend is asking for a cabpool from "+source.getSelectedItem().toString()+" to "+destination.getSelectedItem().toString()+" on "+calender.getText().toString()+" between "+time+". Do you want to join?");
                                        newPost2.child("imageurl").setValue("https://blog.grabon.in/wp-content/uploads/2016/09/Cab-Services.jpg");
                                        newPost2.child("feature").setValue("CabPool");
                                        newPost2.child("id").setValue(key);
                                        newPost2.child("Key").setValue(key);
                                        newPost2.child("desc2").setValue("");
                                        newPost2.child("DT").setValue(s_year + s_monthOfYear + s_dayOfMonth + " " + getTime());

                                        Snackbar snack = Snackbar.make(done, "Added", Snackbar.LENGTH_LONG);
                                        TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                        snackBarText.setTextColor(Color.WHITE);
                                        snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                                        snack.show();

                       
                                    } else {
                                        Snackbar snack = Snackbar.make(done, "Add pool for a single day", Snackbar.LENGTH_LONG);
                                        TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                        snackBarText.setTextColor(Color.WHITE);
                                        snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                                        snack.show();


                                        }

                                }
                        }

                                    } else {
                                        Snackbar snack = Snackbar.make(done, "Please add your contact to Infone before adding a pool", Snackbar.LENGTH_LONG);
                                        TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                        snackBarText.setTextColor(Color.WHITE);
                                        snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                                        snack.show();
                                    }
                                } else {
                                    Snackbar snack = Snackbar.make(done, "No Internet. Try later", Snackbar.LENGTH_LONG);
                                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                    snackBarText.setTextColor(Color.WHITE);
                                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                                    snack.show();
                                }

                } else {
                    Snackbar snack = Snackbar.make(done, "Fields are empty", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                    snack.show();
                }


            }
        });


    }
    DecimalFormat decimalFormat=new DecimalFormat("00");

    public String getTimeOld(){

        double Av=(T1+T2)/2;

        return (decimalFormat.format((int)Av))+":00 to "+(decimalFormat.format((int)Av+1)+":00");
    }

    public String getTime(){

        double Av=(T1+T2)/2;
        Log.e("ABC",String.valueOf(Av));
        Log.e("ABC",String.valueOf((int)Av));
        if(Av==(int)Av){
            Log.e("ABC","545454545");

            String str=decimalFormat.format((int)Av-00)+":00";
            return str;
        }else{

            String str=(decimalFormat.format((int)Av-00)+":30");
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
