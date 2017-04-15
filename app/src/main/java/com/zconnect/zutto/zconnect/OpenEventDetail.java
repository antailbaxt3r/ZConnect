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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    String tag;
    String id;
    Toolbar mActionBarToolbar;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_event_detail);
       id=getIntent().getStringExtra("id");
        tag=getIntent().getStringExtra("Eventtag");

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
            int colorDarkPrimary = ContextCompat.getColor(this, R.color.colorPrimaryDark);
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }


//      Event = (TextView) findViewById(R.id.event);
        EventImage = (ImageView) findViewById(R.id.od_EventImage);
        EventDescription = (TextView) findViewById(R.id.od_description);
        EventVenue = (TextView) findViewById(R.id.od_venue);
        venueDirections = (ImageButton) findViewById(R.id.od_directions);
//        if (event.getLon() == null) {
//            venueDirections.setVisibility(View.GONE);
//        }




        EventDate = (TextView) findViewById(R.id.od_date);
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
        if(tag!=null&&tag.equals("1")){
            Bundle extras = getIntent().getExtras();
            event = (com.zconnect.zutto.zconnect.ItemFormats.Event) extras.get("currentEvent");
            if (event.getLon() != null) {
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
            }
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


            setEventReminder(event.getEventDescription(), event.getEventName(), event.getFormatDate());


        }
        else {
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Event").child("VerifiedPosts").child(id);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    event = dataSnapshot.getValue(Event.class);
                    if (event.getLon() != null) {
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
                    }
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
                    Picasso.with(getApplicationContext()).load(event.getEventImage()).error(R.drawable.defaultevent).placeholder(R.drawable.defaultevent).into(EventImage);
                    EventImage.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;


                    setEventReminder(event.getEventDescription(), event.getEventName(), event.getFormatDate());

                    //  Log.v("Tag",event.getEventDate());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



    }

    public void setEventReminder(final String eventDescription, final String eventName, final String time) {
        ImageButton Reminder = (ImageButton) findViewById(R.id.setReminder);
        Reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addReminderInCalendar(eventName, eventDescription, Long.parseLong(String.valueOf(time)));

            }

        });

    }

    private void addReminderInCalendar(String title, String desc, long time) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", time);
        intent.putExtra("allDay", false);
        intent.putExtra("rrule", "FREQ=DAILY");
        intent.putExtra("endTime", time + 60 * 60 * 1000);
        intent.putExtra("title", title);
        intent.putExtra("description", desc);
        startActivity(intent);

        // Display event id.
        //Toast.makeText(getApplicationContext(), "Event added :: ID :: " + event.getLastPathSegment(), Toast.LENGTH_SHORT).show();

        /** Adding reminder for event added. *
         }
         /** Returns Calendar Base URI, supports both new and old OS. */


    }
}
