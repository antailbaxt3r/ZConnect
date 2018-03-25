package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InfoneProfileActivity extends AppCompatActivity {

    /*UI elements*/
    private TextView nametv;
    //private TextView desc;
    TextView phone1tv;
    TextView phone2tv;
    SimpleDraweeView profileImage;

    ArrayList<String> phoneNums;
    /*user id of the current infone contact in /infone/numbers */
    String infoneUserId;

    /*DB elements*/
    DatabaseReference databaseReferenceInfone;
    ValueEventListener listener;

    /*to get current community*/
    private SharedPreferences communitySP;
    public String communityReference;

    /*db elements needed for views calculation*/
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ValueEventListener listenerView;
    private DatabaseReference mDatabaseViews;
    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infone_profile);

        nametv = (TextView) findViewById(R.id.tv_name_infone_profile);
        profileImage = (SimpleDraweeView) findViewById(R.id.image_profile_infone);
        phone1tv = (TextView) findViewById(R.id.tv_phone1_infone_profile);
        phone2tv = (TextView) findViewById(R.id.tv_phone2_infone_profile);

        infoneUserId = getIntent().getExtras().getString("infoneUserId");

        Log.e(InfoneProfileActivity.class.getName(), "data :" + infoneUserId);

        communitySP = this.getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        databaseReferenceInfone = FirebaseDatabase.getInstance().getReference().child("communities")
                .child(communityReference).child("infone").child("numbers").child(infoneUserId);
        mDatabaseViews = FirebaseDatabase.getInstance().getReference().child("communities")
                .child(communityReference).child("infone").child("numbers").child(infoneUserId).child("views");


        Log.e(InfoneProfileActivity.class.getName(), "data :" + communityReference);

        updateViews();
        Log.e(TAG, "inside" + infoneUserId);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue(String.class);
                nametv.setText(name);

                String imageThumb = dataSnapshot.child("thumbnail").getValue(String.class);
                String imageUrl = dataSnapshot.child("imageurl").getValue(String.class);

                //setting image if not default
                if (imageUrl != null && !imageUrl.equalsIgnoreCase("default")) {
                    Uri imageUri = Uri.parse(imageUrl);
                    profileImage.setImageURI(imageUri);
                }

                phoneNums = new ArrayList<>();
                DataSnapshot dataSnapshot1 = dataSnapshot.child("phone");
                for (DataSnapshot childSnapshot :
                        dataSnapshot1.getChildren()) {
                    String phone = childSnapshot.getValue(String.class);
                    phoneNums.add(phone);
                }

                phone1tv.setText(phoneNums.get(0));
                phone2tv.setText(phoneNums.get(1));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Database error :" + databaseError.toString());
            }
        };
        databaseReferenceInfone.addValueEventListener(listener);

        phone1tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phone1tv.getText().toString().isEmpty())
                    makeCall(phone1tv.getText().toString());
            }
        });
        phone2tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phone2tv.getText().toString().isEmpty())
                    makeCall(phone2tv.getText().toString());
            }
        });

    }

    private void makeCall(String number) {

        String strName = number;


        // to make a call at number
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + strName));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider cal
            // ling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);

        Toast.makeText(this, "call being made to " + strName, Toast.LENGTH_SHORT).show();

    }

    //this function will update the views of people who have visited this activity
    private void updateViews() {

        SharedPreferences sharedPref = this.getSharedPreferences("guestMode", MODE_PRIVATE);
        Boolean status = sharedPref.getBoolean("mode", false);

        if (!status) {
            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();

            listenerView = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    boolean userExists = false;
                    for (DataSnapshot childSnapshot :
                            dataSnapshot.getChildren()) {
                        if (childSnapshot.getKey().equals(user.getUid()) && childSnapshot.exists() &&
                                childSnapshot.getValue(Integer.class) != null) {
                            userExists = true;
                            int originalViews = childSnapshot.getValue(Integer.class);
                            mDatabaseViews.child(user.getUid()).setValue(originalViews + 1);
                            Log.e(TAG, "inside" + originalViews);
                            break;
                        } else {
                            userExists = false;
                        }
                    }
                    if (!userExists) {
                        mDatabaseViews.child(user.getUid()).setValue(1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "Database error :" + databaseError.toString());
                }
            };

            mDatabaseViews.addListenerForSingleValueEvent(listenerView);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReferenceInfone.removeEventListener(listener);
        mDatabaseViews.removeEventListener(listenerView);
    }
}