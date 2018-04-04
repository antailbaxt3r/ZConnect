package com.zconnect.zutto.zconnect.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.InfoneActivity;
import com.zconnect.zutto.zconnect.ItemFormats.InfoneCategories;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookStudentHostelItem;
import com.zconnect.zutto.zconnect.PhonebookStudentHostelRV;
import com.zconnect.zutto.zconnect.R;

import java.util.ArrayList;
import java.util.Vector;


public class InfoneFragment extends Fragment {
    RecyclerView recyclerView;
    Vector<PhonebookStudentHostelItem> phonebookStudentHostelItems;
    int currenttab;
    private ArrayList<InfoneCategories> infoneCategories = new ArrayList<>();
    private PhonebookStudentHostelRV adapter;

    public InfoneFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_infone, container, false);
        Bundle bundle = getArguments();
        currenttab = bundle.getInt("index");
        Log.v("TASDF", String.valueOf(currenttab));
        infoneCategories = InfoneActivity.infoneTabs.get(currenttab).getInfoneCategories();
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_infone_fragment);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        phonebookStudentHostelItems = data();    //Setup layout manager. VERY IMP ALWAYS
        adapter = new PhonebookStudentHostelRV(phonebookStudentHostelItems, getContext());
        recyclerView.setAdapter(adapter);
        return view;
    }

    public Vector<PhonebookStudentHostelItem> data() {
        Vector<PhonebookStudentHostelItem> phonebookStudentHostelItems = new Vector<>();
        for (int i = 0; i < infoneCategories.size(); i++) {
            PhonebookStudentHostelItem phonebookStudentHostelItem1 = new PhonebookStudentHostelItem();
            phonebookStudentHostelItem1.setHostel(infoneCategories.get(i).getTitle());
            phonebookStudentHostelItem1.setCat(InfoneActivity.infoneTabs.get(currenttab).getTitle());
            phonebookStudentHostelItems.add(phonebookStudentHostelItem1);
        }


        return phonebookStudentHostelItems;
    }


}
