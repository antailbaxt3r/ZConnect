package com.zconnect.zutto.zconnect;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class CabPooling extends Fragment {
    Button done;
    CustomSpinner source, destination, time;
    TextView calender;
    int year, month, day;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate((R.layout.activity_cab_pooling), container, false);
        source = (CustomSpinner) v.findViewById(R.id.spinner_source);
        destination = (CustomSpinner) v.findViewById(R.id.spinner_destination);
        time = (CustomSpinner) v.findViewById(R.id.spinner_time);
        done = (Button) v.findViewById(R.id.done);

        calender = (TextView) v.findViewById(R.id.calender);
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
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
                if (calender.getText() != null && source.getSelectedItem() != null && destination.getSelectedItem() != null && time.getSelectedItem() != null) {
                    if (String.valueOf(destination.getSelectedItem()).equals(String.valueOf(source.getSelectedItem()))) {
                        Snackbar snack = Snackbar.make(done, "Invalid fields", Snackbar.LENGTH_LONG);
                        TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                        snackBarText.setTextColor(Color.WHITE);
                        snack.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal800));
                        snack.show();
                    } else {
                        Intent intent = new Intent(getContext(), PoolList.class);
                        intent.putExtra("date", calender.getText().toString());
                        intent.putExtra("source", String.valueOf(source.getSelectedItem()));
                        intent.putExtra("destination", String.valueOf(destination.getSelectedItem()));
                        intent.putExtra("time", String.valueOf(time.getSelectedItem()));

                        CounterManager.searchPool(String.valueOf(destination.getSelectedItem()));
                        startActivity(intent);
                    }
                } else {
                    Snackbar snack = Snackbar.make(done, "Fields are empty", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.teal800));
                    snack.show();
                }
            }
        });

        Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Regular.ttf");
        Typeface customFont2 = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-Light.ttf");
        done.setTypeface(customFont2);
        calender.setTypeface(customFont2);

        TextView from = (TextView) v.findViewById(R.id.from);
        TextView destination = (TextView) v.findViewById(R.id.destination);
        TextView date = (TextView) v.findViewById(R.id.date);
        TextView timeslot = (TextView) v.findViewById(R.id.timeslot);
        TextView search_for_rides = (TextView) v.findViewById(R.id.search_for_rides);
        from.setTypeface(customFont);
        destination.setTypeface(customFont);
        date.setTypeface(customFont);
        timeslot.setTypeface(customFont);
        search_for_rides.setTypeface(customFont);
        return v;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the Menu; this adds items to the action bar if it is present.
        SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
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
            startActivity(new Intent(getContext(), MyRides.class));
        }

        return super.onOptionsItemSelected(item);
    }

}
