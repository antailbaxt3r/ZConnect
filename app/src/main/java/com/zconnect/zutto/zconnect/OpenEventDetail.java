package com.zconnect.zutto.zconnect;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

public class OpenEventDetail extends AppCompatActivity {

    DatabaseReference mDatabase;
    TextView Event;
    ImageView EventImage;
    TextView EventDescription;
    TextView EventDate;
    String EDate;
    String EName;
    String EDesc;
    String EImage;
    Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_event_detail);
        Bundle extras = getIntent().getExtras();

        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);

        if (mActionBarToolbar != null) {
            mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
            getWindow().setStatusBarColor(colorDarkPrimary);
            getWindow().setNavigationBarColor(colorPrimary);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }


//      Event = (TextView) findViewById(R.id.event);
        EventImage = (ImageView) findViewById(R.id.EventImage);
        EventDescription = (TextView) findViewById(R.id.description);
        EventDate = (TextView) findViewById(R.id.date);

        EName = extras.getString("name");
        EDate = extras.getString("date");
        EDesc = extras.getString("desc");
        EImage = extras.getString("image");

        String eventDate[] = (EDate.split("\\s+"));

        String date = "";
        int i = 0;
        while (i < 3) {
            date = date + " " + eventDate[i];
            i++;
        }

        EventDate.setText(date);
        EventDescription.setText(EDesc);
        getSupportActionBar().setTitle(EName);
        Picasso.with(this).load(EImage).error(R.drawable.defaultevent).placeholder(R.drawable.defaultevent).into(EventImage);
        EventImage.getLayoutParams().height = LinearLayout.LayoutParams.WRAP_CONTENT;


//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Events/Posts/"+value);
//
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
////                String eventName = (String) snapshot.child("EventName").getValue();
//                String imageUri = (String) snapshot.child("EventImage").getValue();
//                String eventDescription = (String) snapshot.child("EventDescription").getValue();
//                String eventDate[] = ((String) snapshot.child("EventDate").getValue()).split("\\s+");
//                String date = "";
//                int i = 0;
//                while (i < 3) {
//                    date = date + " " + eventDate[i];
//                    i++;
//                }
//
//                Picasso.with(OpenEventDetail.this).load(imageUri).into(EventImage);
////                Event.setText(eventName);
//                EventDate.setText(date);
//                EventDescription.setText(eventDescription);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//            }
//        });

    }
}
