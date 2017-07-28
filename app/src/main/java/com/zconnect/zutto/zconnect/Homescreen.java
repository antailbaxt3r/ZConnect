package com.zconnect.zutto.zconnect;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;


public class Homescreen extends Fragment {
    public String type = null;
    TextView InfoneStats;
    TextView InfoneName;
    TextView InfoneStatement;
    TextView ShopsStats;
    TextView ShopsName;
    TextView ShopsStatement;
    TextView StoreRoomStats;
    TextView StoreRoomName;
    TextView StoreRoomStatement;
    TextView EventsStats;
    TextView EventsName;
    TextView EventsStatement;
    FirebaseUser user;
    FirebaseAuth mAuth;
    String userId;
    GridLayout Grid;
    DatabaseReference mDatabaseUser, mDatabaseStats;
    int UserTotalNumbers = 0, TotalNumbers = 0;
    int UserTotalProducts = 0, TotalProducts = 0;
    int UserTotalOffers = 0, TotalOffers = 0;
    int UserTotalEvents = 0, TotalEvents = 0;
    Boolean flag;
    ValueEventListener UserStats;
    ValueEventListener TotalStats;
    LinearLayout InfoneCard, StoreRoomCard, EventsCard, ShopsCard;
    @SuppressLint("ValidFragment")
    public Homescreen() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homescreen, container, false);

        InfoneCard = (LinearLayout) view.findViewById(R.id.InfoneCard);
        StoreRoomCard = (LinearLayout) view.findViewById(R.id.StoreRoomCard);
        EventsCard = (LinearLayout) view.findViewById(R.id.EventsCard);
        ShopsCard = (LinearLayout) view.findViewById(R.id.ShopsCard);


        InfoneStats = (TextView) view.findViewById(R.id.InfoneStats);
        InfoneName = (TextView) view.findViewById(R.id.InfoneName);
        InfoneStatement = (TextView) view.findViewById(R.id.InfoneStatement);

        ShopsStats = (TextView) view.findViewById(R.id.ShopsStats);
        ShopsName = (TextView) view.findViewById(R.id.ShopsName);
        ShopsStatement = (TextView) view.findViewById(R.id.ShopsStatement);

        EventsStats = (TextView) view.findViewById(R.id.EventsStats);
        EventsName = (TextView) view.findViewById(R.id.EventsName);
        EventsStatement = (TextView) view.findViewById(R.id.EventsStatement);

        StoreRoomStats = (TextView) view.findViewById(R.id.StoreRoomStats);
        StoreRoomName = (TextView) view.findViewById(R.id.StoreRoomName);
        StoreRoomStatement = (TextView) view.findViewById(R.id.StoreRoomStatement);

        SharedPreferences sharedPref = getContext().getSharedPreferences("guestMode", Context.MODE_PRIVATE);
        final Boolean status = sharedPref.getBoolean("mode", false);
        //Toast.makeText(getContext(), status.toString(), Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();

            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Stats");
            mDatabaseStats = FirebaseDatabase.getInstance().getReference().child("Stats");
        }


        InfoneCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CounterManager.InfoneOpen();
                Intent InfoneIntent = new Intent(getContext(), Phonebook.class);
                startActivity(InfoneIntent);
            }
        });



        StoreRoomCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CounterManager.StoreRoomOpen();
                Intent InfoneIntent = new Intent(getContext(), TabStoreRoom.class);
                startActivity(InfoneIntent);
            }
        });

        EventsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CounterManager.EventOpen();
                Intent InfoneIntent = new Intent(getContext(), AllEvents.class);
                startActivity(InfoneIntent);
            }
        });

        ShopsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CounterManager.ShopOpen();
                Intent InfoneIntent = new Intent(getContext(), Shop.class);
                startActivity(InfoneIntent);
            }
        });

        TotalStats = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TotalNumbers = Integer.parseInt(dataSnapshot.child("TotalNumbers").getValue().toString());
                TotalEvents = Integer.parseInt(dataSnapshot.child("TotalEvents").getValue().toString());
                TotalOffers = Integer.parseInt(dataSnapshot.child("TotalOffers").getValue().toString());
                TotalProducts = Integer.parseInt(dataSnapshot.child("TotalProducts").getValue().toString());
                setNotification();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        if(user!=null) {
            UserStats = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("TotalNumbers").getValue() != null) {
                        UserTotalNumbers = Integer.parseInt(dataSnapshot.child("TotalNumbers").getValue().toString());
                    }
                    if (dataSnapshot.child("TotalEvents").getValue() != null) {
                        UserTotalEvents = Integer.parseInt(dataSnapshot.child("TotalEvents").getValue().toString());
                    }
                    if (dataSnapshot.child("TotalOffers").getValue() != null) {
                        UserTotalOffers = Integer.parseInt(dataSnapshot.child("TotalOffers").getValue().toString());
                    }
                    if (dataSnapshot.child("TotalProducts").getValue() != null) {
                        UserTotalProducts = Integer.parseInt(dataSnapshot.child("TotalProducts").getValue().toString());
                    }
                    setNotification();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            addContactOption();
        }

        //changing fonts
        Typeface ralewayExtraLight = Typeface.createFromAsset(getContext().getAssets(), "fonts/Raleway-ExtraLight.ttf");
        InfoneName.setTypeface(ralewayExtraLight);
        InfoneStats.setTypeface(ralewayExtraLight);
        InfoneStatement.setTypeface(ralewayExtraLight);
        EventsName.setTypeface(ralewayExtraLight);
        EventsStats.setTypeface(ralewayExtraLight);
        EventsStatement.setTypeface(ralewayExtraLight);
        ShopsName.setTypeface(ralewayExtraLight);
        ShopsStats.setTypeface(ralewayExtraLight);
        ShopsStatement.setTypeface(ralewayExtraLight);
        StoreRoomName.setTypeface(ralewayExtraLight);
        StoreRoomStats.setTypeface(ralewayExtraLight);
        StoreRoomStatement.setTypeface(ralewayExtraLight);
        return view;
    }

    void  addContactOption() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Phonebook");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean exists = false;
                for (DataSnapshot shot : dataSnapshot.getChildren()) {

                    PhonebookDisplayItem phonebookDisplayItem = shot.getValue(PhonebookDisplayItem.class);

                    if (mAuth.getCurrentUser().getEmail()!=null&&phonebookDisplayItem.getEmail()!=null&&phonebookDisplayItem.getEmail().equals(mAuth.getCurrentUser().getEmail())) {
                        exists= true;
                    }

                }
                if (!exists)
                {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Add your contact");
                    alertBuilder.setMessage("Welcome to ZConnect! , Add your contact on ZConnect");
                    alertBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent addContact = new Intent(getContext(), AddContact.class);
                            addContact.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(addContact);
                        }
                    });
                    alertBuilder.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        }


    @Override
    public void onStart() {
        super.onStart();
        if (isNetworkAvailable(getContext())&&user!=null) {
            mDatabaseStats.addValueEventListener(TotalStats);
            mDatabaseUser.addValueEventListener(UserStats);
        } else {
            setNotification();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (user != null) {
            mDatabaseUser.removeEventListener(UserStats);
            mDatabaseStats.removeEventListener(TotalStats);
        }
    }

    public void setNotification() {

//
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //write your code here to be executed after 1 second

        if (UserTotalNumbers < TotalNumbers) {


            InfoneName.setVisibility(View.INVISIBLE);
            InfoneStats.setVisibility(View.VISIBLE);
            InfoneStatement.setVisibility(View.VISIBLE);

            ValueAnimator animator1 = new ValueAnimator();
            animator1.setObjectValues(0, TotalNumbers - UserTotalNumbers);
            animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    InfoneStats.setText(String.valueOf(animation.getAnimatedValue()));
                }
            });
            animator1.setEvaluator(new TypeEvaluator<Integer>() {
                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                    return Math.round(startValue + (endValue - startValue) * fraction);
                }
            });
            animator1.setDuration(1000);
            animator1.start();
        } else {
            InfoneName.setVisibility(View.VISIBLE);
            InfoneStats.setVisibility(View.INVISIBLE);
            InfoneStatement.setVisibility(View.INVISIBLE);

        }

        if (UserTotalProducts < TotalProducts) {

            StoreRoomName.setVisibility(View.INVISIBLE);
            StoreRoomStats.setVisibility(View.VISIBLE);
            StoreRoomStatement.setVisibility(View.VISIBLE);

            ValueAnimator animator2 = new ValueAnimator();
            animator2.setObjectValues(0, TotalProducts - UserTotalProducts);
            animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    StoreRoomStats.setText(String.valueOf(animation.getAnimatedValue()));
                }
            });
            animator2.setEvaluator(new TypeEvaluator<Integer>() {
                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                    return Math.round(startValue + (endValue - startValue) * fraction);
                }
            });
            animator2.setDuration(1000);
            animator2.start();

        } else {
            StoreRoomName.setVisibility(View.VISIBLE);
            StoreRoomStats.setVisibility(View.INVISIBLE);
            StoreRoomStatement.setVisibility(View.INVISIBLE);
        }

        if (UserTotalEvents < TotalEvents) {

            EventsName.setVisibility(View.INVISIBLE);
            EventsStats.setVisibility(View.VISIBLE);
            EventsStatement.setVisibility(View.VISIBLE);

            ValueAnimator animator3 = new ValueAnimator();
            animator3.setObjectValues(0, TotalEvents - UserTotalEvents);
            animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    EventsStats.setText(String.valueOf(animation.getAnimatedValue()));
                }
            });
            animator3.setEvaluator(new TypeEvaluator<Integer>() {
                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                    return Math.round(startValue + (endValue - startValue) * fraction);
                }
            });
            animator3.setDuration(1000);
            animator3.start();

        } else {
            EventsName.setVisibility(View.VISIBLE);
            EventsStats.setVisibility(View.INVISIBLE);
            EventsStatement.setVisibility(View.INVISIBLE);
        }

        if (UserTotalOffers < TotalOffers) {

            ShopsName.setVisibility(View.INVISIBLE);
            ShopsStats.setVisibility(View.VISIBLE);
            ShopsStatement.setVisibility(View.VISIBLE);
            ValueAnimator animator4 = new ValueAnimator();
            animator4.setObjectValues(0, TotalOffers - UserTotalOffers);
            animator4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    ShopsStats.setText(String.valueOf(animation.getAnimatedValue()));
                }
            });

            animator4.setEvaluator(new TypeEvaluator<Integer>() {
                public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                    return Math.round(startValue + (endValue - startValue) * fraction);
                }
            });
            animator4.setDuration(1000);
            animator4.start();
        } else {
            ShopsName.setVisibility(View.VISIBLE);
            ShopsStats.setVisibility(View.INVISIBLE);
            ShopsStatement.setVisibility(View.INVISIBLE);
        }
//            }
//        }, 1000);
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
