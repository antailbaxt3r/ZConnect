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
import com.zconnect.zutto.zconnect.pools.adapters.PoolAdapter;
import com.zconnect.zutto.zconnect.pools.adapters.UpcomingPoolAdapter;
import com.zconnect.zutto.zconnect.pools.models.ActivePool;
import com.zconnect.zutto.zconnect.pools.models.Pool;
import com.zconnect.zutto.zconnect.pools.models.UpcomingPool;

/**
 * A simple {@link Fragment} subclass.
 */

public class UpcomingFragment extends Fragment {

    public static final String TAG = "UpcomingFragment";
    private int DUMMYS_NUMBER = 5;

    private RecyclerView recyclerView;
    private PoolAdapter adapter;
    private ChildEventListener activePoolListener;

    private String communityID;


    public UpcomingFragment() {
        // Required empty public constructor
    }

    public static  UpcomingFragment newInstance(String communityID){
        UpcomingFragment frag = new UpcomingFragment();
        frag.communityID = communityID;
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        recyclerView = view.findViewById(R.id.recycleView);
        adapter = new PoolAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        return view;
    }

    public void addPool(Pool pool) {
        adapter.insertAtEnd(pool);

    }

    public void updatePool(Pool pool) {
        adapter.updatePool(pool);
    }

    public void removePool(Pool pool) {
        adapter.removePool(pool);
    }

    public Pool getPool(String id) {
        return adapter.getPool(id);
    }
}
