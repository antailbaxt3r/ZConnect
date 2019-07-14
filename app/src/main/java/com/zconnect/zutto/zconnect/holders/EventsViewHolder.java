package com.zconnect.zutto.zconnect.holders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.addActivities.AddEvent;
import com.zconnect.zutto.zconnect.LoginActivity;
import com.zconnect.zutto.zconnect.commonModules.CounterPush;
import com.zconnect.zutto.zconnect.commonModules.NotificationSender;
import com.zconnect.zutto.zconnect.OpenEventDetail;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.itemFormats.CounterItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.NotificationItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.CounterUtilities;
import com.zconnect.zutto.zconnect.utilities.NotificationIdentifierUtilities;
import com.zconnect.zutto.zconnect.utilities.TimeUtilities;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;
import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityTitle;

/**
 * Created by Lokesh Garg on 23-04-2018.
 */

public class EventsViewHolder extends RecyclerView.ViewHolder {

    View mView;
    String key;
    Boolean flag= false;
    Boolean flag2 = false;
    FirebaseAuth mAuth;
    SharedPreferences sharedPref;
    Boolean status;
String recieverKey;
    SharedPreferences communitySP;
    String communityReference;

    public EventsViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        sharedPref = mView.getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
        status = sharedPref.getBoolean("mode", false);
        if(!status) {
            mAuth = FirebaseAuth.getInstance();
        }
        communitySP = itemView.getContext().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
    }

    static String monthSwitcher(String mon) {

        if (mon.equalsIgnoreCase("Jan")) {
            return "01";
        } else if (mon.equalsIgnoreCase("Feb")) {
            return "02";
        } else if (mon.equalsIgnoreCase("Mar")) {
            return "03";
        } else if (mon.equalsIgnoreCase("Apr")) {
            return "04";
        } else if (mon.equalsIgnoreCase("May")) {
            return "05";
        } else if (mon.equalsIgnoreCase("Jun")) {
            return "06";
        } else if (mon.equalsIgnoreCase("Jul")) {
            return "07";
        } else if (mon.equalsIgnoreCase("Aug")) {
            return "08";
        } else if (mon.equalsIgnoreCase("Sept")) {
            return "09";
        } else if (mon.equalsIgnoreCase("Oct")) {
            return "10";
        } else if (mon.equalsIgnoreCase("Nov")) {
            return "11";
        } else if (mon.equalsIgnoreCase("Dec")) {
            return "12";
        } else
            return "00";

    }


//    public void checkDelete(Long date, final String eventID){
//        final DatabaseReference eventReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("events").child("activeEvents").child(eventID);
//        final DatabaseReference archivedReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("events").child("archivedEvents").child(eventID);
//        flag2 = false;
//        Long currentDate = System.currentTimeMillis() - 86400000;
//        if (currentDate>date){
//            eventReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if(!flag2) {
//                        archivedReference.setValue(dataSnapshot.getValue());
//                        flag2 = true;
//                        eventReference.removeValue();
//                        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home").child(eventID).removeValue();
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }
//    }

    public void setEventDate(String eventDate) {
        if (eventDate != null) {
            TextView dateText = (TextView) mView.findViewById(R.id.evUpcmgDate);
            TextView timeText = (TextView) mView.findViewById(R.id.evUpcmgTime);

            String arr[]=eventDate.split(" ");

            String month = arr[1].trim();
            String date=arr[2];
            String year=arr[5];
            String times=arr[3];

            String timesA[]=times.split(":");
            String hour=timesA[0];
            String mins=timesA[1];
            String datatype;
            Integer finalhour;

            if(Integer.parseInt(hour)>12)
            {
                datatype="PM";
                finalhour=Integer.parseInt(hour) -12;
            }else if(Integer.parseInt(hour)==12)
            {
                datatype="PM";
                finalhour=12;
            }else {
                datatype="AM";
                finalhour=Integer.parseInt(hour);
            }

            dateText.setText(date.toString() + " " + month);
            timeText.setText(finalhour.toString() + " " + datatype);
//                DateText.setText(date.toString());
//                DateMonth.setText(month);
//                TimeNumber.setText(finalhour.toString());
//                TimeType.setText(datatype);

//                DateText.setText(date.toString());
//                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
            Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
            dateText.setTypeface(customFont);
            timeText.setTypeface(customFont);
        }
    }

    public void setEventReminder(final String eventDescription, final String eventName, final String time) {
        if (eventDescription != null && eventName != null && time != null) {
            LinearLayout DateTime = (LinearLayout) mView.findViewById(R.id.dateAndTime);
            DateTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    addReminderInCalendar(eventName, eventDescription, time, mView.getContext());

                }

            });
        }
    }

    private void addReminderInCalendar(String title, String desc, String time, Context context) {

        String arr[]=time.split(" ");

        String month=monthSwitcher(arr[1]);
        String date=arr[2];
        String year=arr[5];
        String times=arr[3];

        String timesA[]=times.split(":");
        String hour=timesA[0];
        String mins=timesA[1];


        Calendar beginTime = Calendar.getInstance();
        beginTime.set(Integer.parseInt(year),Integer.parseInt(month)-1, Integer.parseInt(date),
                Integer.parseInt(hour), Integer.parseInt(mins));
        Calendar endTime = Calendar.getInstance();
        endTime.set(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(date),
                Integer.parseInt(hour)+1, Integer.parseInt(mins));


        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
        intent.putExtra(CalendarContract.Events.ALL_DAY, false);
        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, desc);
        intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
        context.startActivity(intent);
    }


    public void openEvent(final String key, final String type) {



        mView.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {

                CounterItemFormat counterItemFormat = new CounterItemFormat();
                HashMap<String, String> meta= new HashMap<>();

                meta.put("type",type);

                counterItemFormat.setUserID(FirebaseAuth.getInstance().getUid());
                counterItemFormat.setUniqueID(CounterUtilities.KEY_EVENTS_OPEN_EVENT);
                counterItemFormat.setTimestamp(System.currentTimeMillis());
                counterItemFormat.setMeta(meta);

                CounterPush counterPush = new CounterPush(counterItemFormat, communityReference);
                counterPush.pushValues();

                Intent i = new Intent(mView.getContext(), OpenEventDetail.class);
                i.putExtra("id", key);
                mView.getContext().startActivity(i);
            }
        });
    }

    public void setEventName(String eventName) {
        if (eventName != null) {
            TextView post_name = (TextView) mView.findViewById(R.id.er_event);
            post_name.setText(eventName);
            Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
            post_name.setTypeface(customFont);
        }
    }

