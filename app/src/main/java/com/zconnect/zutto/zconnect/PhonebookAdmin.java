package com.zconnect.zutto.zconnect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookItem;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookStudentHostelItem;

import java.util.Vector;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class PhonebookAdmin extends Fragment {

    Vector<PhonebookStudentHostelItem> phonebookStudentHostelItems;
    private PhonebookStudentHostelRV adapter;
    private RecyclerView recyclerView;
    private String dept[] = {"CS", "EEE/INSTR", "MECH", "CHEM-ENGG", "BIO", "MATH", "PHY", "CHEM" ,"ECO", "SWD", "ADMIN", "ARC"};

//    Vector<PhonebookItem> phonebookItems = new Vector<>();
//    Vector<PhonebookDisplayItem> phonebookDisplayItems = new Vector<>();
//    private PhonebookAdapter adapter;
//    private RecyclerView recyclerView;
//    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Phonebook");
//    Query queryRef = databaseReference.orderByChild("name");
//    private ProgressBar progressBar;

    public PhonebookAdmin() {
//         Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Create Fragment view
        View view = inflater.inflate(R.layout.fragment_phonebook_admin, container, false);

        //Reference views---------------------------------------------------------------------------
        recyclerView = (RecyclerView) view.findViewById(R.id.admin_phone_rv);
//        progressBar = (ProgressBar) view.findViewById(R.id.admin_phone_progress);

        //MAIN--------------------------------------------------------------------------------------

        //Keep databaseReference in sync even without needing to call valueEventListener
//        databaseReference.keepSynced(true);
//        queryRef.keepSynced(true);

        //setHasFixedSize is used to optimise RV if we know for sure that this view's bounds do not
        // change with data
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        phonebookStudentHostelItems = data();    //Setup layout manager. VERY IMP ALWAYS
        adapter = new PhonebookStudentHostelRV(phonebookStudentHostelItems, getContext());
        recyclerView.setAdapter(adapter);


        return view;

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        queryRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                progressBar.setVisibility(VISIBLE);
//                phonebookItems.clear();
//                phonebookDisplayItems.clear();
//                for (DataSnapshot shot : dataSnapshot.getChildren()) {
//
//                    phonebookDisplayItems.add(shot.getValue(PhonebookDisplayItem.class));
//                }
//                for (int i = 0; i < phonebookDisplayItems.size(); i++) {
//                    if (phonebookDisplayItems.get(i).getCategory() != null && phonebookDisplayItems.get(i).getCategory().equals("A")) {
//                        phonebookItems.add(new PhonebookItem(phonebookDisplayItems.get(i).getImageurl(), phonebookDisplayItems.get(i).getName(), phonebookDisplayItems.get(i).getNumber(), phonebookDisplayItems.get(i)));
//                    }
//
//                }
//                adapter.notifyDataSetChanged();
//                progressBar.setVisibility(INVISIBLE);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                progressBar.setVisibility(INVISIBLE);
//            }
//        });
//
//
//    }

    public Vector<PhonebookStudentHostelItem> data() {
        Vector<PhonebookStudentHostelItem> phonebookStudentHostelItems = new Vector<>();
        for (int i = 0; i < 12; i++) {
            PhonebookStudentHostelItem phonebookStudentHostelItem1 = new PhonebookStudentHostelItem();
            phonebookStudentHostelItem1.setHostel(dept[i]);
            phonebookStudentHostelItems.add(phonebookStudentHostelItem1);
        }
        return phonebookStudentHostelItems;
    }
}
