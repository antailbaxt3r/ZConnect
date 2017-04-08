package com.zconnect.zutto.zconnect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShopOffersFragment extends Fragment {
    TextView defaultmsg;
    RecyclerView recyclerView;
    String shopid;

    public ShopOffersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shop_offers, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_shop_rv);
        defaultmsg = (TextView) view.findViewById(R.id.shop_errorMessage);
        shopid = getActivity().getIntent().getStringExtra("ShopId");
        return view;
    }

}
