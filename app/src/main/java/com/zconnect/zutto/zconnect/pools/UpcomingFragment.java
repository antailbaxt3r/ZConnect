package com.zconnect.zutto.zconnect.pools;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.R;

/**
 * A simple {@link Fragment} subclass.
 */

public class UpcomingFragment extends Fragment {


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcoming, container, false);
    }
}
