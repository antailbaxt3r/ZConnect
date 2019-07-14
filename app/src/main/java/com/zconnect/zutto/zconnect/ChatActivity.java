package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.accessibilityservice.GestureDescription;
import android.app.AlertDialog;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.linkedin.android.spyglass.mentions.Mentionable;
import com.linkedin.android.spyglass.suggestions.SuggestionsResult;
import com.linkedin.android.spyglass.suggestions.interfaces.Suggestible;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsResultListener;
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsVisibilityManager;
import com.linkedin.android.spyglass.tokenization.QueryToken;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizer;
import com.linkedin.android.spyglass.tokenization.impl.WordTokenizerConfig;
import com.linkedin.android.spyglass.tokenization.interfaces.QueryTokenReceiver;
import com.linkedin.android.spyglass.tokenization.interfaces.Tokenizer;
import com.linkedin.android.spyglass.ui.MentionsEditText;
import com.linkedin.android.spyglass.ui.RichEditorView;
import com.percolate.mentions.Mentions;
import com.percolate.mentions.QueryListener;
import com.percolate.mentions.SuggestionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zconnect.zutto.zconnect.addActivities.CreateForum;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.DBHelper;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.commonModules.newUserVerificationAlert;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserMentionsFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities;
import com.zconnect.zutto.zconnect.utilities.MessageTypeUtilities;
import com.zconnect.zutto.zconnect.adapters.ChatRVAdapter;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//TODO: IMPROVE ANONYMOUS MODE
public class ChatActivity extends BaseActivity implements QueryTokenReceiver, SuggestionsResultListener, SuggestionsVisibilityManager {

    private String TAG = ChatActivity.class.getSimpleName();
    NotificationItemFormat notificationItemFormat;
    private static final String BUCKET = "people-network";


    private static final int GALLERY_REQUEST = 7;
    private String ref = "Misc";
    public RecyclerView chatView;
    public RecyclerView.Adapter adapter;
    private DatabaseReference databaseReference;
    private DatabaseReference forumCategory = null;
    private Calendar calendar;
    private ArrayList<ChatItemFormats> messages = new ArrayList<>();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String type = null;
    private Button joinButton;
    private LinearLayout joinLayout, chatLayout;
    private Menu menu;
    private ProgressBar progressBar;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private static boolean unseenFlag, unseenFlag2;
    private String recieverKey;
    private boolean isAnonymousEnabled = false;
    private final char DELIMIETER =(char)1;

    //For Photo Posting
    private IntentHandle intentHandle;
    private Intent galleryIntent;
    private Uri mImageUri = null;
    private StorageReference mStorage;
    private String userType = ForumsUserTypeUtilities.KEY_USER;
    private ValueEventListener loadMessagesListener;

    //For Sharing
    String shareMessageType = null;
    String shareMessage = null;

    //For Storeroom message
    String storeRoomMessage;
    private AppBarLayout appBarLayout;
    private FrameLayout chatFrameLayout;

    //UI elements
    MentionsEditText typer;
    ImageView anonymousSendBtn;


    //User Mentions
    UserMentionsFormat.MentionsLoader mentionsLoader;
    RecyclerView mentionsRecyclerView;
    private String actualMessage;
//    private Map<Integer,UserMentionsFormat> mentionedUsersList = new HashMap<>();
    ArrayList<UserMentionsFormat> mentionedUsersList = new ArrayList<>();
    private static final WordTokenizerConfig tokenizerConfig = new WordTokenizerConfig
            .Builder()
            .setExplicitChars("@")
            .setMaxNumKeywords(1)
            .setThreshold(1)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }



        mentionsRecyclerView = findViewById(R.id.mentions_grid);
        mentionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MentionsAdapter(new ArrayList<UserMentionsFormat>());
        mentionsRecyclerView.setAdapter(adapter);
        anonymousSendBtn = findViewById(R.id.sendAnonymousButton);

        typer = ((MentionsEditText) findViewById(R.id.typer));

        typer.setTokenizer(new WordTokenizer(tokenizerConfig));
        typer.setQueryTokenReceiver(this);
        typer.setSuggestionsVisibilityManager(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);
//        setTitle("List of people");
        setToolbar();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

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

