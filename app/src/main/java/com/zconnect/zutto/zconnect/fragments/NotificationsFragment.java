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
import com.zconnect.zutto.zconnect.adapters.NotificationsAdapter;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.itemFormats.NotificationsModel;

import org.joda.time.DateTime;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class NotificationsFragment extends Fragment {

    private RecyclerView notifRecyclerView;
    private ProgressBar progressBar;
    private SharedPreferences communitySP;
    public String communityRef;
    private DatabaseReference notificationsReference;
    private ValueEventListener listener;
    ArrayList<NotificationsModel> notificationsList;
    private NotificationsAdapter notificationsAdapter;

    public NotificationsFragment() {

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
                        System.out.println(childSnapshot);
                        String key = childSnapshot.getKey();
                        String title = childSnapshot.child("title").getValue(String.class);
                        String desc = childSnapshot.child("desc").getValue(String.class);
                        long dateValue = childSnapshot.child("timestamp").getValue(Long.class);
                        DateTime date = new DateTime(dateValue);
                        long type = childSnapshot.child("type").getValue(Long.class);
                        boolean seen = childSnapshot.child("seen").getValue(Boolean.class);
                        HashMap<String, String> metadata = new HashMap<>();
                        for(DataSnapshot grandChildSnapshot : childSnapshot.child("metadata").getChildren()) {
                            metadata.put(grandChildSnapshot.getKey(), grandChildSnapshot.getValue(String.class));
                        }
                        System.out.println("NOTTIF: " + title + " " + desc);
                        NotificationsModel notificationsModel = new NotificationsModel(title, desc, date, (int)type, seen, metadata, key);
                        notificationsList.add(notificationsModel);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

                progressBar.setVisibility(View.GONE);
                notifRecyclerView.setVisibility(View.VISIBLE);
                notificationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        notificationsReference.addValueEventListener(listener);

        notificationsAdapter = new NotificationsAdapter(communityRef, notificationsList, getContext());
        notifRecyclerView.setAdapter(notificationsAdapter);

        return view;
    }
}