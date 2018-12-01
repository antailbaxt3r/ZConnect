package com.zconnect.zutto.zconnect.pools;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.pools.adapters.PoolAdapter;
import com.zconnect.zutto.zconnect.pools.models.Pool;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveFragment extends Fragment {

    public static final String TAG = "ActiveFragment";
    private int DUMMYS_NUMBER = 5;

    private RecyclerView recyclerView;
    private PoolAdapter adapter;
    private ChildEventListener activePoolListener;

    private String communityID;


    public ActiveFragment() {
        // Required empty public constructor
    }

    public static ActiveFragment newInstance(String communityID) {
        ActiveFragment frag = new ActiveFragment();
        frag.communityID = communityID;
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_active, container, false);
        recyclerView = view.findViewById(R.id.active_pool_rv);
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
