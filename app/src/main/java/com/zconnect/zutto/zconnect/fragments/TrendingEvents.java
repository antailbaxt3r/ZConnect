package com.zconnect.zutto.zconnect.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.AddEvent;
import com.zconnect.zutto.zconnect.CounterManager;
import com.zconnect.zutto.zconnect.ItemFormats.Event;
import com.zconnect.zutto.zconnect.LoginActivity;
import com.zconnect.zutto.zconnect.NotificationSender;
import com.zconnect.zutto.zconnect.OpenEventDetail;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.Utilities.TimeAgo;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class TrendingEvents extends Fragment {

    private Query queryRef;
    private RecyclerView mEventList;

    public TrendingEvents() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trending_event, container, false);

        LinearLayoutManager mlinearmanager;
        mlinearmanager = new LinearLayoutManager(getContext());

        mlinearmanager.setStackFromEnd(true);
        mlinearmanager.setReverseLayout(true);
//        mlinearmanager.scrollToPosition(1);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        mEventList = (RecyclerView) view.findViewById(R.id.eventList);
        mEventList.setHasFixedSize(true);
        mEventList.setLayoutManager(mlinearmanager);

        SharedPreferences communitySP;
        String communityReference;

        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Event/VerifiedPosts");
        queryRef = mDatabase.orderByChild("BoostCount");

        mDatabase.keepSynced(true);
        queryRef.keepSynced(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Event, EventViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Event, TrendingEvents.EventViewHolder>(
                Event.class,
                R.layout.events_row,
                EventViewHolder.class,
                queryRef
        ) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model,
                                              int position) {
                try {
                    viewHolder.openEvent(model);
                    viewHolder.setEventName(model.getEventName());
                    viewHolder.setEventDesc(model.getEventDescription());
                    viewHolder.setEventImage(getContext(), model.getEventImage());
                    viewHolder.setEventDate(model.getEventDate());
                    viewHolder.setEventVenue(model.getVenue());
                    viewHolder.setEditEvent(model.getUserID(), model.getKey());
                    viewHolder.setBoost(model);
                    viewHolder.setPostedByDetails(model.getPostedBy().getUsername(), model.getPostedBy().getImageThumb());
                    viewHolder.setEventTimestamp(model.getPostTimeMillis());
                }
                catch (Exception e) {
                    Log.d("Error Alert: ", e.getMessage());
                }

            }
        };
        mEventList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        View mView;
        String key;
        Boolean flag= false;

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
                    CounterManager.eventOpenCounter(key, "Trending");
                    Intent i = new Intent(mView.getContext(), OpenEventDetail.class);
                    i.putExtra("id", event.getKey());
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

        public void setEventDesc(String eventDesc) {
//            if (eventDesc != null) {
//                String shortEventDesc;

//                TextView post_desc = (TextView) mView.findViewById(R.id.er_description);
//                if (eventDesc.length() < 70) {
//                    shortEventDesc = eventDesc;
//                } else {
//                    shortEventDesc = eventDesc.substring(0, 70);
//                    shortEventDesc = shortEventDesc + " ... read more";
//                }

//                post_desc.setText(shortEventDesc);
                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
//                post_desc.setTypeface(customFont);
//            }
        }

        public void setEventVenue(String venue) {
//            if (venue != null) {
//                TextView post_venue = (TextView) mView.findViewById(R.id.er_venue);
//                post_venue.setText(venue);
//                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
//                post_venue.setTypeface(customFont);
//            }
        }
        public void setEventImage(Context ctx, String image) {
            if (image != null) {
//                ImageView post_image = (ImageView) mView.findViewById(R.id.er_postImg);
                SimpleDraweeView post_image = (SimpleDraweeView) mView.findViewById(R.id.er_postImg);
                Picasso.with(ctx).load(image).into(post_image);
            }
        }

        public void setEventDate(String eventDate) {
//            if (eventDate != null) {
//                TextView post_date = (TextView) mView.findViewById(R.id.er_date);
//                String date[] = eventDate.split("\\s+");
//                StringBuilder finalDate = new StringBuilder();
//
//                for (int i = 0; i < 4; i++) {
//                    finalDate.append(" ").append(date[i]);
//                }
//
//                post_date.setText(finalDate.toString());
//                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
//                post_date.setTypeface(customFont);
//            }
        }

        public void setEditEvent(String UserID, final String EventID) {
            ImageButton editButton = (ImageButton) mView.findViewById(R.id.editEvent);
            if (mAuth.getCurrentUser().getUid().equals(UserID)) {

                editButton.setVisibility(View.VISIBLE);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent editIntent = new Intent(mView.getContext(), AddEvent.class);
                        editIntent.putExtra("eventID", EventID);
                        CounterManager.eventEdit(EventID, "Trending");
                        mView.getContext().startActivity(editIntent);
                    }
                });
            } else {
                editButton.setVisibility(View.GONE);
            }
        }


        private void setBoost(final Event event) {

            final DatabaseReference eventDatabase = FirebaseDatabase.getInstance().getReference().child("Event/VerifiedPosts").child(event.getKey());

            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final ImageButton boostBtn = (ImageButton) mView.findViewById(R.id.boostBtn);
            final TextView eventNumLit = (TextView) mView.findViewById(R.id.eventsNumLit);



            eventDatabase.child("BoostersUids").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    eventDatabase.child("BoostCount").setValue(dataSnapshot.getChildrenCount());

                    if(dataSnapshot.hasChild(user.getUid())){
//                        boostBtn.setText(dataSnapshot.getChildrenCount() + " Boost");
                        eventNumLit.setText(String.valueOf(dataSnapshot.getChildrenCount())+"x lit");
                        boostBtn.setColorFilter(mView.getContext().getResources().getColor(R.color.lit));
//                        boostBtn.getBackground().setTint(mView.getContext().getResources().getColor(R.color.lit));
//                        boostBtn.setBackground(ContextCompat.getDrawable(mView.getContext(), R.drawable.curvedradiusbutton2_sr));
                        flag=true;
                    }else {
//                        boostBtn.setText(dataSnapshot.getChildrenCount() + " Boost");
                        eventNumLit.setText(String.valueOf(dataSnapshot.getChildrenCount())+"x lit");
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
                            CounterManager.eventBoost(event.getKey(), "Trending-Out");
                            eventDatabase.child("BoostersUids").updateChildren(taskMap);

                            //Sending Notifications
                            FirebaseMessaging.getInstance().subscribeToTopic(event.getKey().toString());
                            NotificationSender notificationSender=new NotificationSender(event.getKey().toString(),null,event.getEventName(),String.valueOf(System.currentTimeMillis()),null,null,"EventBoosted",false,true,itemView.getContext());
                            notificationSender.execute();

                        }else {
                            eventDatabase.child("BoostersUids").child(user.getUid()).removeValue();

                        }
                    }
                });

            } else {
                boostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(mView.getContext());
                        dialog.setNegativeButton("Lite", null)
                                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent loginIntent = new Intent(mView.getContext(), LoginActivity.class);
                                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        mView.getContext().startActivity(loginIntent);
                                    }
                                })
                                .setTitle("Please login to boost.")
                                .create().show();
                    }
                });
            }

            Typeface customfont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
            eventNumLit.setTypeface(customfont);
        }

        private void setPostedByDetails(String username, String imageThumb) {
//            if(username!= null) {
//                TextView post_postedBy = (TextView) mView.findViewById(R.id.eventPostedBy);
//                SimpleDraweeView post_postedByAvatar = (SimpleDraweeView) mView.findViewById(R.id.eventPostedByAvatar);
//                post_postedBy.setText(username);
//                if(imageThumb != null)
//                {
//                    post_postedByAvatar.setImageURI(imageThumb);
//                }
//                Typeface customFont = Typeface.createFromAsset(mView.getContext().getAssets(), "fonts/Raleway-Regular.ttf");
//                post_postedBy.setTypeface(customFont);
//            }
        }

        private void setEventTimestamp(long postTimeMillis) {
            if(postTimeMillis > 0) {
                TextView timestamp = (TextView) mView.findViewById(R.id.evTrendTimestamp);
                TimeAgo ta = new TimeAgo(postTimeMillis, System.currentTimeMillis());
                timestamp.setText(ta.calculateTimeAgo());
            }
        }

    }


}
