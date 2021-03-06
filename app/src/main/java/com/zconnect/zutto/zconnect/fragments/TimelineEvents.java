package com.zconnect.zutto.zconnect.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.addActivities.AddEvent;
import com.zconnect.zutto.zconnect.itemFormats.Event;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.adapters.EventsAdapter;

import java.util.Vector;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;


public class TimelineEvents extends Fragment {
    private Query queryRef;
    private RecyclerView mEventList;
    private EventsAdapter eventsAdapter;
    private Vector<Event> eventsVector = new Vector<Event>();
    private ValueEventListener mListener;
    private RelativeLayout relativeLayout;
    private View lineView;
    private ShimmerFrameLayout shimmerFrameLayout;
    private TextView noevents;
    private FloatingActionButton fab;

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

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("events").child("activeEvents");
        queryRef = mDatabase.orderByChild("FormatDate");

        mDatabase.keepSynced(true);
        queryRef.keepSynced(true);

        noevents = (TextView) view.findViewById(R.id.noevents);
        relativeLayout = view.findViewById(R.id.relativeLayoutTimeline);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container_timeline_events);
        lineView = (View) view.findViewById(R.id.line_view);
        fab = (FloatingActionButton) view.findViewById(R.id.fab_timeline_events);
        mEventList = (RecyclerView) view.findViewById(R.id.eventList);

        shimmerFrameLayout.setVisibility(View.VISIBLE);
        shimmerFrameLayout.startShimmerAnimation();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable(view.getContext())) {
                    Snackbar snack = Snackbar.make(fab, "No internet. Please try again later.", Snackbar.LENGTH_LONG);
                    TextView snackBarText = (TextView) snack.getView().findViewById(android.support.design.R.id.snackbar_text);
                    snackBarText.setTextColor(Color.WHITE);
                    snack.getView().setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorPrimaryDark));
                    snack.show();
                } else {


                    getContext().startActivity(new Intent(getContext(), AddEvent.class));

                }
            }
        });

        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventsVector.clear();
                Boolean flag = false;
                for (DataSnapshot shot: dataSnapshot.getChildren()) {
                    Event singleEvent;
                    try {
                        singleEvent = shot.getValue(Event.class);
                        if(!singleEvent.getKey().equals(null) && !singleEvent.getEventName().equals(null)) {
                            Long currentDate = System.currentTimeMillis() - 7200000;
                            Long dateMillis = singleEvent.getEventTimeMillis();
                            if (currentDate<dateMillis){
                                eventsVector.add(singleEvent);
                                flag=true;
                            }
                        }
                    }catch (Exception e){}
                }
                eventsAdapter.notifyDataSetChanged();
                if (flag){
                    noevents.setVisibility(View.GONE);
                    lineView.setVisibility(View.VISIBLE);
                } else {
                    noevents.setVisibility(View.VISIBLE);
                    lineView.setVisibility(View.GONE);
                }

                mEventList.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(mlinearmanager);

        eventsAdapter = new EventsAdapter(getContext(),eventsVector,"timeline");
        mEventList.setAdapter(eventsAdapter);

        mDatabase.keepSynced(true);
        queryRef.keepSynced(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        queryRef.addListenerForSingleValueEvent(mListener);

//        FirebaseRecyclerAdapter<Event, EventViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(
//                Event.class,
//                R.layout.timeline_event_row,
//                EventViewHolder.class,
//                queryRef
//        ) {
//            @Override
//            protected void populateViewHolder(EventViewHolder viewHolder, Event model,
//                                              int position) {
//
//                try{
//                    viewHolder.setEventStatus(model.getEventDate());
//                }catch (Exception e){}
//
//                try {
//                    viewHolder.openEvent(model);
//                }catch (Exception e) {
//                    Log.d("Error Alert: ", e.getMessage());
//                }
//                try {
//                    viewHolder.setEventName(model.getEventName(),model.getVerified(), model.getPostTimeMillis());
//                }
//                catch (Exception e) {
//                   // Log.d("Error Alert: ", e.getMessage());
//                    model.setVerified("false");
//                    viewHolder.setEventName(model.getEventName(),model.getVerified(), model.getPostTimeMillis());
//                }
//                try {
//                    viewHolder.setBoost(model);
//                }
//                catch (Exception e) {
//                    Log.d("Error Alert: ", e.getMessage());
//                }
//                    viewHolder.setEventDate(model.getEventDate(),model.getBoostCount());
//                    viewHolder.setEventReminder(model.getEventDescription(), model.getEventName(), model.getEventDate());
////                    viewHolder.setEditEvent(model.getUserID(),model.getKey());
//
//
//            }
//        };
//        mEventList.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        queryRef.removeEventListener(mListener);
    }

    //    public static class EventViewHolder extends RecyclerView.ViewHolder {
