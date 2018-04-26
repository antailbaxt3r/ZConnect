package com.zconnect.zutto.zconnect;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.UsersListItemFormat;

import java.util.Vector;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class ForumsPeopleList extends BaseActivity {

    private String key;
    private String name,number;
    private Boolean numberFlag,flag;
    private FirebaseAuth mAuth;
    private DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private DatabaseReference forumMembersList;
    private RecyclerView forumsPeopleRV;
    private ProgressBar progressBar;
    private Button joinLeaveButton;
    private UsersListRVAdapter adapter;
    private ValueEventListener valueEventListener;
    private View.OnClickListener onClickListener;
    Vector<UsersListItemFormat> usersListItemFormatVector = new Vector<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums_people_list);

        joinLeaveButton = (Button) findViewById(R.id.join);

        setToolbar();
        setToolbarTitle("List of people");
        showBackButton();
        setSupportActionBar(getToolbar());

        key = getIntent().getStringExtra("key");
        mAuth = FirebaseAuth.getInstance();

        Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
        forumMembersList = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).child("users");

        usersReference.addValueEventListener(new ValueEventListener() {
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

        forumsPeopleRV = (RecyclerView)findViewById(R.id.content_forum_prople_rv);
        progressBar = (ProgressBar)findViewById(R.id.content_forums_progress);

        progressBar.setVisibility(VISIBLE);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersListItemFormatVector.clear();
                flag = false;
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    UsersListItemFormat usersListItemFormat;
                    try {
                        usersListItemFormat = shot.getValue(UsersListItemFormat.class);
                        if (usersListItemFormat.getUserUID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            flag = true;
                        }
                        usersListItemFormatVector.add(usersListItemFormat);
                    }catch (Exception e){}

                }

                progressBar.setVisibility(INVISIBLE);
                adapter.notifyDataSetChanged();

                if (flag) {
                    joinLeaveButton.setText("Leave");
                } else {
                    joinLeaveButton.setText("Join");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(INVISIBLE);
            }
        };
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    forumMembersList.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
                    Toast.makeText(getApplicationContext(), "Removed", Toast.LENGTH_SHORT).show();

                } else {
                    FirebaseMessaging.getInstance().subscribeToTopic(key);
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
                            forumMembersList.child(userItemFormat.getUserUID()).setValue(userDetails);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        };

        adapter = new UsersListRVAdapter(this, usersListItemFormatVector);
        forumsPeopleRV.setHasFixedSize(true);
        forumsPeopleRV.setLayoutManager(new GridLayoutManager(getApplicationContext(), 1));
        forumsPeopleRV.setAdapter(adapter);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        forumMembersList.addValueEventListener(valueEventListener);
        joinLeaveButton.setOnClickListener(onClickListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        forumMembersList.removeEventListener(valueEventListener);
        joinLeaveButton.setOnClickListener(null);
    }
}
