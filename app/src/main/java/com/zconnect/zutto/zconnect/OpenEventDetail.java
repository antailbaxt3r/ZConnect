package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

public class OpenEventDetail extends AppCompatActivity {

    DatabaseReference mDatabase;
    TextView Event;
    ImageView EventImage;
    TextView EventDescription;
    TextView EventDate;
    Event currentEvent;
    Bitmap image;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_event_detail);
        initializeViews();
        extras = getIntent().getExtras();
        if (extras != null && extras.getString("Flag") != null && extras.getString("Flag").equals("true")) {
            setData();
        }
    }

    void initializeViews() {
        Event = (TextView) findViewById(R.id.event);
        EventImage = (ImageView) findViewById(R.id.EventImage);
        EventDescription = (TextView) findViewById(R.id.description);
        EventDate = (TextView) findViewById(R.id.date);
    }

    void setData() {
        image = (Bitmap) extras.get("currentImage");
        currentEvent = (com.zconnect.zutto.zconnect.Event) extras.getSerializable("currentEvent");
        Event.setText(currentEvent.getEventName());
        EventDescription.setText(currentEvent.getEventDescription());
        String eventDate[] = currentEvent.getEventDate().split("\\s+");
        String date = "";
        int i = 0;
        while (i < 3) {
            date = date + " / " + eventDate[i];
            i++;
        }
        EventDate.setText(date);
        EventImage.setImageBitmap(image);
        EventImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OpenEventDetail.this, viewImage.class);
                i.putExtra("currentEvent", currentEvent.getEventName());
                i.putExtra("eventImage", image);
                startActivity(i);
            }
        });
    }
}
