package com.zconnect.zutto.zconnect.addActivities;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.utilities.RequestTypeUtilities;

import java.util.HashMap;

public class RequestForumTab extends BaseActivity {

    private MaterialEditText inputTabName;
    private FrameLayout doneLayout;
    private DatabaseReference requestForumTabs;
    private DatabaseReference mPostedByDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_forum_tab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_app_bar_home);
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

        inputTabName = findViewById(R.id.input_tab_name_content_add_forum_tab);
        doneLayout = findViewById(R.id.layout_done_content_add_forum_tab);

        requestForumTabs = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features/admin/requests");
        mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        doneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputTabName.getText().toString().length()>0)
                {
                    final DatabaseReference newPush=requestForumTabs.push();

                    final HashMap<String, Object> requestMap = new HashMap<>();
                    requestMap.put("Type",RequestTypeUtilities.TYPE_FORUM_TAB);
                    requestMap.put("key",newPush.getKey());
                    requestMap.put("Name",inputTabName.getText().toString());
                    requestMap.put("PostTimeMillis",System.currentTimeMillis());

                    /*newPush.child("Type").setValue(RequestTypeUtilities.TYPE_FORUM_TAB);
                    newPush.child("key").setValue(newPush.getKey());
                    newPush.child("Name").setValue(inputTabName.getText().toString());
                    Long postTimeMillis = System.currentTimeMillis();
                    newPush.child("PostTimeMillis").setValue(postTimeMillis);*/

                    mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            /*newPush.child("PostedBy").child("Username").setValue(dataSnapshot.child("username").getValue().toString());
                            //needs to be changed after image thumbnail is put
                            newPush.child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child("imageURLThumbnail").getValue().toString());
                            newPush.child("PostedBy").child("UID").setValue(dataSnapshot.child("userUID").getValue().toString());*/
                            final HashMap<String,Object> postedBy = new HashMap<>();
                            postedBy.put("Username",dataSnapshot.child("username").getValue().toString());
                            postedBy.put("ImageThumb",dataSnapshot.child("imageURLThumbnail").getValue().toString());
                            postedBy.put("UID",dataSnapshot.child("userUID").getValue().toString());

                            requestMap.put("PostedBy",postedBy);
                            newPush.setValue(requestMap);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    /*Log.i("chasing parties", "sending to db!");
                    String tabName = inputTabName.getText().toString();
                    String arr[] = tabName.split(" ");
                    String tabUID = "";
                    for(int i=0; i<arr.length; i++)
                    {
                        if(i==arr.length-1)
                        {
                            String str2 = arr[i];
                            str2 = str2.toLowerCase();
                            str2 = Character.toUpperCase(str2.charAt(0)) + str2.substring(1);
                            tabUID+=str2;
                        }
                        else
                        {
                            tabUID+=arr[i].toLowerCase();
                        }
                    }
                    mForumTabs.child(tabUID).child("name").setValue(tabName);
                    mForumTabs.child(tabUID).child("UID").setValue(tabUID);*/
                    finish();
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(inputTabName, "Tab name cannot be empty", Snackbar.LENGTH_SHORT);
                    snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                    snackbar.show();
                }
            }
        });
    }

}
