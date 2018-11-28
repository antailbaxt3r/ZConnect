package com.zconnect.zutto.zconnect.pools;


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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.ActivePoolAdapter;
import com.zconnect.zutto.zconnect.pools.models.ActivePool;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveFragment extends Fragment {

    public static final String TAG = "ActiveFragment";
    private int DUMMYS_NUMBER = 5;

    private RecyclerView recyclerView;
    private ActivePoolAdapter adapter;
    private ChildEventListener activePoolListener;

    private String community_name;


    public ActiveFragment() {
        // Required empty public constructor
    }

    public static ActiveFragment newInstance() {
        ActiveFragment frag = new ActiveFragment();
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_active, container, false);
        recyclerView = view.findViewById(R.id.active_pool_rv);
        adapter = new ActivePoolAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //TODO set proper datat from the preference
        community_name = "testCollege";
        defineListener();
        loadActiveList();

        return view;
    }

    private void defineListener() {
        activePoolListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ActivePool newPool = dataSnapshot.getValue(ActivePool.class);
                newPool.setID(dataSnapshot.getKey());
                adapter.insertAtEnd(newPool);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ActivePool newPool = dataSnapshot.getValue(ActivePool.class);
                newPool.setID(dataSnapshot.getKey());
                adapter.updatePool(newPool);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                ActivePool newPool = dataSnapshot.getValue(ActivePool.class);
                newPool.setID(dataSnapshot.getKey());
                adapter.removePool(newPool);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //TODO on cancel
            }
        };
    }

    private void loadActiveList() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(String.format(ActivePool.URL_ACTIVE_POOL,community_name));
        Log.d(TAG,"loadActiveList : ref "+ref.toString());
        ref.addChildEventListener(activePoolListener);
    }

}
