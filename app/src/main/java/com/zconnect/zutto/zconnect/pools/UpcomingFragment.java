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
import com.zconnect.zutto.zconnect.pools.adapters.UpcomingPoolAdapter;
import com.zconnect.zutto.zconnect.pools.models.UpcomingPool;

/**
 * A simple {@link Fragment} subclass.
 */

public class UpcomingFragment extends Fragment {

    public static final String TAG = "UpcomingFragment";
    private int DUMMYS_NUMBER = 5;

    private RecyclerView recyclerView;
    private UpcomingPoolAdapter adapter;


    public UpcomingFragment() {
        // Required empty public constructor
    }

    public static  UpcomingFragment newInstance(){
        UpcomingFragment frag = new UpcomingFragment();
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
        recyclerView = view.findViewById(R.id.upcoming_pool_rv);
        adapter = new UpcomingPoolAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        for(int i = 0 ;i < DUMMYS_NUMBER ; i++){
            adapter.insertAtEnd(UpcomingPool.dummyValues());
        }


        return view;
    }
}
