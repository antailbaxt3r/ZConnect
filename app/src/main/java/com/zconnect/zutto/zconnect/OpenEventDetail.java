package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.Event;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class OpenEventDetail extends BaseActivity {

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
    Button boostBtn;
    Boolean flag = false;

    ProgressDialog progressDialog;

    Uri screenshotUri;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_event_detail);
       id=getIntent().getStringExtra("id");
        tag=getIntent().getStringExtra("Eventtag");

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(mActionBarToolbar);

        boostBtn = (Button)findViewById(R.id.boostBtn);

        progressDialog=new ProgressDialog(this);

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


        EventDescription.setMovementMethod(LinkMovementMethod.getInstance());



        EventDate = (TextView) findViewById(R.id.od_date);
        EventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CounterManager.eventOpenPic(event.getKey());
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

        if (tag.equals("1")) {
            Bundle extras = getIntent().getExtras();
            event = (com.zconnect.zutto.zconnect.ItemFormats.Event) extras.get("currentEvent");
                venueDirections.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CounterManager.eventgetDirection(event.getKey());
                        try {
                            if (event.getLat() == 0 && event.getLon() == 0) {
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(event.getVenue()));
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            } else {
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + event.getLat() + "," + event.getLon());
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");
                                startActivity(mapIntent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Venue directions not available", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            String eventDate[] = (event.getEventDate().split("\\s+"));
            String date = "";
            int i = 0;
            while (i < 3) {
                date = date + " " + eventDate[i];
                i++;
            }

            try {
                EventDate.setText(date);
                EventDescription.setText(event.getEventDescription());
                EventVenue.setText(event.getVenue());
                getSupportActionBar().setTitle(event.getEventName());
                Picasso.with(this).load(event.getEventImage()).error(R.drawable.defaultevent).placeholder(R.drawable.defaultevent).into(EventImage);
                EventImage.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;

                boostCounter();
                setEventReminder(event.getEventDescription(), event.getEventName(), event.getFormatDate());
            }catch (Exception e) {
                Log.d("Error Alert: ", e.getMessage());
            }
        }
        else {
            databaseReference= FirebaseDatabase.getInstance().getReference().child("Event").child("VerifiedPosts").child(id);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    event = dataSnapshot.getValue(Event.class);

                    boostCounter();
                    venueDirections.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                CounterManager.eventgetDirection(event.getKey());
                                if (event.getLat() == 0 && event.getLon() == 0) {
                                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(event.getVenue()));
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    startActivity(mapIntent);
                                } else {
                                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + event.getLat() + "," + event.getLon());
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    startActivity(mapIntent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Venue directions not available", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    String eventDate[] = (event.getEventDate().split("\\s+"));
                    String date = "";
                    int i = 0;
                    while (i < 3) {
                        date = date + " " + eventDate[i];
                        i++;
                    }

                    try {

                        EventDate.setText(date);
                        EventDescription.setText(event.getEventDescription());
                        EventVenue.setText(event.getVenue());
                        getSupportActionBar().setTitle(event.getEventName());
                        Picasso.with(getApplicationContext()).load(event.getEventImage()).error(R.drawable.defaultevent).placeholder(R.drawable.defaultevent).into(EventImage);
                        EventImage.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        setEventReminder(event.getEventDescription(), event.getEventName(), event.getFormatDate());
                        //  Log.v("Tag",event.getEventDate());

                    }catch (Exception e)
                    {
                        Log.d("Error Alert: ", e.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        //changing fonts
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
        Typeface customFont2 = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
        EventDate.setTypeface(customFont);
        EventDescription.setTypeface(customFont2);
        EventVenue.setTypeface(customFont);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(event==null){

        }

        if (user != null && event!=null) {
            String boost = event.getBoosters();
            if (boost != null) {
            if (boost.contains(user.getUid())) {
                boostBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curvedradiusbutton2_sr));
            }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share) {

            shareEvent(event.getEventImage(),this.getApplicationContext());

        }
        return super.onOptionsItemSelected(item);
    }

    private void shareEvent(final String image, final Context context) {

            try {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
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
                            String temp = "*Event:* " + event.getEventName()
                                    + "\n*Venue:* " + event.getVenue()
                                    + "\n*Date:* " + event.getEventDate()
                                    + "\n" + event.getEventDescription()
                                    + "\n\n" + "https://play.google.com/store/apps/details?id=com.zconnect.zutto.zconnect";

                            shareIntent.putExtra(Intent.EXTRA_TEXT, temp);
                            shareIntent.setType("text/plain");

                            path = MediaStore.Images.Media.insertImage(
                                    context.getContentResolver(),
                                    bm, "", null);
                            screenshotUri = Uri.parse(path);

                            shareIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                            shareIntent.setType("image/png");
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                            progressDialog.dismiss();
                            startActivityForResult(Intent.createChooser(shareIntent, "Share Via"), 0);
                            //context.startActivity(shareIntent);

                        } catch (Exception e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();

            } catch (android.content.ActivityNotFoundException ex) {
                progressDialog.dismiss();
                //ToastHelper.MakeShortText("Whatsapp have not been installed.");
            }

    }

    public Bitmap mergeBitmap(Bitmap bitmap2, Bitmap bitmap1, Context context) {
        Bitmap mergedBitmap = null;


        Drawable[] layers = new Drawable[2];

        layers[0] = new BitmapDrawable(context.getResources(), bitmap1);
        layers[1] = new BitmapDrawable(context.getResources(), bitmap2);

        LayerDrawable layerDrawable = new LayerDrawable(layers);

        int width = layers[0].getIntrinsicWidth();
        int height = layers[0].getIntrinsicHeight();

        mergedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mergedBitmap);
        layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        layerDrawable.draw(canvas);


        //mergedBitmap=BitmapFactory.decodeResourceStream(layerDrawable)

        return mergedBitmap;
    }



    public void setEventReminder(final String eventDescription, final String eventName, final String time) {
        Button Reminder = (Button) findViewById(R.id.setReminder);
        Reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CounterManager.eventReminderCounter(event.getKey());
                if (!TextUtils.isEmpty(time))
                    addReminderInCalendar(eventName, eventDescription, Long.parseLong(String.valueOf(time)));
                else
                    showToast("Time unavailable");

            }

        });
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
        Reminder.setTypeface(customFont);

    }

    private void addReminderInCalendar(String title, String desc, long time) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", time);
        intent.putExtra("allDay", false);
        //intent.putExtra("rrule", "FREQ=DAILY");
        intent.putExtra("endTime", time + 60 * 60 * 1000);
        intent.putExtra("title", title);
        intent.putExtra("contactDescTv", desc);
        startActivity(intent);

        // Display event id.
        //Toast.makeText(getApplicationContext(), "Event added :: ID :: " + event.getLastPathSegment(), Toast.LENGTH_SHORT).show();

        /** Adding reminder for event added. *
         }
         /** Returns Calendar Base URI, supports both new and old OS. */


    }


    private void boostCounter() {

        final DatabaseReference eventDatabase = FirebaseDatabase.getInstance().getReference().child("Event/VerifiedPosts").child(event.getKey());
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        eventDatabase.child("BoostersUids").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                eventDatabase.child("BoostCount").setValue(dataSnapshot.getChildrenCount());

                if (dataSnapshot.hasChild(user.getUid())) {
                    boostBtn.setText(dataSnapshot.getChildrenCount() + " Boosted");
                    boostBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curvedradiusbutton2_sr));
                    flag = true;
                } else {
                    boostBtn.setText(dataSnapshot.getChildrenCount() + " Boost");
                    boostBtn.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.curvedradiusbutton_sr));
                    flag = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if (user != null) {
            boostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!flag) {
                        Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put(user.getUid(), user.getUid());
                        CounterManager.eventBoost(event.getKey(), "Details");
                        eventDatabase.child("BoostersUids").updateChildren(taskMap);
                    } else {
                        eventDatabase.child("BoostersUids").child(user.getUid()).removeValue();
                    }
                }
            });

        } else {
            boostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getApplicationContext());
                    dialog.setNegativeButton("Lite", null)
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(loginIntent);
                                    finish();
                                }
                            })
                            .setTitle("Please login to boost.")
                            .create().show();
                }
            });
        }

        Typeface customfont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Raleway-Light.ttf");
        boostBtn.setTypeface(customfont);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            // delete temp file
            File file = new File(path);
            file.delete();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent eventsIntent=new Intent(OpenEventDetail.this,AllEvents.class);
//        startActivity(eventsIntent);
        finish();
    }
}
