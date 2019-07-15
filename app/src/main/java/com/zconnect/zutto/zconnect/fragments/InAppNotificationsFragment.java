package com.zconnect.zutto.zconnect.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.adapters.InAppNotificationsAdapter;
import com.zconnect.zutto.zconnect.itemFormats.InAppNotificationsItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.RecentsItemFormat;
import com.zconnect.zutto.zconnect.utilities.UserUtilities;
import com.zconnect.zutto.zconnect.utilities.UsersTypeUtilities;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class InAppNotificationsFragment extends Fragment {

    private RecyclerView notifRecyclerView;
    private ProgressBar progressBar;
    private SharedPreferences communitySP;
    public String communityRef;
    private DatabaseReference notificationsReference;
    private DatabaseReference globalReference;
    private ValueEventListener listener;
    private TextView noNotif;
    ArrayList<InAppNotificationsItemFormat> usernotificationsList;
    ArrayList<InAppNotificationsItemFormat> globalnotificationsList;
    ArrayList<InAppNotificationsItemFormat> totalnotificationsList;
    private InAppNotificationsAdapter inAppNotificationsAdapter;

    public InAppNotificationsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications,container,false);
        notifRecyclerView = (RecyclerView) view.findViewById(R.id.rv_notifications_fragment);
        progressBar = (ProgressBar) view.findViewById(R.id.fragment_notifications_progress_circle);
        noNotif = view.findViewById(R.id.noNotif);
        progressBar.setVisibility(View.VISIBLE);

        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityRef = communitySP.getString("communityReference", null);

        globalReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityRef);
        notifRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        totalnotificationsList = new ArrayList<>();
        usernotificationsList = new ArrayList<>();
        globalnotificationsList = new ArrayList<>();
        //Test Notification. Uncomment for testing
        //GlobalFunctions.pushNotifications("Test", "This is a test", false, 1, new HashMap<String, String>());
            Log.d("outside", "onDataChange: ");
            inAppNotificationsAdapter = new InAppNotificationsAdapter(getContext(), communityRef, totalnotificationsList);
            notifRecyclerView.setAdapter(inAppNotificationsAdapter);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("inapresume", "onResume: ");
        if(UserUtilities.currentUser!= null) {
            if (!UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_NOT_VERIFIED) || !UserUtilities.currentUser.getUserType().equals(UsersTypeUtilities.KEY_PENDING)) {
                globalReference.child("globalNotifications").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        totalnotificationsList.clear();
                        globalnotificationsList.clear();
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            InAppNotificationsItemFormat globalnotification = childSnapshot.getValue(InAppNotificationsItemFormat.class);
                            if (globalnotification != null) {
                                globalnotificationsList.add(globalnotification);
                            }
                        }

                        totalnotificationsList.addAll(globalnotificationsList);
                        totalnotificationsList.addAll(usernotificationsList);
                        Collections.sort(totalnotificationsList, new Comparator<InAppNotificationsItemFormat>() {
                            @Override
                            public int compare(InAppNotificationsItemFormat o1, InAppNotificationsItemFormat o2) {
                                return Long.valueOf((Long) o2.getPostTimeMillis()).compareTo((Long) o1.getPostTimeMillis());
                            }
                        });
                        progressBar.setVisibility(View.GONE);
                        notifRecyclerView.setVisibility(View.VISIBLE);
                        if (!totalnotificationsList.isEmpty()) {
                            noNotif.setVisibility(View.GONE);
                            notifRecyclerView.setVisibility(View.VISIBLE);
                            inAppNotificationsAdapter.notifyDataSetChanged();

                        } else {
                            noNotif.setVisibility(View.VISIBLE);
                            notifRecyclerView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
            globalReference.child("Users1").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("notifications").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    totalnotificationsList.clear();
                    usernotificationsList.clear();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        InAppNotificationsItemFormat usernotification = childSnapshot.getValue(InAppNotificationsItemFormat.class);
                        if(usernotification!=null)
                        usernotificationsList.add(usernotification);
                    }
                    totalnotificationsList.addAll(globalnotificationsList);
                    totalnotificationsList.addAll(usernotificationsList);
                    Collections.sort(totalnotificationsList, new Comparator<InAppNotificationsItemFormat>() {
                        @Override
                        public int compare(InAppNotificationsItemFormat o1, InAppNotificationsItemFormat o2) {
                            return Long.valueOf((Long) o2.getPostTimeMillis()).compareTo((Long) o1.getPostTimeMillis());
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    notifRecyclerView.setVisibility(View.VISIBLE);
                    if(!totalnotificationsList.isEmpty()) {
                        noNotif.setVisibility(View.GONE);
                        notifRecyclerView.setVisibility(View.VISIBLE);
                        inAppNotificationsAdapter.notifyDataSetChanged();
                    } else {
                        noNotif.setVisibility(View.VISIBLE);
                        notifRecyclerView.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


    }
}