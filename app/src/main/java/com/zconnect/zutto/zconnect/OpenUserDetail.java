package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zconnect.zutto.zconnect.adapters.UserDetailsJoinedForumsAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.ForumCategoriesItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.OtherKeyUtilities;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import mabbas007.tagsedittext.TagsEditText;

public class OpenUserDetail extends BaseActivity {
    private String TAG = OpenUserDetail.class.getSimpleName();
    String name, mobileNumber,whatsAppNumber, email, desc, imagelink ,skills ,category, Uid;
    Boolean contactHidden = false;
    private TextView editTextName;
    private TextView editTextEmail;
    private TextView editTextDetails;
    private TextView editTextNumber;

    TextView whatsAppNumberText;
    //private android.support.design.widget.TextInputEditText editTextSkills;
    private TagsEditText editTextSkills;
    private SimpleDraweeView image;
    private ImageView mail, call;
//    private EditText textMessage;
//    private LinearLayout anonymMessageLayout;
//    private ImageButton sendButton;
    private ImageButton btn_love,btn_like;
    private Boolean flagforNull=false;
    private TextView points_num, like_text, like_num, love_text, love_num;
    private boolean love_status = false,like_status=false;
    private FirebaseAuth mAuth;
    private UserItemFormat userProfile;
    private LinearLayout content;
    private ProgressBar progressBar;
    private Menu menu;
    private Toolbar toolbar;
    private Button userTypeText, requestContact;
    private RecyclerView forumsJoined;
    UserDetailsJoinedForumsAdapter adapter;
    private ArrayList<ForumCategoriesItemFormat> joinedForumsList = new ArrayList<>();

    private Button chatButton;
    String userImageURL;
    UsersListItemFormat userDetails = new UsersListItemFormat();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_user_detail);
        setToolbar();
        setSupportActionBar(toolbar);
        content = (LinearLayout) findViewById(R.id.phonebook_details_content);
        progressBar = (ProgressBar) findViewById(R.id.phonebook_details_progress_circle);
        progressBar.setVisibility(View.VISIBLE);
        content.setVisibility(View.INVISIBLE);
        image = (SimpleDraweeView) findViewById(R.id.contact_details_display_image);
        editTextDetails = (TextView) findViewById(R.id.contact_details_editText_1);
        editTextEmail = (TextView) findViewById(R.id.contact_details_email_editText);
        editTextName = (TextView) findViewById(R.id.contact_details_name_editText);
        editTextNumber = (TextView) findViewById(R.id.contact_details_number_editText);
        editTextSkills = (TagsEditText) findViewById(R.id.contact_details_editText_skills);
        whatsAppNumberText = (TextView) findViewById(R.id.whatsapp_number);
        forumsJoined = findViewById(R.id.joined_forums_rv);
        chatButton = findViewById(R.id.chat_button);

        btn_like = (ImageButton) findViewById(R.id.btn_like);
        btn_love = (ImageButton) findViewById(R.id.btn_love);
        //btn_love.setEnabled(false);
        //btn_like.setEnabled(false);
        points_num = (TextView) findViewById(R.id.point_num);
        like_text = (TextView) findViewById(R.id.like_text);
        like_num = (TextView) findViewById(R.id.like_num);
        love_text = (TextView) findViewById(R.id.love_text);
        love_num = (TextView) findViewById(R.id.love_num);

