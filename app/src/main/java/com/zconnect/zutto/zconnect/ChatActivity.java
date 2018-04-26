package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zconnect.zutto.zconnect.ItemFormats.UsersListItemFormat;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zconnect.zutto.zconnect.ItemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;

import java.util.ArrayList;
import java.util.Calendar;

import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;

public class ChatActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setToolbar();
        showBackButton();

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
                menu.findItem(R.id.action_list_people).setVisible(false);
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
                joinButton.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.forums));
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
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
                                            databaseReference.child("users").child(userItemFormat.getUserUID()).setValue(userDetails);
                                            NotificationSender notificationSender=new NotificationSender(getIntent().getStringExtra("key"),FirebaseAuth.getInstance().getCurrentUser().getUid(),null,null,null,null,FirebaseAuth.getInstance().getCurrentUser().getUid(),KeyHelper.KEY_FORUMS_JOIN,false,true,ChatActivity.this);
                                            notificationSender.execute();
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

                MaterialEditText typer = ((MaterialEditText)findViewById(R.id.typer));
                final String text = typer.getText().toString();
                if(TextUtils.isEmpty(text)){
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
                        databaseReference.child("Chat").push().setValue(message);
                        if (type.equals("forums")){
                            NotificationSender notificationSender=new NotificationSender(getIntent().getStringExtra("key"),FirebaseAuth.getInstance().getCurrentUser().getUid(),null,null,null,null,userItem.getUsername(),KeyHelper.KEY_FORUMS,false,true,ChatActivity.this);
                            notificationSender.execute();
                        }
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(dataSnapshot.child("tab").getValue().toString()).child(getIntent().getStringExtra("key")).child("lastMessage").setValue(message);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                typer.setText(null);
               // chatView.scrollToPosition(chatView.getChildCount());
            }
        });
        loadMessages();
    }

    private void loadMessages() {
        databaseReference.child("Chat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                messages.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    messages.add(snapshot.getValue(ChatItemFormats.class));
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


        return super.onPrepareOptionsMenu(menu);
    }

}
