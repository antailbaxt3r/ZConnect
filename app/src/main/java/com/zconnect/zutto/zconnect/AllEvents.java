package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.Event;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public class AllEvents extends AppCompatActivity {

    LinearLayoutManager mlinearmanager;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mUserStats, mFeaturesStats;
    String TotalEvents;
    private RecyclerView mEventList;
    private DatabaseReference mDatabase;
    private DatabaseReference mRequest;
    private Query queryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);
        JodaTimeAndroid.init(this);
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

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        mUserStats = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Stats");
        mFeaturesStats = FirebaseDatabase.getInstance().getReference().child("Stats");
        mFeaturesStats.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TotalEvents = dataSnapshot.child("TotalEvents").getValue().toString();
                DatabaseReference newPost = mUserStats;
                Map<String, Object> taskMap = new HashMap<String, Object>();
                taskMap.put("TotalEvents", TotalEvents);
                newPost.updateChildren(taskMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mlinearmanager = new LinearLayoutManager(this);

        //mlinearmanager.setStackFromEnd(true);
        mlinearmanager.scrollToPosition(1);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //get current user
        final String emailId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mEventList = (RecyclerView) findViewById(R.id.eventList);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(mlinearmanager);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Event/VerifiedPosts");
        queryRef = mDatabase.orderByChild("FormatDate");
        mRequest = FirebaseDatabase.getInstance().getReference().child("Event/");

        mDatabase.keepSynced(true);
        queryRef.keepSynced(true);


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(AllEvents.this, AddEvent.class);
                    startActivity(intent);
                    finish();

            }
        });
    }

    private void writeNewPost(String email) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        Post post = new Post(email);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();


        childUpdates.put("/Requests", postValues);

        mRequest.updateChildren(childUpdates);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onStart() {
        super.onStart();

        String flag = null;
        flag = getIntent().getStringExtra("snackbar");
        if (flag != null) {
            if (flag.equals("true")) {
                Snackbar snack = Snackbar.make(mEventList, "Event sent for verification !!", Snackbar.LENGTH_LONG);
                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                snackBarText.setTextColor(Color.WHITE);
                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                snack.show();
            }
        }

        FirebaseRecyclerAdapter<Event, EventViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(
                Event.class,
                R.layout.events_row,
                EventViewHolder.class,
                queryRef
        ) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model,
                                              int position) {
                    viewHolder.openEvent(model);
                    viewHolder.setEventName(model.getEventName());
                    viewHolder.setEventDesc(model.getEventDescription());
                    viewHolder.setEventImage(getApplicationContext(), model.getEventImage());
                    viewHolder.setEventDate(model.getEventDate());
                    viewHolder.setEventReminder(model.getEventDescription(), model.getEventName(), model.getFormatDate());
                    viewHolder.setEventVenue(model.getVenue());
                
//                else {
//                    mDatabase.child(model.getKey()).removeValue(new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                            onStart();
//
//                        }
//                    });
//                }
            }
        };
        mEventList.setAdapter(firebaseRecyclerAdapter);


    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {


        View mView;

        public EventViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void openEvent(final Event event) {
            mView.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(mView.getContext(), OpenEventDetail.class);
                    i.putExtra("currentEvent", event);
                    i.putExtra("Eventtag","1");
                    mView.getContext().startActivity(i);
                }
            });
        }


        public void setEventName(String eventName) {

            TextView post_name = (TextView) mView.findViewById(R.id.er_event);
            post_name.setText(eventName);

        }

        public void setEventDesc(String eventDesc) {

            String shortEventDesc;

            TextView post_desc = (TextView) mView.findViewById(R.id.er_description);
            if (eventDesc.length() < 70) {
                shortEventDesc = eventDesc;
            } else {
                shortEventDesc = eventDesc.substring(0, 70);
                shortEventDesc = shortEventDesc + " ... read more";
            }

            post_desc.setText(shortEventDesc);

        }

        public void setEventVenue(String venue) {
            TextView post_venue = (TextView) mView.findViewById(R.id.er_venue);
            post_venue.setText(venue);

        }

        public void setEventImage(Context ctx, String image) {

            ImageView post_image = (ImageView) mView.findViewById(R.id.er_postImg);
            Picasso.with(ctx).load(image).into(post_image);
        }


        public void setEventDate(String eventDate) {
            TextView post_date = (TextView) mView.findViewById(R.id.er_date);
            String date[] = eventDate.split("\\s+");
            String finalDate = "";

            for (int i = 0; i < 4; i++) {
                finalDate = finalDate + " " + date[i];
            }

            post_date.setText(finalDate);
        }

        public void setEventReminder(final String eventDescription, final String eventName, final String time) {
            Button Reminder = (Button) mView.findViewById(R.id.er_reminder);
            Reminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    addReminderInCalendar(eventName, eventDescription, Long.parseLong(String.valueOf(time)), mView.getContext());

                }

            });

        }

        private void addReminderInCalendar(String title, String desc, long time, Context context) {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra("beginTime", time);
            intent.putExtra("allDay", false);
            intent.putExtra("rrule", "FREQ=DAILY");
            intent.putExtra("endTime", time + 60 * 60 * 1000);
            intent.putExtra("title", title);
            intent.putExtra("description", desc);
            context.startActivity(intent);

            // Display event id.
            //Toast.makeText(getApplicationContext(), "Event added :: ID :: " + event.getLastPathSegment(), Toast.LENGTH_SHORT).show();

            /** Adding reminder for event added. *
             }
             /** Returns Calendar Base URI, supports both new and old OS. */


        }
    }

    @IgnoreExtraProperties
    public class Post {

        public String email;
        public Map<String, Boolean> stars = new HashMap<>();

        public Post() {
            // Default constructor required for calls to DataSnapshot.getValue(Post.class)
        }

        public Post(String email) {

            this.email = email;
        }

        @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("email", email);
            result.put("stars", stars);
            return result;
        }

    }

}
