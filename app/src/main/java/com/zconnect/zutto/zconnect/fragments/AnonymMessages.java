package com.zconnect.zutto.zconnect.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.zconnect.zutto.zconnect.ItemFormats.AnonymousMessages;
import com.zconnect.zutto.zconnect.R;

public class AnonymMessages extends Fragment {
    private DatabaseReference queryRef;
    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth;
    public AnonymMessages() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_anonym_messages, container, false);
        LinearLayoutManager mlinearmanager;
        mlinearmanager = new LinearLayoutManager(getContext());
        mAuth = FirebaseAuth.getInstance();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.messageRecycler);
        mRecyclerView.setLayoutManager(mlinearmanager);
        queryRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Messages");
        queryRef.keepSynced(true);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<AnonymousMessages,AnonymousMessagesViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AnonymousMessages, AnonymousMessagesViewHolder>(
                AnonymousMessages.class,
                R.layout.anonymous_message_row,
                AnonymousMessagesViewHolder.class,
                queryRef) {
            @Override
            protected void populateViewHolder(AnonymousMessagesViewHolder viewHolder, AnonymousMessages model, int position) {
                viewHolder.setText(model.getMessage());
            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AnonymousMessagesViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public AnonymousMessagesViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setText(String message){
            TextView anonymMessage= (TextView) mView.findViewById(R.id.anonymMessage);
            anonymMessage.setText(message);
        }
    }

}
