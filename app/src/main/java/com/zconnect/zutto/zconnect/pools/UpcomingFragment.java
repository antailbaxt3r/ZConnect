package com.zconnect.zutto.zconnect.pools;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolAdapter;
import com.zconnect.zutto.zconnect.pools.models.Pool;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */

public class UpcomingFragment extends Fragment {

    public static final String TAG = "UpcomingFragment";
    private int DUMMYS_NUMBER = 5;

    private RecyclerView recyclerView;
    private PoolAdapter adapter;
    private ValueEventListener upcomingPoolListener;

    private String communityID;


    public UpcomingFragment() {
        // Required empty public constructor
    }

    public static UpcomingFragment newInstance(String communityID) {
        UpcomingFragment frag = new UpcomingFragment();
        frag.communityID = communityID;
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void loadPoolList() {
        Query query = FirebaseDatabase.getInstance().getReference(String.format(Pool.URL_POOL, communityID)).orderByChild(Pool.STATUS).equalTo(Pool.STATUS_UPCOMING);
        query.addValueEventListener(upcomingPoolListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        recyclerView = view.findViewById(R.id.recycleView);
        adapter = new PoolAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        defineListener();
        loadPoolList();


        return view;
    }

    private void defineListener() {
        upcomingPoolListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Pool> arrayList = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Pool newPool = child.getValue(Pool.class);
                    newPool.setID(child.getKey());
                    arrayList.add(newPool);
                }
                adapter.addAll(arrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
