package com.zconnect.zutto.zconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zconnect.zutto.zconnect.adapters.UsersListRVAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.itemFormats.CabItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.FeatureNamesUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class CabPoolListOfPeople extends BaseActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private DatabaseReference pool, chatRef, fullPool;
    private Button join;
    private String key,name, number, uid, imageThumb, userUID, date, source, destination, time, people, postedByImageText, postedByText;

    private Vector<UsersListItemFormat> usersListItemFormatVector = new Vector<>();
    private UsersListRVAdapter adapter;
    private Boolean flag, numberFlag;
    private String reference, reference_old = "archives", reference_default = "allCabs";
    private Button joinButton;
    private LinearLayout joinLayout,chatLayout, share;
    private TextView chatEditText;
    private String formatted_date, Date;
    private TextView dateTV, timeTV, sourceTV, destinationTV, peopleTV, postedByTV;
    private SimpleDraweeView postedByImageDV;
    private FirebaseAuth mAuth;
    private DatabaseReference ref;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String forumUID;
    private String reciverKey;
    private ValueEventListener listener;
    private DatabaseReference mDatabaseViews;
    private int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cab_list_of_people);
        setToolbar();
        setSupportActionBar(toolbar);


        toolbar.setTitle("Cabpool Details");

        share = findViewById(R.id.share_cab_cp_detail);
