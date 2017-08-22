package com.zconnect.zutto.zconnect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import com.zconnect.zutto.zconnect.ItemFormats.RecentsItemFormat;

import java.util.Vector;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Recents extends Fragment {

    Vector<RecentsItemFormat> recentsItemFormats = new Vector<>();
    private RecentsRVAdapter adapter;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("home");
    Query queryRef = databaseReference.limitToLast(15);
    private ProgressBar progressBar;

    public Recents() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recents, container, false);

        //Reference views---------------------------------------------------------------------------
        recyclerView = (RecyclerView) view.findViewById(R.id.recent_rv);
        progressBar = (ProgressBar) view.findViewById(R.id.recent_progress);

        //MAIN--------------------------------------------------------------------------------------

        //Keep databaseReference in sync even without needing to call valueEventListener
        databaseReference.keepSynced(true);
        queryRef.keepSynced(true);

        //setHasFixedSize is used to optimise RV if we know for sure that this view's bounds do not
        // change with data
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager productLinearLayout = new LinearLayoutManager(getContext());
        productLinearLayout.setReverseLayout(true);
        productLinearLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(productLinearLayout);
        //Setup layout manager. VERY IMP ALWAYS
        adapter = new RecentsRVAdapter(getContext(), recentsItemFormats, (HomeActivity) getActivity());
        recyclerView.setAdapter(adapter);


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(VISIBLE);
                recentsItemFormats.clear();

                for (DataSnapshot shot : dataSnapshot.getChildren()) {

                    recentsItemFormats.add(shot.getValue(RecentsItemFormat.class));
                }


                adapter.notifyDataSetChanged();
                progressBar.setVisibility(INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(INVISIBLE);
            }
        });


    }

}
