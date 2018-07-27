package com.zconnect.zutto.zconnect;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.DBHelper;
import com.zconnect.zutto.zconnect.commonModules.IntentHandle;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.commonModules.newUserVerificationAlert;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities;
import com.zconnect.zutto.zconnect.utilities.MessageTypeUtilities;
import com.zconnect.zutto.zconnect.adapters.ChatRVAdapter;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends BaseActivity {

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

    //For Photo Posting
    private IntentHandle intentHandle;
    private Intent galleryIntent;
    private Uri mImageUri = null;
    private StorageReference mStorage;
    private String userType = ForumsUserTypeUtilities.KEY_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setToolbar();
        showBackButton();

        mAuth = FirebaseAuth.getInstance();

        //For Photo Posting
        intentHandle = new IntentHandle();



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
                joinButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.cabpool));

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        FirebaseMessaging.getInstance().subscribeToTopic(getIntent().getStringExtra("key"));
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

                                            NotificationSender notificationSender = new NotificationSender(ChatActivity.this,UserUtilities.currentUser.getUserUID());
                                            NotificationItemFormat cabPoolJoinNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CAB_JOIN,UserUtilities.currentUser.getUserUID());
                                            cabPoolJoinNotification.setCommunityName(UserUtilities.CommunityName);
                                            cabPoolJoinNotification.setItemKey(getIntent().getStringExtra("key"));
                                            cabPoolJoinNotification.setUserName(userItemFormat.getUsername());
                                            cabPoolJoinNotification.setUserImage(userItemFormat.getImageURLThumbnail());
                                            notificationSender.execute(cabPoolJoinNotification);

                                            CounterManager.openCabPoolJoin(getIntent().getStringExtra("key"));
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
                joinButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.forums));
                forumCategory.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        setToolbarTitle(dataSnapshot.child("name").getValue().toString());

                        FirebaseMessaging.getInstance().subscribeToTopic(getIntent().getStringExtra("key"));
                        if (!dataSnapshot.child("users").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
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
                                        public void onDataChange(DataSnapshot dataSnapshot2) {
                                            UserItemFormat userItemFormat = dataSnapshot2.getValue(UserItemFormat.class);
                                            userDetails.setImageThumb(userItemFormat.getImageURLThumbnail());
                                            userDetails.setName(userItemFormat.getUsername());
                                            userDetails.setPhonenumber(userItemFormat.getMobileNumber());
                                            userDetails.setUserUID(userItemFormat.getUserUID());
                                            userDetails.setUserType(ForumsUserTypeUtilities.KEY_USER);
                                            forumCategory.child("users").child(userItemFormat.getUserUID()).setValue(userDetails);

                                            CounterManager.forumsJoinCategory(tab,key);
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

        setSupportActionBar(getToolbar());

        findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED)) {
                    newUserVerificationAlert.buildAlertCheckNewUser("Chat",ChatActivity.this);
                }else {
                    postMessage();

                }
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
        loadMessages();
    }

    private void postMessage(){

        final EditText typer = ((EditText) findViewById(R.id.typer));
        final String text = typer.getText().toString();
        if (TextUtils.isEmpty(text)) {
            showToast("Message is empty.");
            return;
        }

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
                databaseReference.child("Chat").push().setValue(message);
                if (type.equals("forums")){
                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, UserUtilities.currentUser.getUserUID());

                    NotificationItemFormat forumChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_FORUM,UserUtilities.currentUser.getUserUID());

                    forumChatNotification.setItemMessage(text);
                    forumChatNotification.setItemCategoryUID(getIntent().getStringExtra("tab"));
                    forumChatNotification.setItemName(getIntent().getStringExtra("name"));
                    forumChatNotification.setItemKey(getIntent().getStringExtra("key"));

                    forumChatNotification.setUserImage(UserUtilities.currentUser.getImageURLThumbnail());
                    forumChatNotification.setUserName(UserUtilities.currentUser.getUsername());
                    forumChatNotification.setCommunityName(UserUtilities.CommunityName);

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

                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, UserUtilities.currentUser.getUserUID());

                    NotificationItemFormat productChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_PRODUCT,UserUtilities.currentUser.getUserUID());

                    productChatNotification.setItemMessage(text);
                    productChatNotification.setItemName(getIntent().getStringExtra("name"));
                    productChatNotification.setItemKey(getIntent().getStringExtra("key"));

                    productChatNotification.setUserImage(UserUtilities.currentUser.getImageURLThumbnail());
                    productChatNotification.setUserName(UserUtilities.currentUser.getUsername());
                    productChatNotification.setCommunityName(UserUtilities.CommunityName);

                    notificationSender.execute(productChatNotification);

                }else if(type.equals("post")){
                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, UserUtilities.currentUser.getUserUID());

                    NotificationItemFormat postChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_POST,UserUtilities.currentUser.getUserUID());

                    postChatNotification.setItemMessage(text);
                    postChatNotification.setItemKey(getIntent().getStringExtra("key"));

                    postChatNotification.setUserImage(UserUtilities.currentUser.getImageURLThumbnail());
                    postChatNotification.setUserName(UserUtilities.currentUser.getUsername());
                    postChatNotification.setCommunityName(UserUtilities.CommunityName);

                    notificationSender.execute(postChatNotification);

                }else if(type.equals("messages")){
                    NotificationSender notificationSender=new NotificationSender(getIntent().getStringExtra("userKey"),userItem.getUserUID(),null,null,null,null,userItem.getUsername(), OtherKeyUtilities.KEY_MESSAGES_CHAT,false,true,ChatActivity.this);
                    notificationSender.execute();
                }else if(type.equals("events")){
                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, UserUtilities.currentUser.getUserUID());

                    NotificationItemFormat eventChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_EVENT,UserUtilities.currentUser.getUserUID());

                    eventChatNotification.setItemMessage(text);
                    eventChatNotification.setItemKey(getIntent().getStringExtra("key"));
                    eventChatNotification.setItemName(getIntent().getStringExtra("name"));

                    eventChatNotification.setUserImage(UserUtilities.currentUser.getImageURLThumbnail());
                    eventChatNotification.setUserName(UserUtilities.currentUser.getUsername());
                    eventChatNotification.setCommunityName(UserUtilities.CommunityName);

                    notificationSender.execute(eventChatNotification);
                }else if(type.equals("cabPool")){
                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, UserUtilities.currentUser.getUserUID());

                    NotificationItemFormat cabChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_CAB,UserUtilities.currentUser.getUserUID());

                    cabChatNotification.setItemMessage(text);
                    cabChatNotification.setItemKey(getIntent().getStringExtra("key"));

                    cabChatNotification.setUserImage(UserUtilities.currentUser.getImageURLThumbnail());
                    cabChatNotification.setUserName(UserUtilities.currentUser.getUsername());
                    cabChatNotification.setCommunityName(UserUtilities.CommunityName);

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
                                message.setMessage("Image");
                                message.setMessageType(MessageTypeUtilities.KEY_PHOTO_STR);

                                databaseReference.child("Chat").push().setValue(message);
                                if (type.equals("forums")){
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, UserUtilities.currentUser.getUserUID());

                                    NotificationItemFormat forumChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_FORUM,UserUtilities.currentUser.getUserUID());

                                    forumChatNotification.setItemMessage("Image");
                                    forumChatNotification.setItemCategoryUID(getIntent().getStringExtra("tab"));
                                    forumChatNotification.setItemName(getIntent().getStringExtra("name"));
                                    forumChatNotification.setItemKey(getIntent().getStringExtra("key"));

                                    forumChatNotification.setUserImage(UserUtilities.currentUser.getImageURLThumbnail());
                                    forumChatNotification.setUserName(UserUtilities.currentUser.getUsername());
                                    forumChatNotification.setCommunityName(UserUtilities.CommunityName);

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
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, UserUtilities.currentUser.getUserUID());

                                    NotificationItemFormat productChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_PRODUCT,UserUtilities.currentUser.getUserUID());

                                    productChatNotification.setItemMessage("Image");
                                    productChatNotification.setItemName(getIntent().getStringExtra("name"));
                                    productChatNotification.setItemKey(getIntent().getStringExtra("key"));

                                    productChatNotification.setUserImage(UserUtilities.currentUser.getImageURLThumbnail());
                                    productChatNotification.setUserName(UserUtilities.currentUser.getUsername());
                                    productChatNotification.setCommunityName(UserUtilities.CommunityName);

                                    notificationSender.execute(productChatNotification);

                                }else if(type.equals("post")){
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, UserUtilities.currentUser.getUserUID());

                                    NotificationItemFormat postChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_POST,UserUtilities.currentUser.getUserUID());

                                    postChatNotification.setItemMessage("Image");
                                    postChatNotification.setItemKey(getIntent().getStringExtra("key"));

                                    postChatNotification.setUserImage(UserUtilities.currentUser.getImageURLThumbnail());
                                    postChatNotification.setUserName(UserUtilities.currentUser.getUsername());
                                    postChatNotification.setCommunityName(UserUtilities.CommunityName);

                                    notificationSender.execute(postChatNotification);
                                }else if(type.equals("messages")){
                                    NotificationSender notificationSender=new NotificationSender(getIntent().getStringExtra("userKey"),userItem.getUserUID(),null,null,null,null,userItem.getUsername(), OtherKeyUtilities.KEY_MESSAGES_CHAT,false,true,ChatActivity.this);
                                    notificationSender.execute();
                                }else if(type.equals("events")){
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, UserUtilities.currentUser.getUserUID());

                                    NotificationItemFormat eventChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_EVENT,UserUtilities.currentUser.getUserUID());

                                    eventChatNotification.setItemMessage("Image");
                                    eventChatNotification.setItemKey(getIntent().getStringExtra("key"));
                                    eventChatNotification.setItemName(getIntent().getStringExtra("name"));

                                    eventChatNotification.setUserImage(UserUtilities.currentUser.getImageURLThumbnail());
                                    eventChatNotification.setUserName(UserUtilities.currentUser.getUsername());
                                    eventChatNotification.setCommunityName(UserUtilities.CommunityName);

                                    notificationSender.execute(eventChatNotification);
                                }else if(type.equals("cabPool")){
                                    NotificationSender notificationSender = new NotificationSender(ChatActivity.this, UserUtilities.currentUser.getUserUID());

                                    NotificationItemFormat cabChatNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_CHAT_CAB,UserUtilities.currentUser.getUserUID());

                                    cabChatNotification.setItemMessage("Image");
                                    cabChatNotification.setItemKey(getIntent().getStringExtra("key"));

                                    cabChatNotification.setUserImage(UserUtilities.currentUser.getImageURLThumbnail());
                                    cabChatNotification.setUserName(UserUtilities.currentUser.getUsername());
                                    cabChatNotification.setCommunityName(UserUtilities.CommunityName);

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
                        Snackbar.make(chatView, "Failed. Check Internet connectivity", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
        // chatView.scrollToPosition(chatView.getChildCount());
    }


    private void loadMessages() {
        databaseReference.child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {




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
                    key = getIntent().getStringExtra("key");
                    tab = getIntent().getStringExtra("tab");
                    name = getIntent().getStringExtra("name");

                    mydb.replaceForum(name,key,tab,messages.size());
                }

                adapter.notifyDataSetChanged();
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
        });
    }

    public void launchPeopleList(){
        Intent i = new Intent(this,ForumsPeopleList.class);
        i.putExtra("key",getIntent().getStringExtra("key"));
        i.putExtra("tab",getIntent().getStringExtra("tab"));
        i.putExtra("userType",userType);
        startActivity(i);
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
            launchPeopleList();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(!getIntent().getStringExtra("type").equals("forums")) {
            menu.findItem(R.id.action_list_people).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = intentHandle.getPickImageResultUri(data); //Get data
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
