package com.zconnect.zutto.zconnect;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.LinksRVAdapter;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.ListItem;
import com.zconnect.zutto.zconnect.itemFormats.Product;

import java.util.HashMap;
import java.util.Vector;

public class Links extends BaseActivity {

    ValueEventListener mValueEventListener ;
    private  boolean flagNoLinks;
    LinksRVAdapter  linksRVAdapter;
    DatabaseReference linkReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_links);

        final Vector<ListItem> LinksList=new Vector<ListItem>();


        linkReference= FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("links");

        mValueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinksList.clear();

                ListItem singleProduct=new ListItem();
                for (DataSnapshot shot: dataSnapshot.getChildren()){
                    try{
                        singleProduct = shot.getValue(ListItem.class);
                        if(singleProduct.getLinkURL()!=null&& singleProduct.getTitle()!=null) {
                            LinksList.add(singleProduct);

                        }
                    }
                    catch (Exception e){
                        Log.d("Error Alert", e.getMessage());
                    }
                }

                linksRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        RecyclerView links=(RecyclerView)findViewById(R.id.menu_links);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        links.setLayoutManager(linearLayoutManager);
        linksRVAdapter=new LinksRVAdapter(LinksList,Links.this);
        links.setAdapter(linksRVAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        linkReference.addValueEventListener(mValueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        linkReference.removeEventListener(mValueEventListener);
    }
}
