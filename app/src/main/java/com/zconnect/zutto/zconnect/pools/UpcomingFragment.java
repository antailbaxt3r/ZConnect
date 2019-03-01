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

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

import java.util.ArrayList;

public class UpcomingFragment extends Fragment {

    public static final String TAG = "UpcomingFragment";
    private int DUMMYS_NUMBER = 5;

    private RecyclerView recyclerView;
    private PoolAdapter adapter;
    private ValueEventListener upcomingPoolListener;
    private TextView noPools;


    public UpcomingFragment() {
        // Required empty public constructor
    }

    public static UpcomingFragment newInstance() {
        UpcomingFragment frag = new UpcomingFragment();
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void loadPoolList() {
        Query query = FirebaseDatabase.getInstance().getReference(String.format(Pool.URL_POOL, communityReference)).orderByChild(Pool.STATUS).equalTo(Pool.STATUS_UPCOMING);
        query.addValueEventListener(upcomingPoolListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);
        recyclerView = view.findViewById(R.id.recycleView);
        noPools = view.findViewById(R.id.no_upcoming_pools);
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

    private void defineListener() {
        upcomingPoolListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Pool> poolArrayList = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Pool newPool = child.getValue(Pool.class);
                    if (newPool.isUpcoming()) {
                        poolArrayList.add(newPool);
                    }
                }
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
