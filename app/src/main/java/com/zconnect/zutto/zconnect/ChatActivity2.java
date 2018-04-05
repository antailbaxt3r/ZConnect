package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.BaseActivity;
import com.zconnect.zutto.zconnect.ItemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.R;

import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity2 extends BaseActivity{

    //private String ref  = "Misc";
    private RecyclerView chatView;
    private RecyclerView.Adapter adapter;

    private Calendar calendar;
    private ArrayList<ChatItemFormats> messages  = new ArrayList<>();
    private FirebaseUser mauth = FirebaseAuth.getInstance().getCurrentUser();
    String myuid=mauth.getUid();
    SharedPreferences communitySP;
    String communityReference;
    private DatabaseReference databaseReference ;
    String recpuid,recpname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent i = getIntent();
        final String dene = (String) i.getSerializableExtra("s");
        communitySP = ChatActivity2.this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
        databaseReference= FirebaseDatabase.getInstance().getReference();
        recpuid=dene.substring(0,28);
        recpname=dene.substring(28,dene.length());
        //showToast(recpname);
        setToolbar();
        setToolbarTitle("Anonymous");
        showBackButton();
        setSupportActionBar(getToolbar());
        calendar = Calendar.getInstance();
        chatView = (RecyclerView) findViewById(R.id.chatList);
        adapter = new ChatRVAdapter(messages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);
        chatView.setLayoutManager(linearLayoutManager);
        chatView.setAdapter(adapter);
        findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText typer = ((EditText)findViewById(R.id.typer));
                String text = typer.getText().toString();
                if(TextUtils.isEmpty(text)){
                    showToast("Message is empty.");
                    return;
                }

                /*ChatItemFormats message = new ChatItemFormats();
                message.setTimeDate(calendar.getTimeInMillis());
                message.setUuid(mauth.getUid());
                message.setName(mauth.getDisplayName());
                message.setMessage("\""+text+"\"");*/

                String key=databaseReference.child("communities").child(communityReference).child("features").child("messages").child("chats").push().getKey();
                databaseReference.child("communities").child(communityReference).child("features").child("messages").child("chats").child(key).child("message").setValue("\""+text+"\"");
                databaseReference.child("communities").child(communityReference).child("features").child("messages").child("chats").child(key).child("sender").setValue(mauth.getUid());
                databaseReference.child("communities").child(communityReference).child("features").child("messages").child("chats").child(key).child("message").setValue(calendar.getTimeInMillis());
                typer.setText(null);
                chatView.scrollToPosition(chatView.getChildCount());
            }

        });
        loadMessages();
        /*if(getIntent()!=null && !TextUtils.isEmpty(getIntent().getStringExtra("ref"))) {
            ref = getIntent().getStringExtra("ref");
        }

        if(user==null) {
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

                ChatItemFormats message = new ChatItemFormats();
                message.setTimeDate(calendar.getTimeInMillis());
                message.setUuid(user.getUid());
                message.setName(user.getDisplayName());
                message.setMessage("\""+text+"\"");

                databaseReference.push().setValue(message);
                typer.setText(null);
                // chatView.scrollToPosition(chatView.getChildCount());

            }
        });

        loadMessages();*/
    }

    private void loadMessages() {
        final ChatItemFormats cif=new ChatItemFormats();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("messages");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int incount=0,last=0,now=1,change=0,success=0;
                messages.clear();
                for (DataSnapshot childsnapshot:dataSnapshot.child("users").child(myuid).child(recpuid).getChildren()){
                    String k=childsnapshot.getValue().toString();
                    cif.setMessage(dataSnapshot.child("chats").child(k).child("message").getValue().toString());
                    cif.setTimeDate((long)dataSnapshot.child("chats").child(k).child("timeStamp").getValue());
                    if(dataSnapshot.child("chats").child(k).child("sender").getValue().toString().equals(myuid))
                    {
                        cif.setName(mauth.getDisplayName());
                        cif.setUuid(myuid);
                        if(incount==0)
                        {
                            setToolbarTitle(recpname);
                            ++incount;
                            success=0;
                        }
                        else {
                            last = now;
                            now = 0;
                            if (last != now) {
                                ++change;
                            }
                        }
                    }
                    else
                    {
                        cif.setUuid(recpuid);
                        cif.setName("Anonymous");
                        if(incount==0)
                        {
                            ++incount;
                        }
                        if(success==1)
                        {
                            setToolbarTitle(recpname);
                            cif.setName(recpname);
                        }
                        else {
                            last = now;
                            now = 1;
                            if (last != now) {
                                ++change;
                            }
                            if (change >= 2) {
                                setToolbarTitle(recpname);
                                cif.setName(recpname);
                            }
                        }
                    }
                    messages.add(cif);
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