//        setTitle("List of people");
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
//        showBackButton();
//        setSupportActionBar(getToolbar());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {
            key = getIntent().getStringExtra("key");
        } catch (Exception e) {
            finish();
        }


        forumUID = getIntent().getStringExtra("forumUID");

        joinButton = (Button) findViewById(R.id.join);
        joinLayout = (LinearLayout) findViewById(R.id.joinLayout);
        chatLayout = (LinearLayout) findViewById(R.id.chatLayout);

        //for cabpool details
        timeTV = findViewById(R.id.time_cp_detail);
        dateTV = findViewById(R.id.date_cp_detail);
        sourceTV = findViewById(R.id.source_cp_detail);
        destinationTV = findViewById(R.id.destination_cp_detail);
        peopleTV = findViewById(R.id.people_count_cp_detail);
        postedByTV = findViewById(R.id.postedByCabpoolDetail);
        postedByImageDV = findViewById(R.id.user_circle_image_in_detail);

       /* time = getIntent().getStringExtra("timeText");
        date = getIntent().getStringExtra("dateText");
        source = getIntent().getStringExtra("sourceText");
        destination = getIntent().getStringExtra("destinationText");
        postedByText = getIntent().getStringExtra("postedByText");
        postedByImageText = getIntent().getStringExtra("postedByImageText");

        timeTV.setText(time);
        dateTV.setText(date);
        sourceTV.setText(source);
        destinationTV.setText(destination);
        peopleTV.setText(people);
        postedByTV.setText(postedByText);
        postedByImageDV.setImageURI(Uri.parse(postedByImageText));*/

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                i=1;

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();

                try {
                    meta.put("source", String.valueOf(source));
                    meta.put("destination", String.valueOf(destination));
                }catch (Exception e){}

                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_SHARE);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                Uri BASE_URI = Uri.parse("http://www.zconnect.com/cabpooling/");

                Uri APP_URI = BASE_URI.buildUpon().appendQueryParameter("key", key)
                        .appendQueryParameter("communityRef", communityReference)
                        .build();
                String encodedUri = null;
                try {
                    encodedUri = URLEncoder.encode(APP_URI.toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if(i==0) {

                    Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                            .setLongLink(Uri.parse("https://zconnect.page.link/?link=" + encodedUri + "&apn=com.zconnect.zutto.zconnect&amv=11"))
                            .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setMinimumVersion(12).build())
                            .buildShortDynamicLink()
                            .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                                @Override
                                public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                    if (task.isSuccessful()) {
                                        //short link
                                        final Uri shortLink = task.getResult().getShortLink();
                                        Uri flowcharLink = task.getResult().getPreviewLink();
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_SEND);
                                        intent.putExtra(Intent.EXTRA_TEXT, "Join my cabpool from " + source +
                                                " to " + destination + " on " +
                                                date +
                                                "\n Use ZConnect app to join the pool \n"
                                                + shortLink);

                                        intent.setType("text/plain");
                                        intent.setPackage("com.whatsapp");
                                        startActivity(intent);

                                    }
                                    else {
                                        Log.d("CabPoolRVAdapter", task.getException().getMessage());
                                    }
                                }
                            });
                }
                if(i==1) {

                    Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                            .setLongLink(Uri.parse("https://zconnect.page.link/?link=" + encodedUri + "&apn=com.zconnect.zutto.zconnect&amv=11"))
                            .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().setMinimumVersion(12).build())
                            .buildShortDynamicLink()
                            .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                                @Override
                                public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                    if (task.isSuccessful()) {
                                        //short link
                                        final Uri shortLink = task.getResult().getShortLink();
                                        Uri flowcharLink = task.getResult().getPreviewLink();
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_SEND);
                                        intent.putExtra(Intent.EXTRA_TEXT, "Join my cabpool from " + source +
                                                " to " + destination + " on " +
                                                date +
                                                "\n Use the ZConnect app to join the pool \n"
                                                + shortLink);
                                        intent.setType("text/plain");
                                        intent.setPackage("com.whatsapp");
                                        startActivity(intent);
                                    }
                                    else {
                                        Log.d("CabPoolRVAdapter", task.getException().getMessage());
                                    }
                                }
                            });
                }
            }
        });

        joinLayout.setVisibility(View.GONE);
        chatLayout.setVisibility(View.VISIBLE);

        chatEditText = (TextView) findViewById(R.id.typer);
        chatEditText.setShowSoftInputOnFocus(false);

        flag = false;
        mAuth = FirebaseAuth.getInstance();

        ref = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserItemFormat userDisplayItem = dataSnapshot.getValue(UserItemFormat.class);
                name = userDisplayItem.getUsername();
                number = userDisplayItem.getMobileNumber();
                numberFlag = true;
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //getting present dates and defining format for input and output date
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat output = new SimpleDateFormat("yyyyMMdd");
        Date = output.format(c.getTime());

        formatted_date = getIntent().getStringExtra("date");

        //Setting old database or new database

            reference = reference_default;

        databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("cabPool").child(reference);
       databaseReference.child(key).child("PostedBy").child("UID").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               reciverKey = (String) dataSnapshot.getValue();
               Log.d(reciverKey, "reciever key");
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
        pool = databaseReference.child(key).child("usersListItemFormats");
        fullPool = databaseReference.child(key);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = (RecyclerView) findViewById(R.id.content_cabpeople_rv);
        progressBar = (ProgressBar) findViewById(R.id.content_cabpeople_progress);
        join = (Button) findViewById(R.id.join);
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");
        join.setTypeface(customFont);
        progressBar.setVisibility(VISIBLE);
        join.setVisibility(INVISIBLE);

        if (mAuth.getCurrentUser() != null)
            uid = mAuth.getCurrentUser().getUid();

        if(getIntent().getBooleanExtra("fromRVCheck", false)){
            time = getIntent().getStringExtra("timeText");
            date = getIntent().getStringExtra("dateText");
            source = getIntent().getStringExtra("sourceText");
            destination = getIntent().getStringExtra("destinationText");
            postedByText = getIntent().getStringExtra("postedByText");
            postedByImageText = getIntent().getStringExtra("postedByImageText");

            timeTV.setText(time);
            dateTV.setText(date);
            sourceTV.setText(source);
            destinationTV.setText(destination);
            peopleTV.setText(people);
            postedByTV.setText(postedByText);
            postedByImageDV.setImageURI(Uri.parse(postedByImageText));

        }else {
            Log.d("fullpool", key);
            fullPool.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("fullpoolref", String.valueOf(fullPool));
                    Log.d("datapool", String.valueOf(dataSnapshot.child("source").getValue()));
                    source = dataSnapshot.child("source").getValue().toString();
                    destination = dataSnapshot.child("destination").getValue().toString();

                    DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/MM/yyyy");
                    DateTime date2 = dtf.parseDateTime(dataSnapshot.child("date").getValue().toString());
                    date = date2.toString("MMM") + " " + date2.getDayOfMonth();

                    String fromAmPm = Integer.parseInt(dataSnapshot.child("from").getValue().toString()) < 12 ? "AM" : "PM";
                    int fromTime = Integer.parseInt(dataSnapshot.child("from").getValue().toString()) <= 12 ? Integer.parseInt(dataSnapshot.child("from").getValue().toString()) : Integer.parseInt(dataSnapshot.child("from").getValue().toString()) - 12;
                    fromTime = fromTime == 0 ? 12 : fromTime;
                    String toAmPm = Integer.parseInt(dataSnapshot.child("to").getValue().toString()) < 12 ? "AM" : "PM";
                    int toTime = Integer.parseInt(dataSnapshot.child("to").getValue().toString()) <= 12 ? Integer.parseInt(dataSnapshot.child("to").getValue().toString()) : Integer.parseInt(dataSnapshot.child("to").getValue().toString()) - 12;
                    toTime = toTime == 0 ? 12 : toTime;
                    time = fromTime + " " + fromAmPm + " - " + toTime + " " + toAmPm;

                    postedByText = dataSnapshot.child("PostedBy").child("Username").getValue().toString();
                    postedByImageText = dataSnapshot.child("PostedBy").child("ImageThumb").getValue().toString();

                    timeTV.setText(time);
                    dateTV.setText(date);
                    sourceTV.setText(source);
                    destinationTV.setText(destination);
                    peopleTV.setText(people);
                    postedByTV.setText(postedByText);
                    postedByImageDV.setImageURI(Uri.parse(postedByImageText));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        pool.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                flag = false;
                usersListItemFormatVector.clear();
                long noOfPersons = dataSnapshot.getChildrenCount();
                people = noOfPersons + (noOfPersons>1?" Persons":" Person");
                peopleTV.setText(people);


                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    UsersListItemFormat usersListItemFormat;
                    usersListItemFormat = shot.getValue(UsersListItemFormat.class);
                    if (usersListItemFormat.getUserUID().equals(uid)) {
                        flag = true;
                    }
                    usersListItemFormatVector.add(usersListItemFormat);
                }

                if (flag) {
                    join.setText("Leave");
                    joinButton.setVisibility(View.GONE);
                    joinLayout.setVisibility(View.GONE);
                    chatLayout.setVisibility(View.VISIBLE);
                } else {
                    join.setText("Join");
                    joinButton.setVisibility(View.VISIBLE);
                    joinLayout.setVisibility(View.VISIBLE);
                    chatLayout.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
                join.setVisibility(VISIBLE);
                progressBar.setVisibility(INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(INVISIBLE);
            }
        });


        adapter = new UsersListRVAdapter(this, usersListItemFormatVector, FeatureNamesUtilities.KEY_CABPOOL, ForumsUserTypeUtilities.KEY_USER);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        recyclerView.setAdapter(adapter);
        pool.keepSynced(true);


        View.OnClickListener chatListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta = new HashMap<>();

                meta.put("type", "fromTextBox");

                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_CHAT_OPEN);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();


                if (forumUID == null) {

                    Intent intent = new Intent(CabPoolListOfPeople.this, ChatActivity.class);
                    intent.putExtra("type", "cabPool");
                    intent.putExtra("key", key);
                    intent.putExtra("ref", databaseReference.child(key).toString());
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child("others").child(forumUID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("name").getValue().toString();

                            Intent intent = new Intent(CabPoolListOfPeople.this, ChatActivity.class);
                            intent.putExtra("type", "forums");
                            intent.putExtra("key", forumUID);
                            intent.putExtra("name", name);
                            intent.putExtra("tab", "others");
                            intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(forumUID).toString());

                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }
        };
        chatLayout.setOnClickListener(chatListener);

        chatEditText.setOnClickListener(chatListener);


        join.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!flag) {
                    final UsersListItemFormat userDetails = new UsersListItemFormat();
                    DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    final DatabaseReference forumCategory = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child("others").child(key);
                    user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                            userDetails.setImageThumb(userItemFormat.getImageURLThumbnail());
                            userDetails.setName(userItemFormat.getUsername());
                            userDetails.setPhonenumber(userItemFormat.getMobileNumber());
                            userDetails.setUserUID(userItemFormat.getUserUID());
                            databaseReference.child(key).child("usersListItemFormats").child(userItemFormat.getUserUID()).setValue(userDetails);

                            // NotificationSender notificationSender = new NotificationSender(getIntent().getStringExtra("key"),null,null,null,null,null,userItemFormat.getUsername(), OtherKeyUtilities.KEY_CABPOOL_JOIN,false,true,CabPoolListOfPeople.this);
                            NotificationSender notificationSender = new NotificationSender(CabPoolListOfPeople.this, userItemFormat.getUserUID());
                            NotificationItemFormat cabPoolJoinNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_JOIN, userItemFormat.getUserUID(),reciverKey,1);
                            cabPoolJoinNotification.setCommunityName(communityTitle);
                            cabPoolJoinNotification.setUserImage(userItemFormat.getImageURLThumbnail());
                            cabPoolJoinNotification.setItemKey(getIntent().getStringExtra("key"));
                            cabPoolJoinNotification.setUserName(userItemFormat.getUsername());
                            cabPoolJoinNotification.setRecieverKey(reciverKey);
                            notificationSender.execute(cabPoolJoinNotification);
                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                            HashMap<String, String> meta = new HashMap<>();

                            meta.put("type", "fromList");
                            meta.put("key", getIntent().getStringExtra("key"));

                            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                            counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_JOIN);
                            counterItemFormat.setTimestamp(System.currentTimeMillis());
                            counterItemFormat.setMeta(meta);
                            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                            counterPush.pushValues();

                            FirebaseMessaging.getInstance().subscribeToTopic(getIntent().getStringExtra("key"));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cabpool_members_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_chat) {

            CounterItemFormat counterItemFormat = new CounterItemFormat();
            HashMap<String, String> meta = new HashMap<>();

            meta.put("type", "fromMenu");

            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
            counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_CHAT_OPEN);
            counterItemFormat.setTimestamp(System.currentTimeMillis());
            counterItemFormat.setMeta(meta);

            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
            counterPush.pushValues();

            if (forumUID == null) {

                Intent intent = new Intent(CabPoolListOfPeople.this, ChatActivity.class);
                intent.putExtra("type", "cabPool");
                intent.putExtra("key", key);
                intent.putExtra("ref", databaseReference.child(key).toString());
                startActivity(intent);
                overridePendingTransition(0, 0);
            } else {
                FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("forumsCategories").child("others").child(forumUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();

                        Intent intent = new Intent(CabPoolListOfPeople.this, ChatActivity.class);
                        intent.putExtra("type", "forums");
                        intent.putExtra("key", forumUID);
                        intent.putExtra("name", name);
                        intent.putExtra("tab", "others");
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(forumUID).toString());

                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        } else if (id == R.id.leave_pool) {

            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CabPoolListOfPeople.this);
            builder.setMessage("Please confirm to leave this pool.")
                    .setCancelable(false)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            databaseReference.child(key).child("usersListItemFormats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                            FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
                            DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            user.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                                    NotificationSender notificationSender = new NotificationSender(CabPoolListOfPeople.this, userItemFormat.getUserUID());
                                    NotificationItemFormat cabPoolLeaveNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_LEAVE, userItemFormat.getUserUID());
                                    cabPoolLeaveNotification.setCommunityName(communityTitle);
                                    cabPoolLeaveNotification.setItemKey(getIntent().getStringExtra("key"));
                                    cabPoolLeaveNotification.setUserName(userItemFormat.getUsername());
                                    cabPoolLeaveNotification.setUserImage(userItemFormat.getImageURLThumbnail());
                                    notificationSender.execute(cabPoolLeaveNotification);

                                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                                    HashMap<String, String> meta = new HashMap<>();

                                    meta.put("type", "fromList");
                                    meta.put("key", getIntent().getStringExtra("key"));

                                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                    counterItemFormat.setUniqueID(CounterUtilities.KEY_CABPOOL_LEAVE);
                                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                                    counterItemFormat.setMeta(meta);
                                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                    counterPush.pushValues();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    })
                    .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            final android.app.AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorHighlight));

        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (flag) {
            menu.findItem(R.id.leave_pool).setVisible(true);
        } else {
            menu.findItem(R.id.leave_pool).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
