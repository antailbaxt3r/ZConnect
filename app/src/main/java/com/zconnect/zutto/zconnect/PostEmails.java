package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;

public class PostEmails extends BaseActivity {
    public static String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_emails);
        final DatabaseReference mdb = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Phonebook");
        mdb.addValueEventListener(new ValueEventListener() {
            int i = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    i++;
                    if (snapshot.child("category").getValue().toString().equals("S")) {
                        String emailtemp = snapshot.child("uid").getValue().toString();
                        if (emailtemp != null)
                            PostEmails.email += (emailtemp).concat("\n");
                    }
                }
                if (i == dataSnapshot.getChildrenCount()) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "zconnectinc@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Emails");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, email);
                    startActivity(Intent.createChooser(emailIntent, "Send uid..."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

}