//
//        View mView;
//        String key;
//
//        FirebaseAuth mAuth;
//        SharedPreferences sharedPref;
//        Boolean status, flag;
//
////        RelativeLayout eventStatus;
////        TextView eventOver;
//
//        public EventViewHolder(View itemView) {
//            super(itemView);
//            mView = itemView;
//            sharedPref = mView.getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
//            status = sharedPref.getBoolean("mode", false);
//            if(!status) {
//                mAuth = FirebaseAuth.getInstance();
//            }
//        }
//
//        public void openEvent(final Event event) {
//
//            key = event.getKey();
//
//            mView.setOnClickListener(new View.OnClickListener()
//
//            {
//                @Override
//                public void onClick(View view) {
//                    Intent i = new Intent(mView.getContext(), OpenEventDetail.class);
//                    i.putExtra("id", event.getKey());
//                    mView.getContext().startActivity(i);
//                }
//            });
//        }
//
//        public void setEventName(String eventName, String verified, long postTimeMillis) {
//            if (eventName != null) {
//                TextView post_name = (TextView) mView.findViewById(R.id.name);
//                if(verified.equals("true")){
//                    eventName = eventName + " ☑";
//                }
//                Log.d("EVENT", eventName + " + " + verified + " + " + postTimeMillis);
//                post_name.setText(eventName);
//                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-SemiBold.ttf");
//                post_name.setTypeface(customFont);
//            }
//            if (postTimeMillis > 0)
//            {
//                TextView post_timestamp = (TextView) mView.findViewById(R.id.timestamp);
//                TimeUtilities ta = new TimeUtilities(postTimeMillis, System.currentTimeMillis());
//                post_timestamp.setText(ta.calculateTimeAgo());
//            }
//        }
//
//        public void setEventDate(String eventDate, Double BoostCount) {
//            if (eventDate != null) {
//                TextView dateText = (TextView) mView.findViewById(R.id.evUpcmgDate);
//                TextView timeText = (TextView) mView.findViewById(R.id.evUpcmgTime);
////                TextView DateText = (TextView) mView.findViewById(R.id.date_text);
////                TextView DateMonth = (TextView) mView.findViewById(R.id.date_month);
////                TextView TimeNumber = (TextView) mView.findViewById(R.id.time_number);
////                TextView TimeType = (TextView) mView.findViewById(R.id.time_type);
//                TextView Boost = (TextView) mView.findViewById(R.id.boostcount);
//
//
//                Boost.setText(BoostCount.intValue()+ "x lit");
//                String arr[]=eventDate.split(" ");
//
//                String month = arr[1].trim();
//                String date=arr[2];
//                String year=arr[5];
//                String times=arr[3];
//
//                String timesA[]=times.split(":");
//                String hour=timesA[0];
//                String mins=timesA[1];
//                String datatype;
//                Integer finalhour;
//
//                if(Integer.parseInt(hour)>12)
//                {
//                    datatype="PM";
//                    finalhour=Integer.parseInt(hour) -12;
//                }else if(Integer.parseInt(hour)==12)
//                {
//                    datatype="PM";
//                    finalhour=12;
//                }else {
//                    datatype="AM";
//                    finalhour=Integer.parseInt(hour);
//                }
//
//                dateText.setText(date.toString() + " " + month);
//                timeText.setText(finalhour.toString() + " " + datatype);
////                DateText.setText(date.toString());
////                DateMonth.setText(month);
////                TimeNumber.setText(finalhour.toString());
////                TimeType.setText(datatype);
//
////                DateText.setText(date.toString());
////                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
//                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
//                dateText.setTypeface(customFont);
//                timeText.setTypeface(customFont);
//            }
//        }
//
////        public void setEditEvent(String UserID, final String EventID) {
////            ImageButton editButton = (ImageButton) mView.findViewById(R.id.editEvent);
////            if(mAuth.getCurrentUser().getUid().equals(UserID)) {
////
////                editButton.setVisibility(View.VISIBLE);
////                editButton.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View v) {
////                        Intent editIntent = new Intent(mView.getContext(), AddEvent.class);
////                        editIntent.putExtra("eventID", EventID);
////                        mView.getContext().startActivity(editIntent);
////                    }
////                });
////            }else {
////                editButton.setVisibility(View.GONE);
////            }
////        }
//        void setEventReminder(final String eventDescription, final String eventName, final String time) {
//            if (eventDescription != null && eventName != null && time != null) {
//                LinearLayout DateTime = (LinearLayout) mView.findViewById(R.id.dateAndTime);
//                DateTime.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        addReminderInCalendar(eventName, eventDescription, time, mView.getContext());
//
//                    }
//
//                });
//            }
//        }
//
//        private void addReminderInCalendar(String title, String desc, String time, Context context) {
//
//            String arr[]=time.split(" ");
//
//            String month=monthSwitcher(arr[1]);
//            String date=arr[2];
//            String year=arr[5];
//            String times=arr[3];
//
//            String timesA[]=times.split(":");
//            String hour=timesA[0];
//            String mins=timesA[1];
//
//
//            Calendar beginTime = Calendar.getInstance();
//            beginTime.set(Integer.parseInt(year),Integer.parseInt(month)-1, Integer.parseInt(date),
//                    Integer.parseInt(hour), Integer.parseInt(mins));
//            Calendar endTime = Calendar.getInstance();
//            endTime.set(Integer.parseInt(year),Integer.parseInt(month),Integer.parseInt(date),
//                    Integer.parseInt(hour)+1, Integer.parseInt(mins));
//
//
//            Intent intent = new Intent(Intent.ACTION_INSERT);
//            intent.setData(CalendarContract.Events.CONTENT_URI);
//            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
//            intent.putExtra(CalendarContract.Events.ALL_DAY, false);
//            intent.putExtra(CalendarContract.Events.TITLE, title);
//            intent.putExtra(CalendarContract.Events.DESCRIPTION, desc);
//            intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
//            context.startActivity(intent);
//        }
//
//        public void setEventStatus( String eventDate) {
//            if(eventDate != null)
//            {
////                eventStatus = (RelativeLayout) itemView.findViewById(R.id.event_status);
////                eventOver = (TextView) itemView.findViewById(R.id.event_over);
//                Calendar calendar = Calendar.getInstance();
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
//                String currFormattedDate = simpleDateFormat.format(calendar.getTime());
//
//                String currEventDate[]=eventDate.split(" ");
//
//                String month = monthSwitcher(currEventDate[1]);
//                String date=currEventDate[2];
//                String year=currEventDate[5];
//
//                String formattedDate[] = currFormattedDate.split("-");
//
//                String currDate = formattedDate[0];
//                String currMonth = formattedDate[1];
//                String currYear = formattedDate[2];
//
//                if(Integer.parseInt(currYear)>Integer.parseInt(year)) {
////                    eventStatus.setVisibility(View.VISIBLE);
//                }
//                else if(Integer.parseInt(currMonth)>Integer.parseInt(month)) {
////                    eventStatus.setVisibility(View.VISIBLE);
//                }
//                else if(Integer.parseInt(currDate) > Integer.parseInt(date)) {
////                    eventStatus.setVisibility(View.VISIBLE);
//                }
//            }
//        }
//
//        private void setBoost(final Event event) {
//
//            final DatabaseReference eventDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("events").child("activeEvents").child(event.getKey());
//
//            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            final ImageButton boostBtn = (ImageButton) mView.findViewById(R.id.boostBtn);
//            final TextView eventNumLit = (TextView) mView.findViewById(R.id.boostcount);
//
//
//
//            eventDatabase.child("BoostersUids").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    eventDatabase.child("BoostCount").setValue(dataSnapshot.getChildrenCount());
//
//                    if(dataSnapshot.hasChild(user.getUid())){
////                        boostBtn.setText(dataSnapshot.getChildrenCount() + " Boost");
//                        eventNumLit.setText(String.valueOf(dataSnapshot.getChildrenCount())+"x lit");
//                        boostBtn.setColorFilter(mView.getContext().getResources().getColor(R.color.lit));
////                        boostBtn.getBackground().setTint(mView.getContext().getResources().getColor(R.color.lit));
////                        boostBtn.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton2_sr));
//                        flag=true;
//                    }else {
////                        boostBtn.setText(dataSnapshot.getChildrenCount() + " Boost");
//                        eventNumLit.setText(String.valueOf(dataSnapshot.getChildrenCount())+"x lit");
//                        boostBtn.setColorFilter(mView.getContext().getResources().getColor(R.color.primaryText));
////                        boostBtn.getBackground().setTint(mView.getContext().getResources().getColor(R.color.primaryText));
////                        boostBtn.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton_sr));
//                        flag=false;
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//
//
//            if (user != null) {
//                boostBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(!flag){
//                            Map<String, Object> taskMap = new HashMap<String, Object>();
//                            taskMap.put(user.getUid(), user.getUid());
//                            eventDatabase.child("BoostersUids").updateChildren(taskMap);
//
//                            //Sending Notifications
//                             NotificationSender notificationSender=new NotificationSender(key,null,null,event.getEventName(),null,user.getDisplayName(),null,"EventBoosted",false,true,itemView.getContext());
//                             notificationSender.execute();
//
//                        }else {
//                            eventDatabase.child("BoostersUids").child(user.getUid()).removeValue();
//
//                        }
//                    }
//                });
//
//            } else {
//                boostBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(mView.getContext());
//                        dialog.setNegativeButton("Lite", null)
//                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Intent loginIntent = new Intent(mView.getContext(), LoginActivity.class);
//                                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        mView.getContext().startActivity(loginIntent);
//                                    }
//                                })
//                                .setTitle("Please login to boost.")
//                                .create().show();
//                    }
//                });
//            }
//
////            Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//            Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
//            eventNumLit.setTypeface(customFont);
//        }
//    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
