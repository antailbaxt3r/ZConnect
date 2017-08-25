package com.zconnect.zutto.zconnect;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Homescreen extends Fragment implements FirebaseAuth.AuthStateListener, View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    public String type = null;

    @BindView(R.id.InfoneStats)
    TextView InfoneStats;
    @BindView(R.id.InfoneName)
    TextView InfoneName;
    @BindView(R.id.InfoneStatement)
    TextView InfoneStatement;

    @BindView(R.id.ShopsStats)
    TextView ShopsStats;
    @BindView(R.id.ShopsName)
    TextView ShopsName;
    @BindView(R.id.ShopsStatement)
    TextView ShopsStatement;

    @BindView(R.id.StoreRoomStats)
    TextView StoreRoomStats;
    @BindView(R.id.StoreRoomName)
    TextView StoreRoomName;
    @BindView(R.id.StoreRoomStatement)
    TextView StoreRoomStatement;

    @BindView(R.id.EventsStats)
    TextView EventsStats;
    @BindView(R.id.EventsName)
    TextView EventsName;
    @BindView(R.id.EventsStatement)
    TextView EventsStatement;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @BindView(R.id.cabpool)
    ImageView carpool;

    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseStats;
    int UserTotalNumbers = 0, TotalNumbers = 0;
    int UserTotalProducts = 0, TotalProducts = 0;
    int UserTotalOffers = 0, TotalOffers = 0;
    int UserTotalEvents = 0, TotalEvents = 0;

    private ValueEventListener UserStats;
    private ValueEventListener TotalStats;
    @BindView(R.id.InfoneCard)
    LinearLayout InfoneCard;
    @BindView(R.id.StoreRoomCard)
    LinearLayout StoreRoomCard;
    @BindView(R.id.EventsCard)
    LinearLayout EventsCard;
    @BindView(R.id.ShopsCard)
    LinearLayout ShopsCard;

    public Homescreen() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homescreen, container, false);
        ButterKnife.bind(this, view); //now all views are bind

        carpool.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        //https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseAuth.AuthStateListener
        mAuth.addAuthStateListener(this);
        InfoneCard.setOnClickListener(this);
        StoreRoomCard.setOnClickListener(this);
        EventsCard.setOnClickListener(this);
        ShopsCard.setOnClickListener(this);
        initListeners();

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

    /**
     * All {@link ValueEventListener}s for this class are defined here.
     */
    private void initListeners() {
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
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        };
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
                Log.e(TAG, "onCancelled: ", databaseError.toException());
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isNetworkAvailable(getContext()) && user != null) {
            mDatabaseStats.addValueEventListener(TotalStats);
            mDatabaseUser.addValueEventListener(UserStats);
        } else {
            setNotification();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mDatabaseUser != null) mDatabaseUser.removeEventListener(UserStats);
        if (mDatabaseStats != null) mDatabaseStats.removeEventListener(TotalStats);
    }

    public void setNotification() {
        if (UserTotalNumbers < TotalNumbers) {
            InfoneName.setVisibility(View.GONE);
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
            InfoneStats.setVisibility(View.GONE);
            InfoneStatement.setVisibility(View.GONE);
        }

        if (UserTotalProducts < TotalProducts) {
            StoreRoomName.setVisibility(View.GONE);
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
            StoreRoomStatement.setVisibility(View.GONE);
        }

        if (UserTotalEvents < TotalEvents) {

            EventsName.setVisibility(View.GONE);
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
            EventsStats.setVisibility(View.GONE);
            EventsStatement.setVisibility(View.GONE);
        }

        if (UserTotalOffers < TotalOffers) {

            ShopsName.setVisibility(View.GONE);
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
            ShopsStats.setVisibility(View.GONE);
            ShopsStatement.setVisibility(View.GONE);
        }
    }

    //TODO: create base fragment for such methods
    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        user = mAuth.getCurrentUser();
        if (user != null) {
            mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Stats");
            mDatabaseStats = FirebaseDatabase.getInstance().getReference().child("Stats");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cabpool: {
                Intent intent = new Intent(getContext(), CabPooling.class);
                startActivity(intent);
                break;
            }
            case R.id.InfoneCard: {
                CounterManager.InfoneOpen();
                Intent InfoneIntent = new Intent(getContext(), InfoneActivity.class);
                startActivity(InfoneIntent);
                break;
            }
            case R.id.StoreRoomCard: {
                CounterManager.StoreRoomOpen();
                Intent InfoneIntent = new Intent(getContext(), TabStoreRoom.class);
                startActivity(InfoneIntent);
                break;
            }
            case R.id.EventsCard: {
                CounterManager.EventOpen();
                Intent eventsIntent = new Intent(getContext(), AllEvents.class);
                startActivity(eventsIntent);
                break;
            }
            case R.id.ShopsCard: {
                CounterManager.ShopOpen();
                Intent InfoneIntent = new Intent(getContext(), Shop.class);
                startActivity(InfoneIntent);
                break;
            }
        }
    }
}
