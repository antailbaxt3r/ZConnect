package com.zconnect.zutto.zconnect.pools;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolAdapter;
import com.zconnect.zutto.zconnect.pools.models.Pool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class ActiveFragment extends Fragment {

    public static final String TAG = "ActiveFragment";

    private RecyclerView recyclerView;
    private PoolAdapter adapter;
    private ValueEventListener activePoolListener;
    private TextView noPools;

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
        noPools = view.findViewById(R.id.no_active_pools);
        adapter = new PoolAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        defineListener();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPoolList();
    }

    private void loadPoolList() {
        Query query = FirebaseDatabase.getInstance().getReference(String.format(Pool.URL_POOL, communityReference)).orderByChild(Pool.STATUS).equalTo(Pool.STATUS_ACTIVE);
        query.addValueEventListener(activePoolListener);
        query.keepSynced(true);
    }

    private void defineListener() {
        activePoolListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Pool> poolArrayList = new ArrayList<>();


                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    if (child.hasChild(Pool.POOL_INFO)) {
                        Pool newPool = child.getValue(Pool.class);
                        if (newPool.isActive())
                            poolArrayList.add(newPool);

                    }
                }

                Collections.sort(poolArrayList, new Comparator<Pool>() {
                    @Override
                    public int compare(Pool pool1, Pool pool2) {
                        return (int)(pool1.getTimestampOrderReceivingDeadline() - pool2.getTimestampOrderReceivingDeadline());
                    }
                });
                if(poolArrayList.size()>0)
                    noPools.setVisibility(View.GONE);
                else
                    noPools.setVisibility(View.VISIBLE);
                adapter.addAll(poolArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
