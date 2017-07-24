package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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

import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AllEvents extends BaseActivity {

    LinearLayoutManager mlinearmanager;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mUserStats, mFeaturesStats;
    String TotalEvents;
    private RecyclerView mEventList;
    private DatabaseReference mDatabase;
    private DatabaseReference mRequest;
    private Query queryRef;

    static String monthSwitcher(String mon) {

        if (mon.equalsIgnoreCase("Jan")) {
            return "01";
        } else if (mon.equalsIgnoreCase("Feb")) {
            return "02";
        } else if (mon.equalsIgnoreCase("Mar")) {
            return "03";
        } else if (mon.equalsIgnoreCase("Apr")) {
            return "04";
        } else if (mon.equalsIgnoreCase("May")) {
            return "05";
        } else if (mon.equalsIgnoreCase("Jun")) {
            return "06";
        } else if (mon.equalsIgnoreCase("Jul")) {
            return "07";
        } else if (mon.equalsIgnoreCase("Aug")) {
            return "08";
        } else if (mon.equalsIgnoreCase("Sept")) {
            return "09";
        } else if (mon.equalsIgnoreCase("Oct")) {
            return "10";
        } else if (mon.equalsIgnoreCase("Nov")) {
            return "11";
        } else if (mon.equalsIgnoreCase("Dec")) {
            return "12";
        } else
            return "00";

    }

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
        SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status) {
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
        }

        mlinearmanager = new LinearLayoutManager(this);

        //mlinearmanager.setStackFromEnd(true);
        mlinearmanager.scrollToPosition(1);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


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
                SharedPreferences sharedPref = getSharedPreferences("guestMode", MODE_PRIVATE);
                Boolean status = sharedPref.getBoolean("mode", false);

                if (!status) {

                    Intent intent = new Intent(AllEvents.this, AddEvent.class);
                    startActivity(intent);
                    finish();
                }else {

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AllEvents.this);

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage("Please Log In to access this feature.")
                            .setTitle("Dear Guest!");

                    builder.setPositiveButton("Log In", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(AllEvents.this, logIn.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.setNegativeButton("Lite :P", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    android.app.AlertDialog dialog = builder.create();
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
                    viewHolder.setEventReminder(model.getEventDescription(), model.getEventName(), model.getEventDate());
                    viewHolder.setEventVenue(model.getVenue());
                viewHolder.setShareOptions(model.getEventImage());

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

                    addReminderInCalendar(eventName, eventDescription, time, mView.getContext());

                }

            });

        }

        public void setShareOptions(final String image) {

            final Button share = (Button) mView.findViewById(R.id.share_button);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    shareEvent(image, mView.getContext());

                    /*Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                    shareIntent.setType("image*//*");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello World");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(image)); //add image path
                    mView.getContext().startActivity(Intent.createChooser(shareIntent, "Share image using"));
*/
                }
            });
        }

        private void shareEvent(final String image, final Context context) {

            try {
                //shareIntent.setPackage("com.whatsapp");
                //Add text and then Image URI
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            //Your code goes here
                            Uri imageUri = Uri.parse(image);
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);

                            Bitmap bm = BitmapFactory.decodeStream(new URL(image)
                                    .openConnection()
                                    .getInputStream());


                            bm = mergeBitmap(BitmapFactory.decodeResource(context.getResources(),
                                    R.drawable.background_icon_z), bm, context);

                            shareIntent.putExtra(Intent.EXTRA_TEXT, "An important event @Zconnect ...");
                            shareIntent.setType("text/plain");

                            String path = MediaStore.Images.Media.insertImage(
                                    context.getContentResolver(),
                                    bm, "", null);
                            Uri screenshotUri = Uri.parse(path);

                            shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                            shareIntent.setType("image/*");
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            context.startActivity(shareIntent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();

            } catch (android.content.ActivityNotFoundException ex) {
                //ToastHelper.MakeShortText("Whatsapp have not been installed.");
            }

        }

        private void addReminderInCalendar(String title, String desc, String time, Context context) {

            String arr[]=time.split(" ");

            String month=monthSwitcher(arr[1]);
            String date=arr[2];
            String year=arr[5];
            String times=arr[3];

            String timesA[]=times.split(":");
            String hour=timesA[0];
            String mins=timesA[1];


            Calendar beginTime = Calendar.getInstance();
            beginTime.set(Integer.parseInt(year),Integer.parseInt(month)-1, Integer.parseInt(date),
                    Integer.parseInt(hour), Integer.parseInt(mins));
            Calendar endTime = Calendar.getInstance();
            endTime.set(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(date),
                    Integer.parseInt(hour)+1, Integer.parseInt(mins));


            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData(Events.CONTENT_URI);
            //intent.setType("vnd.android.cursor.item/event");
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
            intent.putExtra(Events.ALL_DAY, false);
            //intent.putExtra(Events.RRULE, "FREQ=DAILY");
            //intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
            intent.putExtra(Events.TITLE, title);
            intent.putExtra(Events.DESCRIPTION, desc);
            intent.putExtra(Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
            context.startActivity(intent);

            // Display event id.
            //Toast.makeText(getApplicationContext(), "Event added :: ID :: " + event.getLastPathSegment(), Toast.LENGTH_SHORT).show();

            /** Adding reminder for event added. *
             }
             /** Returns Calendar Base URI, supports both new and old OS. */

        }

        public Bitmap mergeBitmap(Bitmap bitmap2, Bitmap bitmap1, Context context) {
            Bitmap mergedBitmap = null;


            Drawable[] layers = new Drawable[2];

            layers[0] = new BitmapDrawable(context.getResources(), bitmap1);
            layers[1] = new BitmapDrawable(context.getResources(), bitmap2);

            LayerDrawable layerDrawable = new LayerDrawable(layers);

            int width = layerDrawable.getIntrinsicWidth();
            int height = layerDrawable.getIntrinsicHeight();

            mergedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mergedBitmap);
            layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            layerDrawable.draw(canvas);


            //mergedBitmap=BitmapFactory.decodeResourceStream(layerDrawable)

            return mergedBitmap;
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
