package com.zconnect.zutto.zconnect;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.CabItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.CabListItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.util.ArrayList;
import java.util.Calendar;

public class AddCabPool extends AppCompatActivity {
    Button done, calender;
    CustomSpinner source, destination, time;
    TextView date;
    CabItemFormat cabItemFormat;
    String email, name, number;
    int year, month, day;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cab");
    private FirebaseAuth mAuth;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Phonebook");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cab_pool);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {

                    PhonebookDisplayItem phonebookDisplayItem = shot.getValue(PhonebookDisplayItem.class);
                    if (email != null) {
                        if (phonebookDisplayItem.getEmail().equals(email)) {
                            name = phonebookDisplayItem.getName();
                            number = phonebookDisplayItem.getNumber();

                        }
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mAuth = FirebaseAuth.getInstance();
        source = (CustomSpinner) findViewById(R.id.spinner_source);
        destination = (CustomSpinner) findViewById(R.id.spinner_destination);
        time = (CustomSpinner) findViewById(R.id.spinner_time);
        done = (Button) findViewById(R.id.done);
        date = (TextView) findViewById(R.id.date);
        calender = (Button) findViewById(R.id.calender);
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddCabPool.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, year, month, day);
                datePickerDialog.show();

            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date.getText() != null && source.getSelectedItem() != null && destination.getSelectedItem() != null && time.getSelectedItem() != null) {
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
                            newPost.child("source").setValue(String.valueOf(source.getSelectedItem()));
                            newPost.child("destination").setValue(String.valueOf(destination.getSelectedItem()));
                            newPost.child("time").setValue(String.valueOf(time.getSelectedItem()));
                            newPost.child("date").setValue(date.getText().toString());
                            newPost.child("cabListItemFormats").setValue(cabListItemFormats);

                            startActivity(new Intent(AddCabPool.this, CabPooling.class));
                        } else {
                            Snackbar snack = Snackbar.make(done, "Please add your contact to Infone before adding a pool.", Snackbar.LENGTH_LONG);
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

    @Override
    public void onStart() {
        super.onStart();
        email = mAuth.getCurrentUser().getEmail();


    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
