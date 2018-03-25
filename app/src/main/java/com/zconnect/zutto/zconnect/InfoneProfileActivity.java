package com.zconnect.zutto.zconnect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class InfoneProfileActivity extends AppCompatActivity {

    private TextView nametv;
    private TextView desc;
    ArrayList<String> phoneNums;
    RecyclerView phonerv;
    SimpleDraweeView profileImage;
    String infoneUserId;
    DatabaseReference databaseReferenceInfone;
    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infone_profile);

        nametv = (TextView) findViewById(R.id.tv_name_infone_profile);
        profileImage = (SimpleDraweeView) findViewById(R.id.image_profile_infone);
        phonerv = (RecyclerView) findViewById(R.id.rv_phone_infone_profile);


        infoneUserId = getIntent().getExtras().getString("infoneUserId");

        databaseReferenceInfone = FirebaseDatabase.getInstance().getReference().child("communities")
                .child("bitsGoa").child("infone").child("numbers").child(infoneUserId);

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name=dataSnapshot.child("name").getValue(String.class);
                nametv.setText(name);

                phoneNums=new ArrayList<>();
                for (DataSnapshot childSnapshot :
                        dataSnapshot.getChildren()) {
                    String phone=childSnapshot.getValue(String.class);
                    phoneNums.add(phone);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        databaseReferenceInfone.addValueEventListener(listener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        databaseReferenceInfone.removeEventListener(listener);
    }
}