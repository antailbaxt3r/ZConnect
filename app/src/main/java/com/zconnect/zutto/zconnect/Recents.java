package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class Recents extends Fragment {

    Vector<RecentsItemFormat> recentsItemFormats = new Vector<>();
    private RecentsRVAdapter adapter;
    @BindView(R.id.recent_rv)
    RecyclerView recyclerView;
    private Query mStoreroomDatabase;
    List<String> storeroomProductList = new ArrayList<String>();
    private DatabaseReference homeDbRef = FirebaseDatabase.getInstance().getReference().child("home");
    Query queryRef = homeDbRef.limitToLast(15);
    private ValueEventListener queryResponseListener;
    @BindView(R.id.recent_progress)
    ProgressBar progressBar;

    public Recents() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recents, container, false);
        ButterKnife.bind(this, view);

        //Reference views---------------------------------------------------------------------------
        //bind by butterKnife

        //MAIN--------------------------------------------------------------------------------------

        //Keep databaseReference in sync even without needing to call valueEventListener
        homeDbRef.keepSynced(true);
        queryRef.keepSynced(true);
        mStoreroomDatabase = FirebaseDatabase.getInstance().getReference().child("storeroom");
        mStoreroomDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    storeroomProductList.add(shot.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        queryResponseListener = new ValueEventListener() {
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
        };

        //setHasFixedSize is used to optimise RV if we know for sure that this view's bounds do not
        // change with data
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager productLinearLayout = new LinearLayoutManager(getContext());
        productLinearLayout.setReverseLayout(true);
        productLinearLayout.setStackFromEnd(true);
        recyclerView.setLayoutManager(productLinearLayout);
        //Setup layout manager. VERY IMP ALWAYS
        adapter = new RecentsRVAdapter(getContext(), recentsItemFormats, (HomeActivity) getActivity(),storeroomProductList);
        recyclerView.setAdapter(adapter);
        return view;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        //queryRef.addValueEventListener(queryResponseListener);
//    }

    @Override
    public void onResume() {
        super.onResume();
        queryRef.addValueEventListener(queryResponseListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        queryRef.removeEventListener(queryResponseListener);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recents, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open_menu_phonebook) {

            Intent infoneIntent = new Intent(getContext(), Infone2Activity.class);
            startActivity(infoneIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
