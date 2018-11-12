package com.zconnect.zutto.zconnect;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.adapters.NoticeRVAdapter;
import com.zconnect.zutto.zconnect.addActivities.AddNotices;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.NoticeItemFormat;

import java.util.Vector;

public class Notices extends BaseActivity {
    DatabaseReference photoref;
    private RecyclerView gallery;
    private FirebaseAuth mAuth;
    Vector<NoticeItemFormat> photos = new Vector<>();
    private NoticeRVAdapter noticeRVAdapter;
    private ValueEventListener mListener;
    private FloatingActionButton add_photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notices);
        gallery=(RecyclerView)findViewById(R.id.photos);
        gallery.setLayoutManager(new GridLayoutManager(this, 3));
        photoref= FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("notices");
        add_photo=(FloatingActionButton)findViewById(R.id.photos);
        add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notices.this.startActivity(new Intent(Notices.this,AddNotices.class));
            }
        });
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NoticeItemFormat singleObject;
                photos.clear();
                for(DataSnapshot shot: dataSnapshot.getChildren()) {
                    try{
                        singleObject = new NoticeItemFormat();
                        singleObject = shot.getValue(NoticeItemFormat.class);


    //                    String name = dataSnapshot.child("name").getValue().toString();
    //                    Uri image = (Uri) dataSnapshot.child("image").getValue();
    //
    //
    //                    singleObject = new NoticeItemFormat(image, name);

                        if(singleObject.getName()!=null && singleObject.getImageurl()!=null) {
                            photos.add(singleObject);
                        }
                    }catch (Exception e){}
                }

                noticeRVAdapter.notifyDataSetChanged();
            };

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            };
        };
        noticeRVAdapter = new NoticeRVAdapter(photos,this);
        gallery.setAdapter(noticeRVAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        photoref.addValueEventListener(mListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        photoref.removeEventListener(mListener);
    }
}
