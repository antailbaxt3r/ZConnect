package com.zconnect.zutto.zconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zconnect.zutto.zconnect.ItemFormats.Event;

public class OpenEventDetail extends AppCompatActivity {

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
        extras = getIntent().getExtras();
        ProgressDialog mProgress = new ProgressDialog(this);
        if (extras != null && extras.getString("Flag") != null && extras.getString("Flag").equals("true")) {
            currentEvent = (com.zconnect.zutto.zconnect.ItemFormats.Event) extras.getSerializable("currentEvent");
            initializeViews();
            mProgress.setMessage("Loading....");
            mProgress.show();
            String url = currentEvent.getEventImage();
            byteArrayfromNetwork getArray = new byteArrayfromNetwork();
            byte[] byteArray = getArray.getByteArray(url);
            image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

            setData();
        }
        mProgress.dismiss();
    }

    void initializeViews() {
        Event = (TextView) findViewById(R.id.event);
        EventImage = (ImageView) findViewById(R.id.EventImage);
        EventDescription = (TextView) findViewById(R.id.description);
        EventDate = (TextView) findViewById(R.id.date);
    }

    void setData() {
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
