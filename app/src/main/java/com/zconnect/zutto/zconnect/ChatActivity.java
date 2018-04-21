package com.zconnect.zutto.zconnect;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zconnect.zutto.zconnect.ItemFormats.CabListItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.ChatItemFormats;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;

import java.util.ArrayList;
import java.util.Calendar;

import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;

public class ChatActivity extends BaseActivity {

    private String ref  = "Misc", refToCatInTabCategories = "";
    private RecyclerView chatView;
    private RecyclerView.Adapter adapter;
    private DatabaseReference databaseReference ;
    private Calendar calendar;
    private ArrayList<ChatItemFormats> messages  = new ArrayList<>();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String type=null;
    private Button joinButton;
    private LinearLayout joinLayout,chatLayout;

    private DatabaseReference mUserReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
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
            if(!TextUtils.isEmpty(getIntent().getStringExtra("ref_to_cat_in_tabCategories"))){
                refToCatInTabCategories = getIntent().getStringExtra("ref_to_cat_in_tabCategories");
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

        if(type!=null){
            if(type.equals("cabPool")){
                databaseReference.child("cabListItemFormats").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            joinLayout.setVisibility(View.VISIBLE);
                            chatLayout.setVisibility(View.GONE);

                            joinButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final CabListItemFormat userDetails = new CabListItemFormat();
                                    DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    user.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);
                                            userDetails.setImageThumb(userItemFormat.getImageURLThumbnail());
                                            userDetails.setName(userItemFormat.getUsername());
                                            userDetails.setPhonenumber(userItemFormat.getMobileNumber());
                                            userDetails.setUserUID(userItemFormat.getUserUID());
                                            databaseReference.child("cabListItemFormats").child(userItemFormat.getUserUID()).setValue(userDetails);
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

            }
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
                        Log.d("USER IIII", userItem.getUsername());
                        Log.d("USER IIII", userItem.getUserUID());
                        Log.d("USER IIII", userItem.getImageURLThumbnail());
                        message.setUuid(userItem.getUserUID());
                        message.setName(userItem.getUsername());
                        message.setImageThumb(userItem.getImageURLThumbnail());
                        message.setMessage("\""+text+"\"");
                        databaseReference.child("Chat").push().setValue(message);
                        Toast.makeText(ChatActivity.this, refToCatInTabCategories, Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReferenceFromUrl(refToCatInTabCategories).child("lastMessage").setValue(message);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showSnack("Unable to load messages");
            }
        });
    }
}
