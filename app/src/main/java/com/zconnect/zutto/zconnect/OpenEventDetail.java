package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.Event;

public class OpenEventDetail extends AppCompatActivity {

    DatabaseReference mDatabase;
    ImageView EventImage;
    TextView EventDescription;
    TextView EventVenue;
    ImageButton venueDirections;
    TextView EventDate;
    Event event;
    Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_event_detail);
        Bundle extras = getIntent().getExtras();
        event = (com.zconnect.zutto.zconnect.ItemFormats.Event) extras.get("currentEvent");
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
            getWindow().setStatusBarColor(colorPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }


//      Event = (TextView) findViewById(R.id.event);
        EventImage = (ImageView) findViewById(R.id.EventImage);
        EventDescription = (TextView) findViewById(R.id.description);
        EventVenue = (TextView) findViewById(R.id.od_venue);
        venueDirections = (ImageButton) findViewById(R.id.directions);
        if (event.getLon() == 0.0) {
            venueDirections.setVisibility(View.GONE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int colorPrimary = ContextCompat.getColor(this, R.color.colorPrimary);
            getWindow().setStatusBarColor(colorPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }


//      Event = (TextView) findViewById(R.id.event);
        EventImage = (ImageView) findViewById(R.id.EventImage);
        EventDescription = (TextView) findViewById(R.id.description);
        EventVenue = (TextView) findViewById(R.id.od_venue);
        venueDirections = (ImageButton) findViewById(R.id.directions);

        venueDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + event.getLat() + "," + event.getLon()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Venue directions not available", Toast.LENGTH_LONG).show();
                }
            }
        });
        EventDate = (TextView) findViewById(R.id.date);
        EventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog mProg = new ProgressDialog(OpenEventDetail.this);
                mProg.setMessage("Loading....");
                mProg.show();
                final Intent i = new Intent(OpenEventDetail.this, viewImage.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                final ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(OpenEventDetail.this, EventImage, getResources().getString(R.string.transition_string));
                i.putExtra("currentEvent", event.getEventName());
                i.putExtra("eventImage", event.getEventImage());
                mProg.dismiss();
                startActivity(i, optionsCompat.toBundle());
            }
        });


        String eventDate[] = (event.getEventDate().split("\\s+"));
        String date = "";
        int i = 0;
        while (i < 3) {
            date = date + " " + eventDate[i];
            i++;
        }

        EventDate.setText(date);
        EventDescription.setText(event.getEventDescription());
        EventVenue.setText(event.getVenue());
        getSupportActionBar().setTitle(event.getEventName());
        Picasso.with(this).load(event.getEventImage()).error(R.drawable.defaultevent).placeholder(R.drawable.defaultevent).into(EventImage);
        EventImage.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;


//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Events/Posts/"+value);
//
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
////                String eventName = (String) snapshot.child("EventName").getValue();
//                String imageUri = (String) snapshot.child("EventImage").getValue();
//                String eventDescription = (String) snapshot.child("EventDescription").getValue();
//                String eventDate[] = ((String) snapshot.child("EventDate").getValue()).split("\\s+");
//                String date = "";
//                int i = 0;
//                while (i < 3) {
//                    date = date + " " + eventDate[i];
//                    i++;
//                }
//
//                Picasso.with(OpenEventDetail.this).load(imageUri).into(EventImage);
////                Event.setText(eventName);
//                EventDate.setText(date);
//                EventDescription.setText(eventDescription);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//            }
//        });

    }
}