//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        mAuth = FirebaseAuth.getInstance();
        appBarLayout = findViewById(R.id.appBarLayout);


        //For Photo Posting
        intentHandle = new IntentHandle();
        unseenFlag = true;
        unseenFlag2 = false;


        SharedPreferences communitySP;
        final String communityReference;
        communitySP = ChatActivity.this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
        Intent callingActivityIntent = getIntent();
        if (callingActivityIntent.getStringExtra(ForumUtilities.KEY_MESSAGE_TYPE_STR) != null) {
            shareMessage = callingActivityIntent.getStringExtra(ForumUtilities.KEY_MESSAGE);
            shareMessageType = callingActivityIntent.getStringExtra(ForumUtilities.KEY_MESSAGE_TYPE_STR);
        }
        if (callingActivityIntent.getStringExtra("store_room_message") != null) {
            storeRoomMessage = callingActivityIntent.getStringExtra("store_room_message");
        }
        mUserReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (getIntent() != null && !TextUtils.isEmpty(getIntent().getStringExtra("ref"))) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("ref"))) {

                ref = getIntent().getStringExtra("ref");
                Log.d("Ref", ref);
            }
            Log.d("Ref", ref);

            if (!TextUtils.isEmpty(getIntent().getStringExtra("type"))) {
                type = getIntent().getStringExtra("type");
            }
        }
        joinButton = (Button) findViewById(R.id.join);
        joinLayout = (LinearLayout) findViewById(R.id.joinLayout);
        chatLayout = (LinearLayout) findViewById(R.id.chatLayout);

        joinLayout.setVisibility(View.GONE);
        chatLayout.setVisibility(View.VISIBLE);
        chatFrameLayout = findViewById(R.id.chat_frame_layout);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ref);
        Log.d("Try", databaseReference.getParent().toString());

        if (FirebaseAuth.getInstance().getCurrentUser().getUid() == null) {
            showToast("You have to be logged in to chat");
            finish();
        }

        if (type.equals("forums")) {
            toolbar.setTitle(getIntent().getStringExtra("name"));
            setActionBarTitle(getIntent().getStringExtra("name"));
            Log.d("Setting it to: ",getIntent().getStringExtra("name"));
        } else if (type.equals("cabPool")) {
            toolbar.setTitle("Discussion");

        } else if (type.equals("events")) {
            toolbar.setTitle("Discussion");
        } else if (type.equals("messages")) {
            toolbar.setTitle("Comments");
        } else if (type.equals("storeroom")) {
            toolbar.setTitle("Chat with seller");
        } else if (type.equals("post")) {
            toolbar.setTitle("Comments");
        } else if (type.equals("personalChats")) {
            Log.d("Setting it to:", getIntent().getStringExtra("name"));
//            setActionBarTitle(getIntent().getStringExtra("name"));
//            setActionBarTitle("HERE");

        }

        if (type != null) {
            if (type.equals("cabPool")) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        recieverKey = (String) dataSnapshot.child("PostedBy").child("UID").getValue();
                        if (!dataSnapshot.child("usersListItemFormats").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            joinButton.setVisibility(View.VISIBLE);
                            joinLayout.setVisibility(View.VISIBLE);
                            chatLayout.setVisibility(View.GONE);


                            joinButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    final UsersListItemFormat userDetails = new UsersListItemFormat();
                                    DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    user.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                                            userDetails.setImageThumb(userItemFormat.getImageURLThumbnail());
                                            userDetails.setName(userItemFormat.getUsername());
                                            userDetails.setPhonenumber(userItemFormat.getMobileNumber());
                                            userDetails.setUserUID(userItemFormat.getUserUID());
                                            databaseReference.child("usersListItemFormats").child(userItemFormat.getUserUID()).setValue(userDetails);

                                            NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItemFormat.getUserUID());
                                            NotificationItemFormat cabPoolJoinNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_JOIN, userItemFormat.getUserUID(),recieverKey,1);
                                            cabPoolJoinNotification.setCommunityName(communityTitle);
                                            cabPoolJoinNotification.setItemKey(getIntent().getStringExtra("key"));
                                            cabPoolJoinNotification.setUserName(userItemFormat.getUsername());
                                            cabPoolJoinNotification.setUserImage(userItemFormat.getImageURLThumbnail());
                                            cabPoolJoinNotification.setRecieverKey(recieverKey);
                                            Log.d(recieverKey, "reciverkey");
                                            notificationSender.execute(cabPoolJoinNotification);

                                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                                            HashMap<String, String> meta = new HashMap<>();

                                            meta.put("type", "fromChat");
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
                            });
                        } else {
                            joinButton.setVisibility(View.GONE);
                            joinLayout.setVisibility(View.GONE);
                            chatLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else if (type.equals("forums")) {
                final String key, tab;
                key = getIntent().getStringExtra("key");
                tab = getIntent().getStringExtra("tab");
                forumCategory = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(tab).child(key);
                //TODO usersReference IS HARDCODED
                setActionBarTitle(getIntent().getStringExtra("name"));
                toolbar.setTitle(getIntent().getStringExtra("name"));




                forumCategory.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        try {
//                            setToolbarTitle(dataSna/pshot.child("name").getValue().toString());
                            ArrayList<UserMentionsFormat> userMentionsFormats = new ArrayList<>();
                            for (DataSnapshot users : dataSnapshot.child("users").getChildren()) {
                                UserMentionsFormat userMentionsFormat = new UserMentionsFormat();
                                userMentionsFormat.setUsername(users.child("name").getValue().toString());
                                userMentionsFormat.setUserImage(users.child("imageThumb").getValue().toString());
                                userMentionsFormat.setUserUID(users.child("userUID").getValue().toString());
                                userMentionsFormats.add(userMentionsFormat);
                            }
                            mentionsLoader = new UserMentionsFormat.MentionsLoader(userMentionsFormats);

                            if (!dataSnapshot.child("users").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                joinButton.setVisibility(View.VISIBLE);
                                joinLayout.setVisibility(View.VISIBLE);
                                chatLayout.setVisibility(View.GONE);

                                joinButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                                        HashMap<String, String> meta = new HashMap<>();
                                        meta.put("type", "fromChat");
                                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                        counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_JOINED);
                                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                                        counterItemFormat.setMeta(meta);
                                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                        counterPush.pushValues();
                                        final UsersListItemFormat userDetails = new UsersListItemFormat();
                                        DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                        user.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot2) {
                                                UserItemFormat userItemFormat = dataSnapshot2.getValue(UserItemFormat.class);
                                                userDetails.setImageThumb(userItemFormat.getImageURLThumbnail());
                                                userDetails.setName(userItemFormat.getUsername());
                                                userDetails.setPhonenumber(userItemFormat.getMobileNumber());
                                                userDetails.setUserUID(userItemFormat.getUserUID());
                                                userDetails.setUserType(ForumsUserTypeUtilities.KEY_USER);
                                                forumCategory.child("users").child(userItemFormat.getUserUID()).setValue(userDetails);

                                                FirebaseMessaging.getInstance().subscribeToTopic(getIntent().getStringExtra("key"));
//
//                                            NotificationSender notificationSender=new NotificationSender(getIntent().getStringExtra("key"),dataSnapshot.child("name").getValue().toString(),FirebaseAuth.getInstance().getCurrentUser().getUid(),null,null,null,userItemFormat.getUsername(), OtherKeyUtilities.KEY_FORUMS_JOIN,false,true,ChatActivity.this);
//                                            notificationSender.execute();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });
                            } else {
                                joinButton.setVisibility(View.GONE);
                                joinLayout.setVisibility(View.GONE);
                                chatLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else if (type.equals("personalChats")) {

                final String key, tab;
                key = getIntent().getStringExtra("key");
                tab = getIntent().getStringExtra("tab");
                Log.d("Setting it to:", getIntent().getStringExtra("name"));

                setToolbarTitle(getIntent().getStringExtra("name"));

                final DatabaseReference forumCategory = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(tab).child(key);
                joinButton.setVisibility(View.GONE);
                joinLayout.setVisibility(View.GONE);
                chatLayout.setVisibility(View.VISIBLE);

            }

        }
        calendar = Calendar.getInstance();
        chatView = (RecyclerView) findViewById(R.id.chatList);
        if(getIntent().getStringExtra("forumType") != null) {
            if (getIntent().getStringExtra("forumType").equals(ForumUtilities.VALUE_COMMENTS)) {
                adapter = new ChatRVAdapter(messages, databaseReference, forumCategory, this, ForumUtilities.VALUE_COMMENTS);
            } else {
                adapter = new ChatRVAdapter(messages, databaseReference, forumCategory, this, ForumUtilities.VALUE_NORMAL_FORUM);

            }
        }
        else{
            adapter = new ChatRVAdapter(messages, databaseReference, forumCategory, this, ForumUtilities.VALUE_NORMAL_FORUM);

        }
        progressBar = (ProgressBar) findViewById(R.id.activity_chat_progress_circle);
        progressBar.setVisibility(View.VISIBLE);
        chatView.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);
        chatView.setLayoutManager(linearLayoutManager);
        chatView.setAdapter(adapter);

        findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                        if (dataSnapshot.hasChild("userType")) {
                            if (userItemFormat.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItemFormat.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                                newUserVerificationAlert.buildAlertCheckNewUser(userItemFormat.getUserType(), "Chat", ChatActivity.this);
                            } else {

                                postMessage(false);
                            }
                        } else {
                            postMessage(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        anonymousSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                        if (dataSnapshot.hasChild("userType")) {
                            if (userItemFormat.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItemFormat.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                                newUserVerificationAlert.buildAlertCheckNewUser(userItemFormat.getUserType(), "Chat", ChatActivity.this);
                            } else {

                                postMessage(true);
                            }
                        } else {
                            postMessage(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        loadMessagesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatItemFormats temp = new ChatItemFormats();

                    temp = snapshot.getValue(ChatItemFormats.class);

                    temp.setKey(snapshot.getKey());

                    if (!snapshot.hasChild("messageType")) {
                        temp.setMessageType(MessageTypeUtilities.KEY_MESSAGE_STR);
                    }
                    if (isAnonymousEnabled) {
                        messages.add(temp);
                    } else {
                        if (!snapshot.hasChild("anonymous") || snapshot.child("anonymous").getValue().toString().equals("false")) {
                            messages.add(temp);
                        } else {
                            temp.setMessageType(MessageTypeUtilities.KEY_ANONYMOUS_MESSAGE_STR);
                            messages.add(temp);
                        }
                    }
                }


                if (type.equals("forums") || type.equals("others") || type.equals("personalChats")) {
                    DBHelper mydb = new DBHelper(ChatActivity.this);
                    setActionBarTitle(getIntent().getStringExtra("name"));
                    toolbar.setTitle(getIntent().getStringExtra("name"));


                    String key, tab, name;
                    int unseen_num;
                    key = getIntent().getStringExtra("key");
                    tab = getIntent().getStringExtra("tab");
                    name = getIntent().getStringExtra("name");
                    if (getIntent().getStringExtra("unseen_num") != null)
                        unseen_num = Integer.parseInt(getIntent().getStringExtra("unseen_num"));
                    else
                        unseen_num = 0;
                    mydb.replaceForum(name, key, tab, messages.size());
                    mydb.close();
                    Log.d("readNumber",messages.size()+"");
                    if (unseenFlag) {
                        chatView.scrollToPosition(messages.size() - 1 - unseen_num);
                        unseenFlag = false;
                    }
                    if (unseenFlag2) {
                        chatView.scrollToPosition(messages.size() - 1);
                    }
                }
                adapter.notifyDataSetChanged();
                if (!type.equals("forums"))
                    chatView.scrollToPosition(messages.size() - 1);
                progressBar.setVisibility(View.GONE);
                chatView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showSnack("Unable to load messages");
                progressBar.setVisibility(View.GONE);
                chatView.setVisibility(View.VISIBLE);
            }
        };

        typer.addMentionWatcher(new MentionsEditText.MentionWatcher() {
            @Override
            public void onMentionAdded(@NonNull Mentionable mention, @NonNull String text, int start, int end) {


            }

            @Override
            public void onMentionDeleted(@NonNull Mentionable mention, @NonNull String text, int start, int end) {
                mentionedUsersList.remove((UserMentionsFormat)mention);
            }

            @Override
            public void onMentionPartiallyDeleted(@NonNull Mentionable mention, @NonNull String text, int start, int end) {

            }
        });

        //Posting Photo
        findViewById(R.id.chat_photo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        ChatActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            ChatActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            12345
                    );
                } else {
                    galleryIntent = intentHandle.getPickImageIntent(ChatActivity.this);
                    startActivityForResult(galleryIntent, GALLERY_REQUEST);
                }
            }
        });
        if (shareMessageType != null) {
            if (shareMessageType.equals(ForumUtilities.VALUE_MESSAGE_TEXT_MESSAGE)) {
                typer.setText(shareMessage);
                postMessage(false);
            }
            if (shareMessageType.equals(ForumUtilities.VALUE_MESSAGE_IMAGE)) {
                mImageUri = Uri.parse(shareMessage);
                postPhoto();
            }
        }

    }

    private void postMessage(boolean anonymous) {

//        final EditText typer = ((EditText) findViewById(R.id.typer));
        final String text;
        if (TextUtils.isEmpty(typer.getText().toString().trim())) {
            showToast("Message is empty.");
            return;
        }
        String messagePushID = databaseReference.child("Chat").push().getKey();

        if (storeRoomMessage != null) {
            text = storeRoomMessage + "\n" + typer.getText().toString().trim();
            storeRoomMessage = null;
        } else {
            String text1 = typer.getText().toString().trim();
            String textCopy = text1;

                for(UserMentionsFormat user : mentionedUsersList){
                    if(text1.contains(user.getUsername())){
                        String textCopy1 = textCopy;
                        String uid = user.getUserUID();
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(uid);
                        userRef.child("mentionedChats").child(getIntent().getStringExtra("key")).child(messagePushID).setValue(" ");
//                        textCopy.replace( text.indexOf("@"+user.getUsername()),user.getUsername().length()+1,"AA");
                        textCopy = textCopy1.substring(0,
                                textCopy1.indexOf(user.getUsername())) + "@"+ user.getUsername()+ "~" + user.getUserUID()+ ";" + textCopy1.substring(textCopy1.indexOf(user.getUsername())+user.getUsername().length());
                    }
                Log.d("TryMention",textCopy);

            }
            mentionedUsersList.clear();

                text = textCopy;
        }
        unseenFlag2 = true;
        final ChatItemFormats message = new ChatItemFormats();
        message.setTimeDate(calendar.getTimeInMillis());
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);
                message.setUuid(userItem.getUserUID());
                message.setName(userItem.getUsername());
                if (userItem.getAnonymousUsername() != null) {
                    message.setUserName(userItem.getAnonymousUsername());

                } else {
                    message.setUserName("Unknown");
                }
                message.setImageThumb(userItem.getImageURLThumbnail());
                message.setMessage("\"" + text + "\"");
                GlobalFunctions.addPoints(2);
                message.setKey(messagePushID);
                if(anonymous){
                    message.setMessageType(MessageTypeUtilities.KEY_ANONYMOUS_MESSAGE_STR);
                }
                else{
                    message.setMessageType(MessageTypeUtilities.KEY_MESSAGE_STR);

                }

                databaseReference.child("Chat").child(messagePushID).setValue(message);