//        textMessage = (EditText) findViewById(R.id.textInput);
//        anonymMessageLayout = (LinearLayout) findViewById(R.id.anonymTextInput);
//        sendButton = (ImageButton) findViewById(R.id.send);

        call = (ImageView) findViewById(R.id.ib_call_contact_item);
        mail = (ImageView) findViewById(R.id.mailbutton);
        userTypeText = (Button) findViewById(R.id.user_type_content_phonebook_details);
        requestContact = (Button) findViewById(R.id.show_cum_request_contact_button);
        mAuth = FirebaseAuth.getInstance();
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
//            getWindow().setStatusBarColor(colorDarkPrimary);
//            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        Uid=getIntent().getStringExtra("Uid");
        adapter = new UserDetailsJoinedForumsAdapter(joinedForumsList);
        forumsJoined.setLayoutManager(new LinearLayoutManager(OpenUserDetail.this,LinearLayoutManager.HORIZONTAL,false));
        forumsJoined.setAdapter(adapter);

        final DatabaseReference userForums = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("userForums").child(Uid).child("joinedForums");

        userForums.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot shot2: dataSnapshot.getChildren()) {
                    try {
                        String name = null;
                        String imageURL = null;
                        Log.d("Try",shot2.toString());
                        ForumCategoriesItemFormat temp = new ForumCategoriesItemFormat();
                        try{
                            temp = shot2.getValue(ForumCategoriesItemFormat.class);
                        }
                        catch (Exception e){
                            Log.d("Try:ErrorFormat", e.toString());

                        }
                        if(temp==null) {
                            temp.setTabUID(shot2.child("tabUID").getValue().toString());
                            temp.setCatUID(shot2.child("catUID").getValue().toString());
                        }



                        if(shot2.child("tabUID").getValue().toString().equals("personalChats") || shot2.child("tabUID").getValue().toString().equals("others")|| shot2.child("tabUID").getValue().toString().equals("cabpools") || shot2.child("tabUID").getValue().toString().equals("events")){
                            continue;
                        }
                       joinedForumsList.add(temp);
                    }catch (Exception e){Log.e("Try:Outside Error",e.toString());}
                }




                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Like and Love data reader
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(Uid);
        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference currentUser = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(myUID);
        //Value fill listener

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    userProfile = dataSnapshot.getValue(UserItemFormat.class);
                    userDetails.setUserUID(userProfile.getUserUID());
                    userDetails.setName(userProfile.getUsername());
                    userDetails.setImageThumb(userProfile.getImageURLThumbnail());
                    userDetails.setUserType(ForumsUserTypeUtilities.KEY_ADMIN);
