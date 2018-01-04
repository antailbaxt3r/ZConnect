package com.zconnect.zutto.zconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.ItemFormats.Event;
import com.zconnect.zutto.zconnect.OpenEventDetail;
import com.zconnect.zutto.zconnect.R;

import java.util.Calendar;


public class TimelineEvents extends Fragment {
    private Query queryRef;
    private RecyclerView mEventList;

    public TimelineEvents() {
        // Required empty public constructor
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_timeline_events, container, false);

        LinearLayoutManager mlinearmanager;

        mlinearmanager = new LinearLayoutManager(getContext());

        //mlinearmanager.setStackFromEnd(true);
        mlinearmanager.scrollToPosition(1);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        mEventList = (RecyclerView) view.findViewById(R.id.eventList);
//        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(mlinearmanager);
        mlinearmanager.setReverseLayout(true);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Event/VerifiedPosts");
        queryRef = mDatabase.orderByChild("FormatDate");

        mDatabase.keepSynced(true);
        queryRef.keepSynced(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Event, EventViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(
                Event.class,
                R.layout.timeline_event_row,
                EventViewHolder.class,
                queryRef
        ) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model,
                                              int position) {
                try {
                    viewHolder.openEvent(model);
                }catch (Exception e) {
                    Log.d("Error Alert: ", e.getMessage());
                }
                try {
                    viewHolder.setEventName(model.getEventName(),model.getVerified());
                }catch (Exception e) {
                    Log.d("Error Alert: ", e.getMessage());
                }
                    viewHolder.setEventDate(model.getEventDate(),model.getBoostCount());
                    viewHolder.setEventReminder(model.getEventDescription(), model.getEventName(), model.getEventDate());
//                    viewHolder.setEditEvent(model.getUserID(),model.getKey());


            }
        };
        mEventList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        View mView;
        String key;

        FirebaseAuth mAuth;
        SharedPreferences sharedPref;
        Boolean status;

        public EventViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            sharedPref = mView.getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
            status = sharedPref.getBoolean("mode", false);
            if(!status) {
                mAuth = FirebaseAuth.getInstance();
            }
        }

        public void openEvent(final Event event) {

            key = event.getKey();

            mView.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View view) {
                    CounterManager.eventOpenCounter(key, "Timeline");
                    Intent i = new Intent(mView.getContext(), OpenEventDetail.class);
                    i.putExtra("currentEvent", event);
                    i.putExtra("Eventtag", "1");
                    mView.getContext().startActivity(i);
                }
            });
        }

        public void setEventName(String eventName, String verified) {
            if (eventName != null) {
                TextView post_name = (TextView) mView.findViewById(R.id.title);
                if(verified.equals("true")){
                    eventName = eventName + " ☑";
                }
                post_name.setText(eventName);
                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
                post_name.setTypeface(customFont);
            }
        }

        public void setEventDate(String eventDate, Double BoostCount) {
            if (eventDate != null) {
                TextView DateText = (TextView) mView.findViewById(R.id.date_text);
                TextView DateMonth = (TextView) mView.findViewById(R.id.date_month);
                TextView TimeNumber = (TextView) mView.findViewById(R.id.time_number);
                TextView TimeType = (TextView) mView.findViewById(R.id.time_type);
                TextView Boost = (TextView) mView.findViewById(R.id.boostcount);


                Boost.setText(BoostCount.intValue()+ " people boosted this event");
                String arr[]=eventDate.split(" ");

                String month = arr[1].trim().toUpperCase();
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


                DateText.setText(date.toString());
                DateMonth.setText(month);
                TimeNumber.setText(finalhour.toString());
                TimeType.setText(datatype);

                DateText.setText(date.toString());
                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
                DateText.setTypeface(customFont);
            }
        }

//        public void setEditEvent(String UserID, final String EventID) {
//            ImageButton editButton = (ImageButton) mView.findViewById(R.id.editEvent);
//            if(mAuth.getCurrentUser().getUid().equals(UserID)) {
//
//                editButton.setVisibility(View.VISIBLE);
//                editButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent editIntent = new Intent(mView.getContext(), AddEvent.class);
//                        editIntent.putExtra("eventID", EventID);
//                        mView.getContext().startActivity(editIntent);
//                    }
//                });
//            }else {
//                editButton.setVisibility(View.GONE);
//            }
//        }
        void setEventReminder(final String eventDescription, final String eventName, final String time) {
            if (eventDescription != null && eventName != null && time != null) {
                RelativeLayout DateTime = (RelativeLayout) mView.findViewById(R.id.date);
                DateTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CounterManager.eventReminderCounter(key);
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
    }
}
