package com.zconnect.zutto.zconnect.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.adapters.InAppNotificationsAdapter;
import com.zconnect.zutto.zconnect.itemFormats.InAppNotificationsItemFormat;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class InAppNotificationsFragment extends Fragment {

    private RecyclerView notifRecyclerView;
    private ProgressBar progressBar;
    private SharedPreferences communitySP;
    public String communityRef;
    private DatabaseReference notificationsReference;
    private ValueEventListener listener;
    ArrayList<InAppNotificationsItemFormat> notificationsList;
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

        notifRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityRef = communitySP.getString("communityReference", null);

        notificationsReference = FirebaseDatabase.getInstance().getReference().child("communities")
                .child(communityRef).child("Users1")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("notifications");

        notifRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

        notificationsList = new ArrayList<>();

        //Test Notification. Uncomment for testing
        //GlobalFunctions.pushNotifications("Test", "This is a test", false, 1, new HashMap<String, String>());

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    try {
                        InAppNotificationsItemFormat inAppNotificationsItemFormat = childSnapshot.getValue(InAppNotificationsItemFormat.class);

                        notificationsList.add(inAppNotificationsItemFormat);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

                progressBar.setVisibility(View.GONE);
                notifRecyclerView.setVisibility(View.VISIBLE);
                inAppNotificationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        notificationsReference.addValueEventListener(listener);

        inAppNotificationsAdapter = new InAppNotificationsAdapter(getContext(), communityRef, notificationsList);
        notifRecyclerView.setAdapter(inAppNotificationsAdapter);

        return view;
    }
}