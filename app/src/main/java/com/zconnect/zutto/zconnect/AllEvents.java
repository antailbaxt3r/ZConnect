package com.zconnect.zutto.zconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.Event;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AllEvents extends AppCompatActivity {

    boolean flag = false;
    LinearLayoutManager mlinearmanager;
    private RecyclerView mEventList;
    private DatabaseReference mDatabase;
    private DatabaseReference mPrivileges;
    private DatabaseReference mRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_events);
        JodaTimeAndroid.init(this);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Event/Posts");
        mPrivileges = FirebaseDatabase.getInstance().getReference().child("Event/Privileges/");
        mRequest = FirebaseDatabase.getInstance().getReference().child("Event/");
        mDatabase.keepSynced(true);
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
            getWindow().setStatusBarColor(colorPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        mlinearmanager = new LinearLayoutManager(this);
        mlinearmanager.setReverseLayout(true);
        mlinearmanager.setStackFromEnd(true);
        //mlinearmanager.setStackFromEnd(true);
        mlinearmanager.scrollToPosition(1);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //get current user
        final String emailId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mEventList = (RecyclerView) findViewById(R.id.eventList);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(mlinearmanager);



        mPrivileges.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {

                    if (child.getValue().equals(emailId)) {
                        flag = true;
                    }

                } //prints "Do you have data? You'll love Firebase."
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag) {
                    Intent intent = new Intent(AllEvents.this, AddEvent.class);
                    startActivity(intent);
                    finish();
                } else {


                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(AllEvents.this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage(R.string.dialog_message)
                            .setTitle(R.string.dialog_title);

                    // Add the buttons
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(R.string.request, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog


                            //checks if user is online
                            if (!isOnline()) {
                                Snackbar snack = Snackbar.make(fab, "Request not Sent. Check Internet Connection", Snackbar.LENGTH_LONG);
                                TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                                snackBarText.setTextColor(Color.WHITE);
                                snack.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.teal800));
                                snack.show();
                            } else {

                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto", "zconnectinc@gmail.com", null));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Request Permission to add Events");
                                startActivity(Intent.createChooser(emailIntent, "Send email..."));


                            }
                        }
                    });
                    // Set other dialog properties


                    // Create the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

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

        FirebaseRecyclerAdapter<Event, EventViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(
                Event.class,
                R.layout.events_row,
                EventViewHolder.class,
                mDatabase
        ) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {
                Date current_date = new LocalDate().toDate();
                if (current_date.getTime() > Long.parseLong(model.getFormatDate()) + 24 * 60 * 60) {
                    viewHolder.setEventName(model.getEventName());
                    viewHolder.setEventDesc(model.getEventDescription());
                    viewHolder.setEventImage(getApplicationContext(), model.getEventName(), model.getEventImage());
                    viewHolder.setEventDate(model.getEventDate());
                    viewHolder.openEvent(model);
                    viewHolder.setEventReminder(model.getEventDescription(), model.getEventName(), model.getFormatDate());
                } else

                    mDatabase.child(model.getKey()).removeValue();
            }

        };
        mEventList.setAdapter(firebaseRecyclerAdapter);


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