//                messages.add(message);

//                adapter.notifyDataSetChanged();
                if (type.equals("forums")) {
                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                    NotificationItemFormat forumChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_FORUM, userItem.getUserUID());

                    forumChatNotification.setItemMessage(text);
                    forumChatNotification.setItemCategoryUID(getIntent().getStringExtra("tab"));
                    forumChatNotification.setItemName(getIntent().getStringExtra("name"));
                    forumChatNotification.setItemKey(getIntent().getStringExtra("key"));

                    forumChatNotification.setUserImage(userItem.getImageURLThumbnail());
                    forumChatNotification.setUserName(userItem.getUsername());
                    forumChatNotification.setCommunityName(communityTitle);

                    notificationSender.execute(forumChatNotification);

                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("tab").getValue().toString() != null) {
                                FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(dataSnapshot.child("tab").getValue().toString()).child(getIntent().getStringExtra("key")).child("lastMessage").setValue(message);
                            } else {
                                FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(getIntent().getStringExtra("tab")).child(getIntent().getStringExtra("key")).child("lastMessage").setValue(message);

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else if (type.equals("storeroom")) {

                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                    NotificationItemFormat productChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_PRODUCT, userItem.getUserUID());

                    productChatNotification.setItemMessage(text);
                    productChatNotification.setItemName(getIntent().getStringExtra("name"));
                    productChatNotification.setItemKey(getIntent().getStringExtra("key"));

                    productChatNotification.setUserImage(userItem.getImageURLThumbnail());
                    productChatNotification.setUserName(userItem.getUsername());
                    productChatNotification.setCommunityName(communityTitle);

                    notificationSender.execute(productChatNotification);

                } else if (type.equals("post")) {
                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());
                    HashMap<String,Object> metadata = new HashMap<>();
                    NotificationItemFormat postChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_POST, userItem.getUserUID());
                    postChatNotification.setItemMessage(text);
                    postChatNotification.setItemKey(getIntent().getStringExtra("key"));
                    postChatNotification.setUserImage(userItem.getImageURLThumbnail());
                    postChatNotification.setUserName(userItem.getUsername());
                    postChatNotification.setCommunityName(communityTitle);
                    metadata.put("key",getIntent().getStringExtra("key"));
                    metadata.put("ref",getIntent().getStringExtra("ref"));
                    metadata.put("type",getIntent().getStringExtra("type"));
                    metadata.put("uid",getIntent().getStringExtra("uid"));
                    GlobalFunctions.inAppNotifications("commented on your status","Comment: "+text,userItem,false,"statusComment",metadata,getIntent().getStringExtra("uid"));
                    notificationSender.execute(postChatNotification);

                } else if (type.equals("messages")) {
                    NotificationSender notificationSender = new NotificationSender(getIntent().getStringExtra("userKey"), userItem.getUserUID(), null, null, null, null, userItem.getUsername(), OtherKeyUtilities.KEY_MESSAGES_CHAT, false, true, ChatActivity.this);
                    notificationSender.execute();
                } else if (type.equals("events")) {
                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                    NotificationItemFormat eventChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_EVENT, userItem.getUserUID());

                    eventChatNotification.setItemMessage(text);
                    eventChatNotification.setItemKey(getIntent().getStringExtra("key"));
                    eventChatNotification.setItemName(getIntent().getStringExtra("name"));

                    eventChatNotification.setUserImage(userItem.getImageURLThumbnail());
                    eventChatNotification.setUserName(userItem.getUsername());
                    eventChatNotification.setCommunityName(communityTitle);

                    notificationSender.execute(eventChatNotification);
                } else if (type.equals("cabPool")) {
                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                    NotificationItemFormat cabChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_CAB, userItem.getUserUID());

                    cabChatNotification.setItemMessage(text);
                    cabChatNotification.setItemKey(getIntent().getStringExtra("key"));

                    cabChatNotification.setUserImage(userItem.getImageURLThumbnail());
                    cabChatNotification.setUserName(userItem.getUsername());
                    cabChatNotification.setCommunityName(communityTitle);

                    notificationSender.execute(cabChatNotification);
                } else if (type.equals("personalChats")) {
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child("personalChats").child(getIntent().getStringExtra("key")).child("lastMessage").setValue(message);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        typer.setText(null);
        // chatView.scrollToPosition(chatView.getChildCount());

    }

    private void postPhoto() {

        mStorage = FirebaseStorage.getInstance().getReference();

        final ChatItemFormats message = new ChatItemFormats();
        final ChatItemFormats messageTemp = new ChatItemFormats();

        message.setTimeDate(calendar.getTimeInMillis());

        if (mImageUri != null) {
            Log.d("L", Integer.toString(messages.size()));
            messageTemp.setUuid(mAuth.getCurrentUser().getUid());
            messageTemp.setName(mAuth.getCurrentUser().getDisplayName());
            messageTemp.setPhotoURL(mImageUri != null ? mImageUri.toString() : null);
            messageTemp.setImageThumb(mImageUri.toString());
            messageTemp.setMessage(" \uD83D\uDCF7 Image ");
            messageTemp.setMessageType(MessageTypeUtilities.KEY_PHOTO_STR);
            messages.add(messageTemp);
//            messages.add()
            messages.add(messages.get(0));
            Log.d("L", Integer.toString(messages.size()));
            adapter.notifyDataSetChanged();


            final StorageReference filePath = mStorage.child(communityReference).child("features").child(type).child((mImageUri.getLastPathSegment()) + mAuth.getCurrentUser().getUid());
            UploadTask uploadTask = filePath.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final Uri downloadUri = task.getResult();
                        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);
                                message.setUuid(userItem.getUserUID());
                                message.setName(userItem.getUsername());
                                message.setPhotoURL(downloadUri != null ? downloadUri.toString() : null);
                                message.setImageThumb(userItem.getImageURLThumbnail());
                                message.setMessage(" \uD83D\uDCF7 Image ");
                                message.setMessageType(MessageTypeUtilities.KEY_PHOTO_STR);
                                GlobalFunctions.addPoints(5);
                                String messagePushID = databaseReference.child("Chat").push().getKey();
                                message.setKey(messagePushID);
                                databaseReference.child("Chat").child(messagePushID).setValue(message);
                                if (type.equals("forums")) {
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                                    NotificationItemFormat forumChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_FORUM, userItem.getUserUID());

                                    forumChatNotification.setItemMessage(" \uD83D\uDCF7 Image ");
                                    forumChatNotification.setItemCategoryUID(getIntent().getStringExtra("tab"));
                                    forumChatNotification.setItemName(getIntent().getStringExtra("name"));
                                    forumChatNotification.setItemKey(getIntent().getStringExtra("key"));

                                    forumChatNotification.setUserImage(userItem.getImageURLThumbnail());
                                    forumChatNotification.setUserName(userItem.getUsername());
                                    forumChatNotification.setCommunityName(communityTitle);

                                    notificationSender.execute(forumChatNotification);

                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(dataSnapshot.child("tab").getValue().toString()).child(getIntent().getStringExtra("key")).child("lastMessage").setValue(message);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                } else if (type.equals("storeroom")) {
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                                    NotificationItemFormat productChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_PRODUCT, userItem.getUserUID());

                                    productChatNotification.setItemMessage("Image");
                                    productChatNotification.setItemName(getIntent().getStringExtra("name"));
                                    productChatNotification.setItemKey(getIntent().getStringExtra("key"));

                                    productChatNotification.setUserImage(userItem.getImageURLThumbnail());
                                    productChatNotification.setUserName(userItem.getUsername());
                                    productChatNotification.setCommunityName(communityTitle);

                                    notificationSender.execute(productChatNotification);

                                } else if (type.equals("post")) {
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                                    NotificationItemFormat postChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_POST, userItem.getUserUID());

                                    postChatNotification.setItemMessage("Image");
                                    postChatNotification.setItemKey(getIntent().getStringExtra("key"));

                                    postChatNotification.setUserImage(userItem.getImageURLThumbnail());
                                    postChatNotification.setUserName(userItem.getUsername());
                                    postChatNotification.setCommunityName(communityTitle);

                                    notificationSender.execute(postChatNotification);
                                } else if (type.equals("messages")) {
                                    NotificationSender notificationSender = new NotificationSender(getIntent().getStringExtra("userKey"), userItem.getUserUID(), null, null, null, null, userItem.getUsername(), OtherKeyUtilities.KEY_MESSAGES_CHAT, false, true, ChatActivity.this);
                                    notificationSender.execute();
                                } else if (type.equals("events")) {
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                                    NotificationItemFormat eventChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_EVENT, userItem.getUserUID());

                                    eventChatNotification.setItemMessage("Image");
                                    eventChatNotification.setItemKey(getIntent().getStringExtra("key"));
                                    eventChatNotification.setItemName(getIntent().getStringExtra("name"));

                                    eventChatNotification.setUserImage(userItem.getImageURLThumbnail());
                                    eventChatNotification.setUserName(userItem.getUsername());
                                    eventChatNotification.setCommunityName(communityTitle);

                                    notificationSender.execute(eventChatNotification);
                                } else if (type.equals("cabPool")) {
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                                    NotificationItemFormat cabChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_CAB, userItem.getUserUID());

                                    cabChatNotification.setItemMessage("Image");
                                    cabChatNotification.setItemKey(getIntent().getStringExtra("key"));

                                    cabChatNotification.setUserImage(userItem.getImageURLThumbnail());
                                    cabChatNotification.setUserName(userItem.getUsername());
                                    cabChatNotification.setCommunityName(communityTitle);

                                    notificationSender.execute(cabChatNotification);
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        // Handle failures
                        // ...
                        Snackbar snackbar = Snackbar.make(chatView, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT);
                        snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                        snackbar.show();
                    }
                }
            });
        }
        // chatView.scrollToPosition(chatView.getChildCount());
    }


    @Override
    protected void onResume() {
        super.onResume();
        databaseReference.child("Chat").addValueEventListener(loadMessagesListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.child("Chat").removeEventListener(loadMessagesListener);
    }

    public void launchPeopleList() {
        Intent i = new Intent(this, ForumsPeopleList.class);
        i.putExtra("key", getIntent().getStringExtra("key"));
        i.putExtra("tab", getIntent().getStringExtra("tab"));
        i.putExtra("userType", userType);
        startActivity(i);
    }

    public void launchEditForum() {
        String tab = getIntent().getStringExtra("tab");
        String key = getIntent().getStringExtra("key");
        Intent i = new Intent(this, CreateForum.class);
        i.putExtra("uid", tab);
        i.putExtra("catUID", key);
        i.putExtra("flag", "true");
        startActivity(i);
//        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_list_people) {
            CounterItemFormat counterItemFormat = new CounterItemFormat();
            HashMap<String, String> meta = new HashMap<>();
            meta.put("type", "fromFeature");
            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
            counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_LIST_MEMBERS);
            counterItemFormat.setTimestamp(System.currentTimeMillis());
            counterItemFormat.setMeta(meta);
            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
            counterPush.pushValues();
            launchPeopleList();
        }

        if (item.getItemId() == R.id.action_edit_forum) {
            final String tabuid = getIntent().getStringExtra("tab");
            final String catuid = getIntent().getStringExtra("key");
            final DatabaseReference userRefInCat = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(tabuid).child(catuid).child("users").child(user.getUid());
            mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("userType") && dataSnapshot.child("userType").getValue().toString().equals(UsersTypeUtilities.KEY_ADMIN)) {
                        Log.d(TAG, "Community Admin");
                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                        HashMap<String, String> meta = new HashMap<>();
                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                        counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_EDIT_FORUM_OPEN);
                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                        counterItemFormat.setMeta(meta);
                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                        counterPush.pushValues();
                        launchEditForum();
                    } else {
                        userRefInCat.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d(TAG, dataSnapshot.getRef().toString());
                                if (dataSnapshot.hasChild("userType") && dataSnapshot.child("userType").getValue().toString().equals(ForumsUserTypeUtilities.KEY_ADMIN)) {
                                    Log.d(TAG, "Forum Admin");
                                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                                    HashMap<String, String> meta = new HashMap<>();
                                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                    counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_EDIT_FORUM_OPEN);
                                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                                    counterItemFormat.setMeta(meta);
                                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                    counterPush.pushValues();
                                    launchEditForum();
                                } else {
                                    Log.d(TAG, "Normal User");
                                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ChatActivity.this);
                                    builder.setMessage("Only Forum Admins and Community Admins can edit a forum")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Log.d(TAG, "Clicked Ok");
                                                }
                                            });
                                    final android.app.AlertDialog dialog = builder.create();
                                    dialog.setCancelable(true);
                                    dialog.show();
