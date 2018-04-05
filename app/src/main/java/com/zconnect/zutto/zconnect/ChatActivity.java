package com.zconnect.zutto.zconnect;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;

import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends BaseActivity {

    private String ref  = "Misc";
    private RecyclerView chatView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference databaseReference ;
    private Calendar calendar;
    private ArrayList<ChatItemFormats> messages  = new ArrayList<>();

    private DatabaseReference mUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        SharedPreferences communitySP;
        String communityReference;
        communitySP = ChatActivity.this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
        mUserReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(getIntent()!=null && !TextUtils.isEmpty(getIntent().getStringExtra("ref"))) {
            ref = getIntent().getStringExtra("ref");
        }

        if(FirebaseAuth.getInstance().getCurrentUser().getUid()==null) {
            showToast("You have to be logged in to chat");
            finish();
        }
        calendar = Calendar.getInstance();
        chatView = (RecyclerView) findViewById(R.id.chatList);
        adapter = new ChatRVAdapter(messages);
        setToolbar();
        setToolbarTitle("Chat");
        showBackButton();
        setSupportActionBar(getToolbar());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);
        chatView.setLayoutManager(linearLayoutManager);
        chatView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(ref).child("Chat");

        findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText typer = ((EditText)findViewById(R.id.typer));
                String text = typer.getText().toString();
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                message.setMessage("\""+text+"\"");

                databaseReference.push().setValue(message);
                typer.setText(null);
               // chatView.scrollToPosition(chatView.getChildCount());

            }
        });

        loadMessages();
    }

    private void loadMessages() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                messages.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    messages.add(snapshot.getValue(ChatItemFormats.class));
                }
                adapter.notifyDataSetChanged();
                chatView.scrollToPosition(messages.size()-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showSnack("Unable to load messages");
            }
        });
    }
}
