package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.Event;

public class EventViewHolder extends RecyclerView.ViewHolder {

    View mView;
    ImageView post_image;

    public EventViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void openEvent(final Event thisEvent) {
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(mView.getContext(), OpenEventDetail.class);
                i.putExtra("currentEvent", thisEvent);
                mView.getContext().startActivity(i);
            }
        });
    }


    public void setEventName(String eventName) {

        TextView post_name = (TextView) mView.findViewById(R.id.event);
        post_name.setText(eventName);

    }

    public void setEventDesc(String eventDesc) {

        TextView post_desc = (TextView) mView.findViewById(R.id.description);
        post_desc.setText(eventDesc);


    }

    public void setEventImage(final Context ctx, final String name, final String image) {

        post_image = (ImageView) mView.findViewById(R.id.postImg);
        Picasso.with(ctx).load(image).into(post_image);
        post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(mView.getContext(), viewImage.class);
                i.putExtra("currentEvent", name);
                i.putExtra("eventImage", image);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        });

    }


    public void setEventDate(String eventDate) {
        TextView post_date = (TextView) mView.findViewById(R.id.date);
        String date[] = eventDate.split("\\s+");
        String finalDate = "";

        for (int i = 0; i < 4; i++) {
            finalDate = finalDate + " " + date[i];
        }
        post_date.setText(finalDate);

    }

    public void setEventReminder(final String eventDescription, final String eventName, final String time) {
        Button Reminder = (Button) mView.findViewById(R.id.reminder);
        Reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addReminderInCalendar(eventName, eventDescription, Long.parseLong(String.valueOf(time)), mView.getContext());

            }

        });

    }

    private void addReminderInCalendar(String title, String desc, long time, Context context) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", time);
        intent.putExtra("allDay", false);
        intent.putExtra("rrule", "FREQ=DAILY");
        intent.putExtra("endTime", time + 60 * 60 * 1000);
        intent.putExtra("title", title);
        intent.putExtra("description", desc);
        context.startActivity(intent);

    }

}