//    public void setEventDesc(String eventDesc) {
////            if (eventDesc != null) {
////                String shortEventDesc;
//
////                TextView post_desc = (TextView) mView.findViewById(R.id.er_description);
////                if (eventDesc.length() < 70) {
////                    shortEventDesc = eventDesc;
////                } else {
////                    shortEventDesc = eventDesc.substring(0, 70);
////                    shortEventDesc = shortEventDesc + " ... read more";
////                }
//
////                post_desc.setText(shortEventDesc);
//        Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
////                post_desc.setTypeface(customFont);
////            }
//    }

//    public void setEventVenue(String venue) {
////            if (venue != null) {
////                TextView post_venue = (TextView) mView.findViewById(R.id.er_venue);
////                post_venue.setText(venue);
////                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
////                post_venue.setTypeface(customFont);
////            }
//    }
    public void setEventImage(Context ctx, String image) {
        if (image != null) {
            SimpleDraweeView post_image = (SimpleDraweeView) mView.findViewById(R.id.er_postImg);
            Picasso.with(ctx).load(image).into(post_image);
            ProgressBar progressBar = (ProgressBar) mView.findViewById(R.id.trending_event_image_progress_circle);
            progressBar.setVisibility(View.GONE);
            post_image.setVisibility(View.VISIBLE);
        }
    }