//                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorHighlight));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (!getIntent().getStringExtra("type").equals("forums")) {
            menu.findItem(R.id.action_list_people).setVisible(false);

        }

        if (!getIntent().getStringExtra("type").equals("forums")) {

            menu.findItem(R.id.action_edit_forum).setVisible(false);
        } else {
            final String tabuid = getIntent().getStringExtra("tab");
            if (tabuid.equals("shopPools") || tabuid.equals("otherChats")) {

                menu.findItem(R.id.action_edit_forum).setVisible(false);
            }
            if (tabuid.equals("personalChats")) {
                anonymousSendBtn.setVisibility(View.GONE);
                menu.findItem(R.id.action_edit_forum).setVisible(false);
                menu.findItem(R.id.action_list_people).setVisible(false);
                Log.d("Menu Setting", getIntent().getStringExtra("name"));
                setActionBarTitle(getIntent().getStringExtra("name"));
                toolbar.setTitle(getIntent().getStringExtra("name"));

            }
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data, ChatActivity.this); //Get data
            CropImage.activity(imageUri)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                try {
                    String path2 = compressImage(result.getUri().toString());
//                    mImageUri = Uri.parse(path);
//                    File original = new File(result.getUri().toString());
//                    File file1 = new File(mImageUri.toString());
//                    File file2 = new File(path2);
                    mImageUri = Uri.fromFile(new File(path2));
//                    Log.d("Upload Activity","Size of Original File:"+Double.toString(bitmap.getByteCount()));
//                    Log.d("Upload Activity","Size of previous Comptession:"+Double.toString(bitmap.getByteCount()));
//                    Log.d("Upload Activity","Size of New Comptession:"+Double.toString(file2.length()));


//                    ChatItemFormats temproraryChat = new ChatItemFormats();
//                    temproraryChat.setPhotoURL(mImageUri.toString());
//                    temproraryChat.setMessageType(MessageTypeUtilities.KEY_PHOTO_SENDING_STR);
//                    messages.add(temproraryChat);

                    postPhoto();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                try {
                    throw error;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);

        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            Log.d("Upload Activity", "Size of New Comptession Original:" + Double.toString(scaledBitmap.getByteCount()));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    @Override
    public List<String> onQueryReceived(final @NonNull QueryToken queryToken) {
        List<String> buckets = Collections.singletonList(BUCKET);
        if (mentionsLoader == null) {
            return null;
        }
        List<UserMentionsFormat> suggestions = mentionsLoader.getSuggestions(queryToken);
        SuggestionsResult result = new SuggestionsResult(queryToken, suggestions);
        // Have suggestions, now call the listener (which is this activity)
        onReceiveSuggestionsResult(result, BUCKET);
        Log.d("MENTIONs", queryToken.toString());

        return buckets;
    }

    @Override
    public void onReceiveSuggestionsResult(@NonNull SuggestionsResult result, @NonNull String bucket) {
        Log.d("MENTIONs", result.toString());
        List<? extends Suggestible> suggestions = result.getSuggestions();
        adapter = new MentionsAdapter(result.getSuggestions());
        mentionsRecyclerView.swapAdapter(adapter, true);
        boolean display = suggestions != null && suggestions.size() > 0;
        displaySuggestions(display);
    }

    @Override
    public void displaySuggestions(boolean display) {
        if (display) {
            mentionsRecyclerView.setVisibility(RecyclerView.VISIBLE);
        } else {
            mentionsRecyclerView.setVisibility(RecyclerView.GONE);
        }
    }

    @Override
    public boolean isDisplayingSuggestions() {
        return mentionsRecyclerView.getVisibility() == RecyclerView.VISIBLE;
    }

    public void displaysuggestions(boolean display) {
        if (display) {
            mentionsRecyclerView.setVisibility(RecyclerView.VISIBLE);
        } else {
            mentionsRecyclerView.setVisibility(RecyclerView.GONE);
        }
    }


    public class MentionsViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public SimpleDraweeView picture;

        public MentionsViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.person_name);
            picture = itemView.findViewById(R.id.person_image);
        }
    }


    public class MentionsAdapter extends RecyclerView.Adapter<MentionsViewHolder> {

        public List<? extends Suggestible> suggestions;

        public MentionsAdapter(List<? extends Suggestible> people) {
            suggestions = people;
        }

        @NonNull
        @Override
        public MentionsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mentions_item, viewGroup, false);
            return new MentionsViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MentionsViewHolder viewHolder, int i) {
            Suggestible suggestion = suggestions.get(i);
            if (!(suggestion instanceof UserMentionsFormat)) {
                return;
            }

            final UserMentionsFormat person = (UserMentionsFormat) suggestion;

            if(mentionedUsersList.contains(person)){
                return;
            }

            viewHolder.name.setText(person.getUsername());
            Uri imageuri = Uri.parse(person.getUserImage());
            viewHolder.picture.setImageURI(imageuri);
            Glide.with(viewHolder.picture.getContext())
                    .load(person.getUserImage())
                    .crossFade()
                    .into(viewHolder.picture);

            viewHolder.itemView.setOnClickListener(v -> {

                person.setUsername(person.getUsername());
                typer.insertMention(person);
                suggestions.remove(person);
                Integer mentionPosition = typer.getSelectionStart()- person.getUsername().length()+1;
                mentionedUsersList.add(person);
                mentionsRecyclerView.swapAdapter(new MentionsAdapter(new ArrayList<UserMentionsFormat>()), true);
                displaysuggestions(false);
                typer.requestFocus();
            });
        }

        @Override
        public int getItemCount() {
            return suggestions.size();
        }
    }


}
