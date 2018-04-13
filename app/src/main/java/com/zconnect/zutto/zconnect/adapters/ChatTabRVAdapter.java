package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zconnect.zutto.zconnect.ChatActivity2;
import com.zconnect.zutto.zconnect.ItemFormats.ChatTabRVItem;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.holders.ChatTabRVViewHolder;

import java.util.ArrayList;

/**
 * Created by tanmay on 25/3/18.
 */

public class ChatTabRVAdapter extends RecyclerView.Adapter<ChatTabRVViewHolder> {

    Context context;
    ArrayList<ChatTabRVItem> chatTabRVItems;

    public ChatTabRVAdapter(Context context, ArrayList<ChatTabRVItem> chatTabRVItems) {
        this.context = context;
        this.chatTabRVItems = chatTabRVItems;
    }

    @Override
    public ChatTabRVViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_chat_message, parent, false);

        return new ChatTabRVViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatTabRVViewHolder holder, final int position) {

        holder.nametv.setText(chatTabRVItems.get(position).getName().substring(28,chatTabRVItems.get(position).getName().length()));
        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recid=chatTabRVItems.get(position).getName().substring(0,28);
                DatabaseReference databaseReference;
                SharedPreferences communitySP;
                String communityReference;
                communitySP = context.getSharedPreferences("communityName", Context.MODE_PRIVATE);
                communityReference = communitySP.getString("communityReference", null);
                databaseReference= FirebaseDatabase.getInstance().getReference();
                FirebaseUser mauth = FirebaseAuth.getInstance().getCurrentUser();
                String myuid=mauth.getUid();
                databaseReference.child("communities").child(communityReference).child("features").child("messages").child("users").child(myuid).child(recid).removeValue();
            }
        });
        //notifyDataSetChanged();
        //Log.e("adsfda",chatTabRVItems.get(position).getName());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ChatItemFormats cif=new ChatItemFormats();
                /*
                SharedPreferences communitySP;
                final String communityReference;
                communitySP = context.getSharedPreferences("communityName", MODE_PRIVATE);
                communityReference = communitySP.getString("communityReference", null);
                final DatabaseReference databaseReference;
                databaseReference= FirebaseDatabase.getInstance().getReference();
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String recpuid="blah";
                        String tempkey;
                        for (DataSnapshot childsnapShot :
                                dataSnapshot.child("communities").child(communityReference).child("users").getChildren())
                        {
                            tempkey=dataSnapshot.getKey();
                            if((dataSnapshot.child(tempkey).child("Username").getValue().toString()).equals(chatTabRVItems.get(position).getName()))
                            {
                                recpuid = tempkey;
                            }
                        }
                        Toast.makeText(context,recpuid, Toast.LENGTH_SHORT).show();
                        /*Intent i = new Intent(context, ChatActivity2.class);
                        i.putExtra("s",recpuid);
                        context.startActivity(i);*/
                        /*for (DataSnapshot childsnapShot :
                                dataSnapshot.child("communities").child(communityReference).child("features").child("messages").child("users").child(myuid).getChildren())
                        {
                            dr=databaseReference.child("communities").child(communityReference).child("features").child("messages").child("users").child(myuid).child(recpuid);
                            Intent i = new Intent(context, ChatActivity2.class);
                            i.putExtra("dataref",dr);
                            //i.putExtra("dataref",dr);
                            context.startActivity(i);

                        }*//*
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/
                Intent i = new Intent(context, ChatActivity2.class);
                i.putExtra("s",chatTabRVItems.get(position).getName());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatTabRVItems.size();
    }

}
