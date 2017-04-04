package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostEmails extends AppCompatActivity {
    public static String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_emails);
        final DatabaseReference mdb = FirebaseDatabase.getInstance().getReference("Phonebook");
        mdb.addValueEventListener(new ValueEventListener() {
            int i = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    i++;
                    //Toast.makeText(getApplicationContext(),snapshot.child("email").getValue().toString(),Toast.LENGTH_SHORT).show();
                    if (snapshot.child("category").getValue().toString().equals("S")) {
                        String emailtemp = snapshot.child("email").getValue().toString();
                        if (emailtemp != null)
                            PostEmails.email += (emailtemp).concat("\n");
                    }
                }
                if (i == dataSnapshot.getChildrenCount()) {
                    //Toast.makeText(getApplicationContext(),PostEmails.email,Toast.LENGTH_LONG).show();
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "zconnectinc@gmail.com", null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Emails");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, email);
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

}
