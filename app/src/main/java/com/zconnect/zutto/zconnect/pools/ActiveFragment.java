package com.zconnect.zutto.zconnect.pools;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        for (int i = 0; i < DUMMYS_NUMBER; i++) {
            adapter.insertAtEnd(ActivePool.dummyValues());
        }


        return view;
    }

}
