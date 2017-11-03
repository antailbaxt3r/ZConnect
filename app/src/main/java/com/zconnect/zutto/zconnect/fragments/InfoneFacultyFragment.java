package com.zconnect.zutto.zconnect.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zconnect.zutto.zconnect.ItemFormats.PhonebookStudentHostelItem;
import com.zconnect.zutto.zconnect.PhonebookStudentHostelRV;
import com.zconnect.zutto.zconnect.R;

import java.util.Vector;


public class InfoneFacultyFragment extends Fragment {

    Vector<PhonebookStudentHostelItem> phonebookStudentHostelItems;
    private PhonebookStudentHostelRV adapter;
    private RecyclerView recyclerView;
    private String dept[] = {"CS", "EEE/INSTR", "MECH", "CHEM-ENGG", "BIO", "MATH", "PHY", "CHEM", "ECO", "HUMANITIES"};

    public InfoneFacultyFragment() {
//         Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Create Fragment view
        View view = inflater.inflate(R.layout.fragment_phonebook_admin, container, false);

        //Reference views---------------------------------------------------------------------------
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_infone_faculty);

        //MAIN--------------------------------------------------------------------------------------

        //setHasFixedSize is used to optimise RV if we know for sure that this view's bounds do not
        // change with data
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        phonebookStudentHostelItems = data();    //Setup layout manager. VERY IMP ALWAYS
        adapter = new PhonebookStudentHostelRV(phonebookStudentHostelItems, getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }

    public Vector<PhonebookStudentHostelItem> data() {
        Vector<PhonebookStudentHostelItem> phonebookStudentHostelItems = new Vector<>();
        for (int i = 0; i < 10; i++) {
            PhonebookStudentHostelItem phonebookStudentHostelItem1 = new PhonebookStudentHostelItem();
            phonebookStudentHostelItem1.setHostel(dept[i]);
            phonebookStudentHostelItem1.setCat("A");
            phonebookStudentHostelItems.add(phonebookStudentHostelItem1);
        }
        return phonebookStudentHostelItems;
    }
}