//                    forumsJoined.setText(dataSnapshot.getValue().toString());

                    setUserDetails(currentUser);

                    progressBar.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    if(userProfile.getUserUID().equals(myUID));
                    {
                        menu.findItem(R.id.action_edit_profile).setVisible(true);
                    }
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
            }
        });

        final DatabaseReference db_like = db.child("Likes");
        final DatabaseReference db_love = db.child("Loves");
        final DatabaseReference db_point = db.child("userPoints");
        if(db_point != null)
        {
            db_point.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        String points = dataSnapshot.getValue().toString();
                        points = points==null ? "0" : points;
                        points_num.setText(points);
                    }
                    else
                    {
                        points_num.setText("0");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
        {
            points_num.setText("0");
        }
        if(db_love != null){
            db_love.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long loves = dataSnapshot.getChildrenCount();
                    love_text.setText("Loves");
                    love_num.setText(String.valueOf(loves));
                    if (dataSnapshot.hasChild(myUID)){
                        //I already liked him
                        btn_love.setImageResource(R.drawable.heart_red);
                        love_status = true;
                    }else {
                        love_status= false;
                        btn_love.setImageResource(R.drawable.heart);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            //no one loves him
            love_text.setText("Loves");
            love_num.setText("0");
        }

        if(db_like != null){
            db_like.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long like = dataSnapshot.getChildrenCount();
                    like_text.setText("Likes");
                    like_num.setText(String.valueOf(like));
                    if (dataSnapshot.hasChild(myUID)){
                        //I already liked him
                        btn_like.setImageResource(R.drawable.like_blue);
                        like_status = true;
                    }else {
                        like_status = false;
                        btn_like.setImageResource(R.drawable.like);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else {
            //no one likes him
            like_text.setText("Likes");
            like_num.setText("0");
        }
        //seting onclickListener for togelling the likes and loves

        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_LIKE);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);
                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();
                if(like_status){
                    db_like.child(myUID).setValue(null);
                    btn_like.setImageResource(R.drawable.like);
                    like_status = false;
                }else {
                    db_like.child(myUID).setValue(true);
                    like_status = true;
                    btn_like.setImageResource(R.drawable.like_blue);

                    currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("Likes").hasChild(Uid)){
                                Toast.makeText(OpenUserDetail.this, "Congrats, now you both like each other, we recommend you to start a conversation", Toast.LENGTH_LONG).show();
                            }

                            UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                            NotificationSender notificationSender = new NotificationSender(OpenUserDetail.this, userItemFormat.getUserUID());

                            NotificationItemFormat infoneLikeNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_INFONE_LIKE,userItemFormat.getUserUID());
                            Log.d(userProfile.getUsername(), "editprolike");
                            Log.d(userItemFormat.getUsername(), "editprolike");
                            infoneLikeNotification.setItemKey(userProfile.getUserUID());
                            infoneLikeNotification.setUserImage(userItemFormat.getImageURLThumbnail());
                            infoneLikeNotification.setUserName(userItemFormat.getUsername());
                            infoneLikeNotification.setCommunityName(communityTitle);
                            GlobalFunctions.inAppNotifications("has liked your profile","Your profile is liked",userItemFormat,false,"status",null,userProfile.getUserUID());
                            notificationSender.execute(infoneLikeNotification);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        btn_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();
                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_LOVE);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);
                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();
                if(love_status) {
                    db_love.child(myUID).setValue(null);
                    love_status = false;
                } else{
                    db_love.child(myUID).setValue(true);
                    love_status = true;
                    final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                            .child(communityReference).child(ZConnectDetails.USERS_DB).child(mAuth.getCurrentUser().getUid());

                    currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("Loves").hasChild(Uid)) {
                                Toast.makeText(OpenUserDetail.this, "WOW, now you both love each other, we recommend you to start a conversation", Toast.LENGTH_LONG).show();

                                databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                                        if (!dataSnapshot1.child("userChats").hasChild(Uid)) {
                                            userImageURL = dataSnapshot1.child("imageURL").getValue().toString();
                                            Log.d("Try", createPersonalChat(mAuth.getCurrentUser().getUid(), Uid));
                                        }else {

                                            databaseReferenceUser.child("userChats").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                    String key = dataSnapshot2.getValue().toString();
                                                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                                    intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
                                                    intent.putExtra("type", "forums");
                                                    intent.putExtra("name", name);
                                                    intent.putExtra("tab", "personalChats");
                                                    intent.putExtra("key", key);
                                                    intent.putExtra("match",true);
                                                    startActivity(intent);

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

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        try {
            Log.e("msg", name);

            Log.e("msg", desc);
            Log.e("msg", mobileNumber);
            Log.e("msg", imagelink);
            Log.e("msg", email);
            Log.e("msg", skills);
            Log.e("msg", category);
            Log.e("msg", Uid);
        }catch (Exception e){

        }

        if (Uid.equals("null"))
        {
//            anonymMessageLayout.setVisibility(View.GONE);
            flagforNull=true;
        }



//        sendButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//            String textMessageString;
//            Calendar calendar;
//            calendar = Calendar.getInstance();
//            textMessageString = textMessage.getText().toString();
//            if (textMessageString!=null && !flagforNull){
//                DatabaseReference UsersReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("messages");
//                String s = UsersReference.child("chats").push().getKey();
//
//                DatabaseReference chatReference = UsersReference.child("chats").child(s).push();
//                chatReference.child("message").setValue("\""+textMessageString+"\"");
//                chatReference.child("sender").setValue(mAuth.getCurrentUser().getUid());
//                chatReference.child("senderName").setValue("Anonymous");
//                chatReference.child("timeStamp").setValue(calendar.getTimeInMillis());
//
//                UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("name").setValue(name);
//                UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("message").setValue(textMessageString);
//                UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("type").setValue("sent");
//                UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("chatUID").setValue(s);
//                UsersReference.child("users").child(mAuth.getCurrentUser().getUid()).child("chats").child(s).child("sender").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//                UsersReference.child("users").child(Uid).child("messages").child(s).child("message").setValue(textMessageString);
//                UsersReference.child("users").child(Uid).child("messages").child(s).child("sender").setValue(mAuth.getCurrentUser().getUid());
//                UsersReference.child("users").child(Uid).child("messages").child(s).child("type").setValue("recieved");
//                UsersReference.child("users").child(Uid).child("messages").child(s).child("chatUID").setValue(s);
//                UsersReference.child("users").child(Uid).child("messages").child(s).child("timeStamp").setValue(calendar.getTimeInMillis());
//
//                FirebaseMessaging.getInstance().subscribeToTopic(s);
//
//
//                textMessage.setText(null);
//                Toast.makeText(OpenUserDetail.this, "Encrypted message sent", Toast.LENGTH_SHORT).show();
//
//            }
//            }
//        });

        //changing fonts
        Typeface ralewayRegular = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Regular.ttf");
        Typeface ralewaySemiBold = Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");
        editTextName.setTypeface(ralewaySemiBold);
        editTextDetails.setTypeface(ralewayRegular);
        editTextNumber.setTypeface(ralewayRegular);
        whatsAppNumberText.setTypeface(ralewayRegular);
        editTextSkills.setTypeface(ralewayRegular);
        editTextEmail.setTypeface(ralewayRegular);

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Try", "clicked");
                final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                        .child(communityReference).child(ZConnectDetails.USERS_DB).child(mAuth.getCurrentUser().getUid());

                if (databaseReferenceUser == null) {
                    Toast.makeText(v.getContext(), "The user does not exist!", Toast.LENGTH_SHORT).show();
                    return;
                }
                databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.child("userChats").hasChild(Uid)) {
                            userImageURL = dataSnapshot.child("imageURL").getValue().toString();
                            Log.d("Try", createPersonalChat(mAuth.getCurrentUser().getUid(), Uid));
                        }else {
                            databaseReferenceUser.child("userChats").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                    String key = dataSnapshot1.getValue().toString();
                                    Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                                    intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
                                    intent.putExtra("type", "forums");
                                    intent.putExtra("name", name);
                                    intent.putExtra("tab", "personalChats");
                                    intent.putExtra("key", key);
                                    startActivity(intent);

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
        });

    }

    private String createPersonalChat(final String senderUID, final String receiverUserUUID) {
        final DatabaseReference databaseReferenceCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories");
        final DatabaseReference databaseReferenceTabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child("personalChats");

        final DatabaseReference databaseReferenceReceiver = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(receiverUserUUID);
        final DatabaseReference databaseReferenceSender = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(senderUID);

        final DatabaseReference newPush = databaseReferenceCategories.push();


        newPush.child("name").setValue(false);
        Long postTimeMillis = System.currentTimeMillis();
        newPush.child("PostTimeMillis").setValue(postTimeMillis);
        newPush.child("UID").setValue(newPush.getKey());
        newPush.child("tab").setValue("personalChats");
        newPush.child("Chat");


        databaseReferenceReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);

                UsersListItemFormat userDetails = new UsersListItemFormat();

                userDetails.setImageThumb(userItem.getImageURLThumbnail());

                userDetails.setName(userItem.getUsername());
                userDetails.setPhonenumber(userItem.getMobileNumber());
                userDetails.setUserUID(userItem.getUserUID());
                userDetails.setUserType(ForumsUserTypeUtilities.KEY_ADMIN);


                HashMap<String,UsersListItemFormat> userList = new HashMap<String,UsersListItemFormat>();
                userList.put(receiverUserUUID,userDetails);

                databaseReferenceSender.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        UserItemFormat temp = dataSnapshot1.getValue(UserItemFormat.class);


                        UsersListItemFormat currentUser = new UsersListItemFormat();
                        currentUser.setImageThumb(temp.getImageURLThumbnail());
                        currentUser.setName(temp.getUsername());
                        currentUser.setPhonenumber(temp.getMobileNumber());
                        currentUser.setUserUID(temp.getUserUID());
                        currentUser.setUserType(temp.getUserType());
                        userList.put(senderUID,currentUser);
                        databaseReferenceTabsCategories.child(newPush.getKey()).child("users").setValue(userList);

                        HashMap<String,Object> forumTabs = new HashMap<>();
                        forumTabs.put("name",false);
                        forumTabs.put("catUID",newPush.getKey());
                        forumTabs.put("tabUID","personalChats");
                        forumTabs.put("lastMessage","Null");
                        forumTabs.put("users",userList);
                        databaseReferenceTabsCategories.child(newPush.getKey()).setValue(forumTabs);


                        databaseReferenceSender.child("userChats").child(receiverUserUUID).setValue(newPush.getKey());
                        databaseReferenceReceiver.child("userChats").child(senderUID).setValue(newPush.getKey());

                        String key = newPush.getKey();
                        Intent intent = new Intent(OpenUserDetail.this, ChatActivity.class);
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
                        intent.putExtra("type", "forums");
                        intent.putExtra("name", userDetails.getName());
                        intent.putExtra("tab", "personalChats");
                        intent.putExtra("key", key);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });





        return newPush.getKey();

    }
    public void setUserDetails(final DatabaseReference currentUser){
        //        name = getIntent().getStringExtra("name");
//        desc = getIntent().getStringExtra("desc");
//        mobileNumber = getIntent().getStringExtra("contactDescTv");
//        imagelink = getIntent().getStringExtra("image");
//        email = getIntent().getStringExtra("uid");
//        skills=getIntent().getStringExtra("skills");
//        category=getIntent().getStringExtra("category");
        name = userProfile.getUsername();
        desc = userProfile.getAbout();
        mobileNumber = userProfile.getMobileNumber();
        whatsAppNumber = userProfile.getWhatsAppNumber();
        imagelink = userProfile.getImageURL();
        email = userProfile.getEmail();
        skills = userProfile.getSkillTags();
        requestContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SHOTGUN", "RISE");
                requestCallFunction(userProfile.getUserUID(), currentUser);
                String shortname = name;
                if(name.indexOf(' ')>0)
                    shortname = name.substring(0, name.indexOf(' '));
                Toast.makeText(getApplicationContext(), "Request sent. " + shortname + " will contact you back.", Toast.LENGTH_SHORT).show();
            }
        });
        if(userProfile.getContactHidden()!=null){
            contactHidden = userProfile.getContactHidden();
        }

        if(skills==null)
            skills="";

        if (!skills.equals("") || skills.indexOf(',') > 0) {
            editTextSkills.setVisibility(View.VISIBLE);
            String[] skillsArray = skills.split(",");
            skillsArray[0] = skillsArray[0].substring(1);
            skillsArray[skillsArray.length - 1] = skillsArray[skillsArray.length - 1]
                    .substring(0, skillsArray[skillsArray.length - 1].length() - 1);
            editTextSkills.setTags(skillsArray);
            editTextSkills.setClickable(false);
            editTextSkills.setEnabled(false);

        } else {
            editTextSkills.setVisibility(View.GONE);
        }

        if (name != null) {
            editTextName.setText(name);
        }
        if (desc != null) {
            editTextDetails.setText(desc);
        }else{
            editTextDetails.setVisibility(View.GONE);
        }
        if (mobileNumber != null) {
            editTextNumber.setText(mobileNumber);
            editTextNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber)));
                }
            });
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber)));
                }
            });
        }
        if(whatsAppNumber != null) {
            whatsAppNumberText.setText(whatsAppNumber);
        }

        if(contactHidden){
            //Define if contact hidden here also link the request call function with the button
            requestContact.setVisibility(View.VISIBLE);
            call.setVisibility(View.GONE);
            editTextNumber.setText("******" + mobileNumber.substring(6));
            whatsAppNumberText.setText("******" + whatsAppNumber.substring(6));
            requestContact.setText(getApplicationContext().getResources().getString(R.string.request_contact));
            editTextNumber.setOnClickListener(null);
            call.setOnClickListener(null);
        }
        else {
            editTextNumber.setText(mobileNumber);
            whatsAppNumberText.setText(whatsAppNumber);
            requestContact.setVisibility(View.GONE);
            call.setVisibility(View.VISIBLE);
            editTextNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber)));
                }
            });
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta= new HashMap<>();
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_CALL);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);
                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobileNumber)));
                }
            });
        }

        if (imagelink != null) {
            if (imagelink.equals("https://firebasestorage.googleapis.com/v0/b/zconnect-89fbd.appspot.com/o/PhonebookImage%2FdefaultprofilePhone.png?alt=media&token=5f814762-16dc-4dfb-ba7d-bcff0de7a336")) {

                image.setBackgroundResource(R.drawable.ic_profile_icon);

            } else {

                image.setImageURI((Uri.parse(imagelink)));

            }

        }
        if (email != null) {
            mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CounterItemFormat counterItemFormat = new CounterItemFormat();
                    HashMap<String, String> meta= new HashMap<>();
                    counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                    counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_EMAIL);
                    counterItemFormat.setTimestamp(System.currentTimeMillis());
                    counterItemFormat.setMeta(meta);
                    CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                    counterPush.pushValues();

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                    startActivity(Intent.createChooser(emailIntent, "Send Email ..."));
                }
            });


            //image.setImageURI((Uri.parse(imagelink)));
            editTextEmail.setText(email);
            editTextEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
                    startActivity(Intent.createChooser(emailIntent, "Send Email ..."));
                }
            });
        }

        if(userProfile.getUserType()!=null)
        {
            if(userProfile.getUserType().equals(UsersTypeUtilities.KEY_ADMIN)){
                userTypeText.setText("Admin");
            }else if(userProfile.getUserType().equals(UsersTypeUtilities.KEY_VERIFIED)){
                userTypeText.setText("Verfied Member");
            }else if(userProfile.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED)) {
                userTypeText.setText("Not Verified, Verify Now");
                userTypeText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userProfile.getUserUID())) {
                            Intent i = new Intent(getApplicationContext(), VerificationPage.class);
                            startActivity(i);
                        }
                    }
                });
            }
        }
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phonebook_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_report) {
            CounterItemFormat counterItemFormat = new CounterItemFormat();
            HashMap<String, String> meta= new HashMap<>();
            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
            counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_REPORT);
            counterItemFormat.setTimestamp(System.currentTimeMillis());
            counterItemFormat.setMeta(meta);
            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
            counterPush.pushValues();

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "zconnectinc@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Problem with the content displayed");
            // emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send uid..."));

            return true;
        }else if (id==R.id.menu_share_conatct) {

            CounterItemFormat counterItemFormat = new CounterItemFormat();
            HashMap<String, String> meta= new HashMap<>();
            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
            counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_SHARE);
            counterItemFormat.setTimestamp(System.currentTimeMillis());
            counterItemFormat.setMeta(meta);
            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
            counterPush.pushValues();

            String send = "";
            String format1 = "%1$-20s %2$-20s\n";
            send = "Name: " + name + "\n"+ "Number: " + mobileNumber + "\n \nShared using ZConnect. \nDownlaod ZConnect now, to access all contacts of your community"+ "\n \nhttps://play.google.com/store/apps/details?id=com.zconnect.zutto.zconnect";
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/*");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, send);
            startActivity(sharingIntent);

        }
        else if(id==R.id.action_edit_profile) {

            CounterItemFormat counterItemFormat = new CounterItemFormat();
            HashMap<String, String> meta= new HashMap<>();
            counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
            counterItemFormat.setUniqueID(CounterUtilities.KEY_PROFILE_EDIT_OPEN);
            counterItemFormat.setTimestamp(System.currentTimeMillis());
            counterItemFormat.setMeta(meta);
            CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
            counterPush.pushValues();

            Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
            intent.putExtra("newUser",false);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestCallFunction(final String itemUID, DatabaseReference currentUser){

            currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                    NotificationSender notificationSender = new NotificationSender(OpenUserDetail.this,userItemFormat.getUserUID());

                    NotificationItemFormat requestCallNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_REQUEST_CALL,userItemFormat.getUserUID());
                    requestCallNotification.setItemKey(itemUID);

                    requestCallNotification.setUserMobileNumber(userItemFormat.getMobileNumber());
                    requestCallNotification.setUserImage(userItemFormat.getImageURLThumbnail());
                    requestCallNotification.setUserName(userItemFormat.getUsername());
                    requestCallNotification.setCommunityName(communityTitle);

                    GlobalFunctions.inAppNotifications("tried contacting you"," call him back!",userItemFormat,false,"requestcallback",null,Uid);
                    notificationSender.execute(requestCallNotification);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

}

