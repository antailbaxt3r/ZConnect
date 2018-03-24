package com.zconnect.zutto.zconnect;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookStudentHostelItem;

import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;


public class PhonebookOthersCategories extends Fragment {
    private Vector<PhonebookStudentHostelItem> phonebookStudentHostelItems = new Vector<>();
    private PhonebookStudentHostelRV adapter;
    private RecyclerView recyclerView;

    public PhonebookOthersCategories() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Create Fragment view
        View view = inflater.inflate(R.layout.fragment_phonebook_students, container, false);

        //Reference views---------------------------------------------------------------------------
        recyclerView = (RecyclerView) view.findViewById(R.id.student_phone_rv);


        //MAIN--------------------------------------------------------------------------------------


        //setHasFixedSize is used to optimise RV if we know for sure that this view's bounds do not
        // change with data
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new PhonebookStudentHostelRV(phonebookStudentHostelItems, getContext());
        recyclerView.setAdapter(adapter);
        data();


        return view;
    }

    public void data() {
        SharedPreferences communitySP;
        String communityReference;

        communitySP = getActivity().getSharedPreferences("communityName", MODE_PRIVATE);
        communityReference = communitySP.getString("communityReference", null);
        FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("PhonebookOtherCategories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phonebookStudentHostelItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PhonebookStudentHostelItem phonebookStudentHostelItem1 = new PhonebookStudentHostelItem();
                    phonebookStudentHostelItem1.setHostel(snapshot.getValue(String.class));
                    phonebookStudentHostelItem1.setCat("O");
                    phonebookStudentHostelItems.add(phonebookStudentHostelItem1);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
