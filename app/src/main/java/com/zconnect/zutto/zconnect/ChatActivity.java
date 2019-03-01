package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities;
import com.zconnect.zutto.zconnect.utilities.MessageTypeUtilities;
import com.zconnect.zutto.zconnect.adapters.ChatRVAdapter;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class ChatActivity extends BaseActivity {

    private String TAG = ChatActivity.class.getSimpleName();

    private static final int GALLERY_REQUEST = 7;
    private String ref  = "Misc";
    private RecyclerView chatView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference databaseReference ;
    private Calendar calendar;
    private ArrayList<ChatItemFormats> messages  = new ArrayList<>();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String type=null;
    private Button joinButton;
    private LinearLayout joinLayout,chatLayout;
    private Menu menu;
    private ProgressBar progressBar;
    private DatabaseReference mUserReference;
    private FirebaseAuth mAuth;
    private static boolean unseenFlag, unseenFlag2;

    //For Photo Posting
    private IntentHandle intentHandle;
    private Intent galleryIntent;
    private Uri mImageUri = null;
    private StorageReference mStorage;
    private String userType = ForumsUserTypeUtilities.KEY_USER;
    private ValueEventListener loadMessagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
        setSupportActionBar(toolbar);
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
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        showBackButton();

        mAuth = FirebaseAuth.getInstance();


        //For Photo Posting
        intentHandle = new IntentHandle();
        unseenFlag = true;
        unseenFlag2 = false;

        SharedPreferences communitySP;
        final String communityReference;
        communitySP = ChatActivity.this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
        mUserReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(getIntent()!=null && !TextUtils.isEmpty(getIntent().getStringExtra("ref"))) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("ref"))){
                ref = getIntent().getStringExtra("ref");
            }
            if (!TextUtils.isEmpty(getIntent().getStringExtra("type"))){
                type = getIntent().getStringExtra("type");
            }
        }
        joinButton = (Button) findViewById(R.id.join);
        joinLayout = (LinearLayout) findViewById(R.id.joinLayout);
        chatLayout = (LinearLayout) findViewById(R.id.chatLayout);

        joinLayout.setVisibility(View.GONE);
        chatLayout.setVisibility(View.VISIBLE);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ref);

        if(FirebaseAuth.getInstance().getCurrentUser().getUid()==null) {
            showToast("You have to be logged in to chat");
            finish();
        }

        if (type.equals("forums"))
        {
            setActionBarTitle(getIntent().getStringExtra("name"));
        }else if (type.equals("cabPool")){
            setActionBarTitle("Discussion");
        }else if (type.equals("events")){
            setActionBarTitle("Discussion");
        }else if (type.equals("messages")){
            setActionBarTitle("Comments");
        }else if (type.equals("storeroom")){
            setActionBarTitle("Chat with seller");
        }else if (type.equals("post")){
            setActionBarTitle("Comments");
        }

        if(type!=null){
            if(type.equals("cabPool")){
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.child("usersListItemFormats").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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

                                            NotificationSender notificationSender = new NotificationSender(ChatActivity.this,userItemFormat.getUserUID());
                                            NotificationItemFormat cabPoolJoinNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_JOIN,userItemFormat.getUserUID());
                                            cabPoolJoinNotification.setCommunityName(communityTitle);
                                            cabPoolJoinNotification.setItemKey(getIntent().getStringExtra("key"));
                                            cabPoolJoinNotification.setUserName(userItemFormat.getUsername());
                                            cabPoolJoinNotification.setUserImage(userItemFormat.getImageURLThumbnail());
                                            notificationSender.execute(cabPoolJoinNotification);

                                            CounterItemFormat counterItemFormat = new CounterItemFormat();
                                            HashMap<String, String> meta= new HashMap<>();

                                            meta.put("type","fromChat");
                                            meta.put("key",getIntent().getStringExtra("key"));

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
                        }else {
                            joinButton.setVisibility(View.GONE);
                            joinLayout.setVisibility(View.GONE);
                            chatLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else if (type.equals("forums")){
                final String key,tab;
                key = getIntent().getStringExtra("key");
                tab = getIntent().getStringExtra("tab");
                final DatabaseReference forumCategory = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(tab).child(key);

                forumCategory.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        try {
                            setToolbarTitle(dataSnapshot.child("name").getValue().toString());

                            if (!dataSnapshot.child("users").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                joinButton.setVisibility(View.VISIBLE);
                                joinLayout.setVisibility(View.VISIBLE);
                                chatLayout.setVisibility(View.GONE);

                                joinButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                                        HashMap<String, String> meta= new HashMap<>();
                                        meta.put("type","fromChat");
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
                            }else {
                                joinButton.setVisibility(View.GONE);
                                joinLayout.setVisibility(View.GONE);
                                chatLayout.setVisibility(View.VISIBLE);
                            }
                        }catch (Exception e){}

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        calendar = Calendar.getInstance();
        chatView = (RecyclerView) findViewById(R.id.chatList);
        adapter = new ChatRVAdapter(messages,this);
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
                        if(dataSnapshot.hasChild("userType")) {
                            if (userItemFormat.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || userItemFormat.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                                newUserVerificationAlert.buildAlertCheckNewUser(userItemFormat.getUserType(),"Chat", ChatActivity.this);
                            } else {
                                postMessage();
                            }
                        }else {
                            postMessage();
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
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    ChatItemFormats temp = new ChatItemFormats();

                    temp = snapshot.getValue(ChatItemFormats.class);

                    if (!snapshot.hasChild("messageType")) {
                        temp.setMessageType(MessageTypeUtilities.KEY_MESSAGE_STR);
                    }
                    messages.add(temp);
                }

                if (type.equals("forums")) {
                    DBHelper mydb = new DBHelper(ChatActivity.this);

                    String key, tab, name;
                    int unseen_num;
                    key = getIntent().getStringExtra("key");
                    tab = getIntent().getStringExtra("tab");
                    name = getIntent().getStringExtra("name");
                    if(getIntent().getStringExtra("unseen_num")!=null)
                        unseen_num = Integer.parseInt(getIntent().getStringExtra("unseen_num"));
                    else
                        unseen_num = 0;
                    mydb.replaceForum(name,key,tab,messages.size());
                    if(unseenFlag) {
                        chatView.scrollToPosition(messages.size() - 1 - unseen_num);
                        unseenFlag = false;
                    }
                    if(unseenFlag2) {
                        chatView.scrollToPosition(messages.size() - 1);
                    }
                }
                adapter.notifyDataSetChanged();
                if(!type.equals("forums"))
                    chatView.scrollToPosition(messages.size()-1);
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
    }

    private void postMessage(){

        final EditText typer = ((EditText) findViewById(R.id.typer));
        final String text = typer.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            showToast("Message is empty.");
            return;
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
                message.setImageThumb(userItem.getImageURLThumbnail());
                message.setMessage("\""+text+"\"");
                message.setMessageType(MessageTypeUtilities.KEY_MESSAGE_STR);
                GlobalFunctions.addPoints(2);
                databaseReference.child("Chat").push().setValue(message);
                if (type.equals("forums")){
                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this,userItem.getUserUID());

                    NotificationItemFormat forumChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_FORUM,userItem.getUserUID());

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
                            FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(dataSnapshot.child("tab").getValue().toString()).child(getIntent().getStringExtra("key")).child("lastMessage").setValue(message);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else if(type.equals("storeroom")){

                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this,userItem.getUserUID());

                    NotificationItemFormat productChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_PRODUCT,userItem.getUserUID());

                    productChatNotification.setItemMessage(text);
                    productChatNotification.setItemName(getIntent().getStringExtra("name"));
                    productChatNotification.setItemKey(getIntent().getStringExtra("key"));

                    productChatNotification.setUserImage(userItem.getImageURLThumbnail());
                    productChatNotification.setUserName(userItem.getUsername());
                    productChatNotification.setCommunityName(communityTitle);

                    notificationSender.execute(productChatNotification);

                }else if(type.equals("post")){
                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                    NotificationItemFormat postChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_POST,userItem.getUserUID());

                    postChatNotification.setItemMessage(text);
                    postChatNotification.setItemKey(getIntent().getStringExtra("key"));

                    postChatNotification.setUserImage(userItem.getImageURLThumbnail());
                    postChatNotification.setUserName(userItem.getUsername());
                    postChatNotification.setCommunityName(communityTitle);

                    notificationSender.execute(postChatNotification);

                }else if(type.equals("messages")){
                    NotificationSender notificationSender=new NotificationSender(getIntent().getStringExtra("userKey"),userItem.getUserUID(),null,null,null,null,userItem.getUsername(), OtherKeyUtilities.KEY_MESSAGES_CHAT,false,true,ChatActivity.this);
                    notificationSender.execute();
                }else if(type.equals("events")){
                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this,userItem.getUserUID());

                    NotificationItemFormat eventChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_EVENT,userItem.getUserUID());

                    eventChatNotification.setItemMessage(text);
                    eventChatNotification.setItemKey(getIntent().getStringExtra("key"));
                    eventChatNotification.setItemName(getIntent().getStringExtra("name"));

                    eventChatNotification.setUserImage(userItem.getImageURLThumbnail());
                    eventChatNotification.setUserName(userItem.getUsername());
                    eventChatNotification.setCommunityName(communityTitle);

                    notificationSender.execute(eventChatNotification);
                }else if(type.equals("cabPool")){
                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                    NotificationItemFormat cabChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_CAB,userItem.getUserUID());

                    cabChatNotification.setItemMessage(text);
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

        typer.setText(null);
        // chatView.scrollToPosition(chatView.getChildCount());

    }

    private void postPhoto(){

        mStorage = FirebaseStorage.getInstance().getReference();

        final ChatItemFormats message = new ChatItemFormats();
        message.setTimeDate(calendar.getTimeInMillis());

        if(mImageUri!=null){
            final StorageReference filePath = mStorage.child(communityReference).child("features").child(type).child((mImageUri.getLastPathSegment()) + mAuth.getCurrentUser().getUid());
            UploadTask uploadTask = filePath.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful())
                    {
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
                                databaseReference.child("Chat").push().setValue(message);
                                if (type.equals("forums")){
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                                    NotificationItemFormat forumChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_FORUM,userItem.getUserUID());

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
                                }else if(type.equals("storeroom")){
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                                    NotificationItemFormat productChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_PRODUCT,userItem.getUserUID());

                                    productChatNotification.setItemMessage("Image");
                                    productChatNotification.setItemName(getIntent().getStringExtra("name"));
                                    productChatNotification.setItemKey(getIntent().getStringExtra("key"));

                                    productChatNotification.setUserImage(userItem.getImageURLThumbnail());
                                    productChatNotification.setUserName(userItem.getUsername());
                                    productChatNotification.setCommunityName(communityTitle);

                                    notificationSender.execute(productChatNotification);

                                }else if(type.equals("post")){
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                                    NotificationItemFormat postChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_POST,userItem.getUserUID());

                                    postChatNotification.setItemMessage("Image");
                                    postChatNotification.setItemKey(getIntent().getStringExtra("key"));

                                    postChatNotification.setUserImage(userItem.getImageURLThumbnail());
                                    postChatNotification.setUserName(userItem.getUsername());
                                    postChatNotification.setCommunityName(communityTitle);

                                    notificationSender.execute(postChatNotification);
                                }else if(type.equals("messages")){
                                    NotificationSender notificationSender=new NotificationSender(getIntent().getStringExtra("userKey"),userItem.getUserUID(),null,null,null,null,userItem.getUsername(), OtherKeyUtilities.KEY_MESSAGES_CHAT,false,true,ChatActivity.this);
                                    notificationSender.execute();
                                }else if(type.equals("events")){
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                                    NotificationItemFormat eventChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_EVENT,userItem.getUserUID());

                                    eventChatNotification.setItemMessage("Image");
                                    eventChatNotification.setItemKey(getIntent().getStringExtra("key"));
                                    eventChatNotification.setItemName(getIntent().getStringExtra("name"));

                                    eventChatNotification.setUserImage(userItem.getImageURLThumbnail());
                                    eventChatNotification.setUserName(userItem.getUsername());
                                    eventChatNotification.setCommunityName(communityTitle);

                                    notificationSender.execute(eventChatNotification);
                                }else if(type.equals("cabPool")){
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, userItem.getUserUID());

                                    NotificationItemFormat cabChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_CAB,userItem.getUserUID());

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
                    }
                    else {
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

    public void launchPeopleList(){
        Intent i = new Intent(this,ForumsPeopleList.class);
        i.putExtra("key",getIntent().getStringExtra("key"));
        i.putExtra("tab",getIntent().getStringExtra("tab"));
        i.putExtra("userType",userType);
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
        this.menu=menu;

        getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_list_people) {
            CounterItemFormat counterItemFormat = new CounterItemFormat();
            HashMap<String, String> meta= new HashMap<>();
            meta.put("type","fromFeature");
            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
            counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_LIST_MEMBERS);
            counterItemFormat.setTimestamp(System.currentTimeMillis());
            counterItemFormat.setMeta(meta);
            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
            counterPush.pushValues();
            launchPeopleList();
        }

        if(item.getItemId() == R.id.action_edit_forum) {
            final String tabuid = getIntent().getStringExtra("tab");
            final String catuid = getIntent().getStringExtra("key");
            final DatabaseReference userRefInCat = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(tabuid).child(catuid).child("users").child(user.getUid());
            mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("userType") && dataSnapshot.child("userType").getValue().toString().equals(UsersTypeUtilities.KEY_ADMIN))
                    {
                        Log.d(TAG, "Community Admin");
                        CounterItemFormat counterItemFormat = new CounterItemFormat();
                        HashMap<String, String> meta= new HashMap<>();
                        counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                        counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_EDIT_FORUM_OPEN);
                        counterItemFormat.setTimestamp(System.currentTimeMillis());
                        counterItemFormat.setMeta(meta);
                        CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                        counterPush.pushValues();
                        launchEditForum();
                    }
                    else
                    {
                        userRefInCat.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Log.d(TAG, dataSnapshot.getRef().toString());
                                if(dataSnapshot.hasChild("userType") && dataSnapshot.child("userType").getValue().toString().equals(ForumsUserTypeUtilities.KEY_ADMIN))
                                {
                                    Log.d(TAG, "Forum Admin");
                                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                                    HashMap<String, String> meta= new HashMap<>();
                                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                                    counterItemFormat.setUniqueID(CounterUtilities.KEY_FORUMS_EDIT_FORUM_OPEN);
                                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                                    counterItemFormat.setMeta(meta);
                                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                                    counterPush.pushValues();
                                    launchEditForum();
                                }
                                else
                                {
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

        if(!getIntent().getStringExtra("type").equals("forums")) {
            menu.findItem(R.id.action_list_people).setVisible(false);
        }

        if(!getIntent().getStringExtra("type").equals("forums")){

            menu.findItem(R.id.action_edit_forum).setVisible(false);
        }else {
            final String tabuid = getIntent().getStringExtra("tab");
            if (tabuid.equals("shopPools") || tabuid.equals("otherChats")){

                menu.findItem(R.id.action_edit_forum).setVisible(false);
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
                    mImageUri = result.getUri();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), mImageUri);
                    Double ratio = ((double) bitmap.getWidth()) / bitmap.getHeight();

                    if (bitmap.getByteCount() > 350000) {

                        bitmap = Bitmap.createScaledBitmap(bitmap, 960, (int) (960 / ratio), false);
                    }
                    String path = MediaStore.Images.Media.insertImage(ChatActivity.this.getContentResolver(), bitmap, mImageUri.getLastPathSegment(), null);

                    mImageUri = Uri.parse(path);

                    postPhoto();

                } catch (IOException e) {
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

}
