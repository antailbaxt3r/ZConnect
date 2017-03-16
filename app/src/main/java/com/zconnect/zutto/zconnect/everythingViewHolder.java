package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

/**
 * Created by Lokesh Garg on 09-02-2017.
 */

public class everythingViewHolder extends RecyclerView.ViewHolder {

    View mView;

    public everythingViewHolder(View itemView) {

        super(itemView);
        mView = itemView;


    }


    public void openEvent() {
        mView.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(mView.getContext(), AllEvents.class);
                mView.getContext().startActivity(i);
            }
        });
    }

    public void openProduct() {
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.getContext().startActivity(new Intent(mView.getContext(), TabStoreRoom.class));

            }
        });
    }

    public void removeView(Boolean product) {
        LinearLayout del = (LinearLayout) mView.findViewById(R.id.ContactCardEverything);
        del.setVisibility(View.GONE);
        LinearLayout show = (LinearLayout) mView.findViewById(R.id.EventsAndDescriptionEverything);
        show.setVisibility(View.VISIBLE);
        if(product)
        {
            mView.findViewById(R.id.ev_directions).setVisibility(View.GONE);
            mView.findViewById(R.id.ev_venue).setVisibility(View.GONE);
        }

    }

    public void setTitle(String title) {

        TextView postTitle = (TextView) mView.findViewById(R.id.title_everything);
        postTitle.setText(title);

    }

    public void setDescription(String description) {


        TextView postDesc = (TextView) mView.findViewById(R.id.description_or_price);
        postDesc.setText(description);
    }

    public void setDate(final String title, boolean isNumber, final Context context) {
        TextView setDate = (TextView) mView.findViewById(R.id.date_event);
        if (isNumber)
        {
            setDate.setText("+91 " + title);
            setDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Long.parseLong(title.trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });
        } else {
            String date[] = title.split("\\s+");
            String finalDate = "";
            for (int i = 0; i < 4; i++) {
                finalDate = finalDate + " " + date[i];
            }

            setDate.setText(finalDate);
        }
    }

    public void setPrice(String price) {
        TextView setPrice = (TextView) mView.findViewById(R.id.price);
        setPrice.setText(price);
    }

    public void setTime(String title) {


        TextView setTime = (TextView) mView.findViewById(R.id.time);
        setTime.setText(title);

    }

    public void setImage(Context context, String url) {


        ImageView imageView = (ImageView) mView.findViewById(R.id.Image_everything);
        Picasso.with(context).load(url).into(imageView);

    }

    public void setBarColor(Context context, Boolean event) {
        View view = mView.findViewById(R.id.bar);
        ImageView icon = (ImageView) mView.findViewById((R.id.icon));
        if (event) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.EventsBar));
            icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_event_black_24dp));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.ProductBar));
            icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_phone_black_24dp));
        }
    }

    public void setDirections(final Double lon, final Double lat)
    {
        ImageView mDirections = (ImageView) mView.findViewById(R.id.ev_directions);
        mDirections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + lat + "," + lon));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mView.getContext().startActivity(intent);
            }
        });
    }

    public void makeButton(final String title, final String desc, final long time) {
        RelativeLayout layout = (RelativeLayout) mView.findViewById(R.id.icon_and_text_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminderInCalendar(title, desc, time, mView.getContext());
            }
        });
    }

    public void setVenue(String venue){
        TextView mVenue = (TextView) mView.findViewById(R.id.ev_venue);
        mVenue.setText(venue);
    }
    private void addReminderInCalendar(String title, String desc, long time_gaph, Context context) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", time_gaph);
        intent.putExtra("allDay", false);
        intent.putExtra("rrule", "FREQ=DAILY");
        intent.putExtra("endTime", time_gaph + 60 * 60 * 1000);
        intent.putExtra("title", title);
        intent.putExtra("description", desc);
        context.startActivity(intent);

        // Display event id.
        //Toast.makeText(getAppli
        //cationContext(), "Event added :: ID :: " + event.getLastPathSegment(), Toast.LENGTH_SHORT).show();

        /** Adding reminder for event added. *
         }

         /** Returns Calendar Base URI, supports both new and old OS. */


    }

    public void makeContactView(final Context context, final PhonebookDisplayItem phonebookDisplayItem) {

        LinearLayout mlayout = (LinearLayout) mView.findViewById(R.id.EventsAndDescriptionEverything);
        mlayout.setVisibility(View.GONE);
        LinearLayout mRel = (LinearLayout) mView.findViewById(R.id.ContactCardEverything);
        mRel.setVisibility(View.VISIBLE);
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) mView.findViewById(R.id.everything_item_format_image);
        simpleDraweeView.setImageURI(Uri.parse(phonebookDisplayItem.getImageurl()));
        TextView name = (TextView) mView.findViewById(R.id.everything_name1);
        name.setText(phonebookDisplayItem.getName());
        TextView number = (TextView) mView.findViewById(R.id.everything_number1);
        number.setText(phonebookDisplayItem.getNumber());
        mRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Phonebook.class);
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });


    }
}