//    public void setEventDate(String eventDate) {
////            if (eventDate != null) {
////                TextView post_date = (TextView) mView.findViewById(R.id.er_date);
////                String date[] = eventDate.split("\\s+");
////                StringBuilder finalDate = new StringBuilder();
////
////                for (int i = 0; i < 4; i++) {
////                    finalDate.append(" ").append(date[i]);
////                }
////
////                post_date.setText(finalDate.toString());
////                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
////                post_date.setTypeface(customFont);
////            }
//    }

    public void setEditEvent(String UserID, final String EventID) {
        ImageButton editButton = (ImageButton) mView.findViewById(R.id.editEvent);
        if (mAuth.getCurrentUser().getUid().equals(UserID)) {

            editButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editIntent = new Intent(mView.getContext(), AddEvent.class);
                    editIntent.putExtra("eventID", EventID);

                    mView.getContext().startActivity(editIntent);
                }
            });
        } else {
            editButton.setVisibility(View.GONE);
        }
    }


    public void setBoost(final String key, final String name) {

        final DatabaseReference eventDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("events").child("activeEvents").child(key);
eventDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        recieverKey = (String) dataSnapshot.child("PostedBy").child("UID").getValue();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final ImageButton boostBtn = (ImageButton) mView.findViewById(R.id.boostBtn);
        final TextView eventNumLit = (TextView) mView.findViewById(R.id.eventsNumLit);



        eventDatabase.child("BoostersUids").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                eventDatabase.child("BoostCount").setValue(dataSnapshot.getChildrenCount());

                if(dataSnapshot.hasChild(user.getUid())){
//                        boostBtn.setText(dataSnapshot.getChildrenCount() + " Boost");
                    eventNumLit.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    boostBtn.setColorFilter(mView.getContext().getResources().getColor(R.color.lit));
//                        boostBtn.getBackground().setTint(mView.getContext().getResources().getColor(R.color.lit));
//                        boostBtn.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton2_sr));
                    flag=true;
                }else {
//                        boostBtn.setText(dataSnapshot.getChildrenCount() + " Boost");
                    eventNumLit.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    boostBtn.setColorFilter(mView.getContext().getResources().getColor(R.color.primaryText));
//                        boostBtn.getBackground().setTint(mView.getContext().getResources().getColor(R.color.primaryText));
//                        boostBtn.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton_sr));
                    flag=false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        if (user != null) {
            boostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!flag){
                        Map<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put(user.getUid(), user.getUid());
                        eventDatabase.child("BoostersUids").updateChildren(taskMap);



                        DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        user.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UserItemFormat userItemFormat = dataSnapshot.getValue(UserItemFormat.class);

                                NotificationSender notificationSender = new NotificationSender(itemView.getContext(),FirebaseAuth.getInstance().getCurrentUser().getUid());
                                NotificationItemFormat eventBoostNotification = new NotificationItemFormat(NotificationIdentifierUtilities.KEY_NOTIFICATION_EVENT_BOOST,FirebaseAuth.getInstance().getCurrentUser().getUid(),recieverKey,1);
                                eventBoostNotification.setItemKey(key);
                                eventBoostNotification.setUserImage(userItemFormat.getImageURLThumbnail());
                                eventBoostNotification.setItemName(name);
                                eventBoostNotification.setUserName(userItemFormat.getUsername());
                                eventBoostNotification.setCommunityName(communityTitle);
                                eventBoostNotification.setRecieverKey(recieverKey);
                                notificationSender.execute(eventBoostNotification);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });

                    }else {
                        eventDatabase.child("BoostersUids").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

                    }
                }
            });

        } else {
            boostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mView.getContext());
                    builder.setNegativeButton("Lite", null)
                            .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent loginIntent = new Intent(mView.getContext(), LoginActivity.class);
                                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    mView.getContext().startActivity(loginIntent);
                                }
                            })
                            .setTitle("Please login to boost.");

                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(itemView.getResources().getColor(R.color.colorHighlight));
                }
            });
        }

        Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
        eventNumLit.setTypeface(customfont);
    }

//    private void setPostedByDetails(String username, String imageThumb) {
////            if(username!= null) {
////                TextView post_postedBy = (TextView) mView.findViewById(R.id.eventPostedBy);
////                SimpleDraweeView post_postedByAvatar = (SimpleDraweeView) mView.findViewById(R.id.eventPostedByAvatar);
////                post_postedBy.setText(username);
////                if(imageThumb != null)
////                {
////                    post_postedByAvatar.setImageURI(imageThumb);
////                }
////                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
////                post_postedBy.setTypeface(customFont);
////            }
//    }

    public void setEventTimestamp(long postTimeMillis) {
        if(postTimeMillis > 0) {
            TextView timestamp = (TextView) mView.findViewById(R.id.evTrendTimestamp);
            TimeUtilities ta = new TimeUtilities(postTimeMillis, System.currentTimeMillis());
            timestamp.setText(ta.calculateTimeAgo());
        }
    }

    public void setEventDesc(String eventDesc, final String key) {
        TextView desc = (TextView) mView.findViewById(R.id.evTrendDesc);
        if(eventDesc.length() < 55) {
            desc.setText(eventDesc);
        }
        else {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(mView.getContext(), OpenEventDetail.class);
                    i.putExtra("id", key);
                    mView.getContext().startActivity(i);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false); // set to false to remove underline
                }
            };
            String descWithMore = eventDesc.substring(0, 55) + " more...";
            SpannableString spannableString = new SpannableString(descWithMore);
            spannableString.setSpan(clickableSpan, 0, descWithMore.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mView.getContext().getResources().getColor(R.color.link));
            spannableString.setSpan(foregroundColorSpan, descWithMore.indexOf("more..."), descWithMore.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            foregroundColorSpan = new ForegroundColorSpan(mView.getContext().getResources().getColor(R.color.primaryText));
            spannableString.setSpan(foregroundColorSpan,0, descWithMore.indexOf("more..."), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            desc.setText(spannableString);
            desc.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

}

