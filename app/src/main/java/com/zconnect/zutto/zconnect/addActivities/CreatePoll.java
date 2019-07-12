package com.zconnect.zutto.zconnect.addActivities;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.RecentTypeUtilities;

import java.util.HashMap;

public class CreatePoll extends BaseActivity {
    EditText pollQuestion;
    EditText pollOptionA;
    EditText pollOptionB;
    EditText pollOptionC;
    Button createPoll;

    DatabaseReference postedByDetails;
    DatabaseReference home;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setOverflowIcon(ContextCompat.getDrawable(this, R.drawable.ic_more_vert_black_24dp));
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black));

        progressDialog = new ProgressDialog(this);
        setActionBarTitle("Create a Poll");
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

        postedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        home = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");

        pollQuestion = (EditText) findViewById(R.id.poll_add_question);
        pollOptionA = (EditText) findViewById(R.id.poll_add_optionA);
        pollOptionB = (EditText) findViewById(R.id.poll_add_optionB);
        pollOptionC = (EditText) findViewById(R.id.poll_add_optionC);
        createPoll = (Button) findViewById(R.id.createPollButton);

        createPoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((pollQuestion.getText().toString().trim().length()==0)||(pollOptionA.getText().toString().trim().length()==0)||((pollOptionB.getText().toString().trim().length()==0))||((pollOptionC.getText().toString().trim().length()==0)))
                {
                    Snackbar snackbar = Snackbar.make(view, "Fields are empty", Snackbar.LENGTH_SHORT);
                    snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                    snackbar.show();
                }
                else
                {
                    progressDialog.setMessage("Posting Poll...");
                    progressDialog.show();

                    final DatabaseReference newPoll = home.push();

                    final DatabaseReference homePosts = postedByDetails.child("homePosts");
                    final String key = newPoll.getKey();
                    homePosts.child(key).setValue(true);

                    final HashMap<String, Object> createPollMap = new HashMap<>();

                    createPollMap.put("Key",key);
                    createPollMap.put("question",pollQuestion.getText().toString().trim());
                    createPollMap.put("feature","createPoll");
                    createPollMap.put("recentType",RecentTypeUtilities.KEY_RECENT_NORMAL_POST_STR);
                    createPollMap.put("PostTimeMillis",System.currentTimeMillis());

                    final HashMap<String, Object> optionsMap = new HashMap<>();
                    optionsMap.put("optionA",pollOptionA.getText().toString().trim());
                    optionsMap.put("optionACount",0);
                    optionsMap.put("optionB",pollOptionB.getText().toString().trim());
                    optionsMap.put("optionBCount",0);
                    optionsMap.put("optionC",pollOptionC.getText().toString().trim());
                    optionsMap.put("optionCCount",0);

                    createPollMap.put("options",optionsMap);
                    createPollMap.put("totalCount",0);

                    /*newPoll.child("Key").setValue(key);
                    newPoll.child("Question").setValue(pollQuestion.getText().toString().trim());
                    newPoll.child("Options").child("OptionA").setValue(pollOptionA.getText().toString().trim());
                    newPoll.child("Options").child("OptionB").setValue(pollOptionB.getText().toString().trim());
                    newPoll.child("Options").child("OptionC").setValue(pollOptionC.getText().toString().trim());
                    newPoll.child("feature").setValue("CreatePoll");
                    newPoll.child("recentType").setValue(RecentTypeUtilities.KEY_RECENT_NORMAL_POST_STR);
                    newPoll.child("PostTimeMillis").setValue(System.currentTimeMillis());*/

                    postedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserItemFormat user = dataSnapshot.getValue(UserItemFormat.class);

                            final HashMap<String, Object> postedByDetails = new HashMap<>();
                            postedByDetails.put("Username",user.getUsername());
                            postedByDetails.put("UID",user.getUserUID());
                            postedByDetails.put("ImageThumb",user.getImageURLThumbnail());

                            createPollMap.put("PostedBy",postedByDetails);

                            newPoll.setValue(createPollMap);

                            /*newPoll.child("PostedBy").child("Username").setValue(user.getUsername());
                            newPoll.child("PostedBy").child("UID").setValue(user.getUserUID());
                            newPoll.child("PostedBy").child("ImageThumb").setValue(user.getImageURLThumbnail());*/
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    progressDialog.dismiss();
                    finish();
                }
            }
        });

    }
}
