package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.commonModules.GlobalFunctions;
import com.zconnect.zutto.zconnect.holders.newRequestViewHolder;
import com.zconnect.zutto.zconnect.itemFormats.NewRequestItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.utilities.RecentTypeUtilities;
import com.zconnect.zutto.zconnect.utilities.RequestTypeUtilities;

import java.util.Objects;
import java.util.Vector;

import static com.zconnect.zutto.zconnect.commonModules.BaseActivity.communityReference;

public class NewRequestRVAdapter extends RecyclerView.Adapter<newRequestViewHolder> {
    newRequestViewHolder holder;
    int position;
    private Context context;
    UserItemFormat userItemFormat=new UserItemFormat();
    private Vector<NewRequestItemFormat> newRequestItemFormats;

    public NewRequestRVAdapter(Context context, Vector<NewRequestItemFormat> newRequestItemFormats)
    {
        this.context = context;
        this.newRequestItemFormats = newRequestItemFormats;
    }

    @Override
    public newRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_new_request_admin_page, parent, false);

        return new newRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(newRequestViewHolder holder,final int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1");
        if (newRequestItemFormats.get(holder.getAdapterPosition()).getType().equals(RequestTypeUtilities.TYPE_CABPOOL_LOCATION))
            holder.newRequestName.setText("Requested Location name: "+newRequestItemFormats.get(position).getName());
        else if(newRequestItemFormats.get(holder.getAdapterPosition()).getType().equals(RequestTypeUtilities.TYPE_FORUM_TAB))
            holder.newRequestName.setText("Requested ForumTab name: "+newRequestItemFormats.get(position).getName());
        else if(newRequestItemFormats.get(holder.getAdapterPosition()).getType().equals(RequestTypeUtilities.TYPE_LINKS))
            holder.newRequestName.setText("Requested Link Title: "+newRequestItemFormats.get(position).getName()+"\n" + "Requested Link: "+newRequestItemFormats.get(position).getLink());
        else if (newRequestItemFormats.get(holder.getAdapterPosition()).getType().equals(RequestTypeUtilities.TYPE_INFONE_CAT))
            holder.newRequestName.setText("Requested Infone Category: "+newRequestItemFormats.get(position).getName());

        holder.postedByNameInLocation.setText(newRequestItemFormats.get(position).getPostedBy().getUsername());
        holder.postedByImageLocation.setImageURI(newRequestItemFormats.get(position).getPostedBy().getImageThumb());

        if (newRequestItemFormats.get(position).getType().equals(RequestTypeUtilities.TYPE_CABPOOL_LOCATION)) {
            Log.d(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), "onBindViewHolder: ");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot childsnap: dataSnapshot.getChildren()){
                        if(Objects.requireNonNull(childsnap.getKey()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            userItemFormat.setUserUID((String) childsnap.child("userUID").getValue());
                            userItemFormat.setUsername((String) childsnap.child("username").getValue());
                            userItemFormat.setImageURL((String) childsnap.child("imageURL").getValue());
                        }
                    }
                    holder.setAcceptDeclineButtonForLocations(newRequestItemFormats.get(holder.getAdapterPosition()).getKey(), newRequestItemFormats.get(holder.getAdapterPosition()).getPostedBy().getUID(),userItemFormat,holder.acceptUserButton,holder.declineUserButton);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else if(newRequestItemFormats.get(position).getType().equals(RequestTypeUtilities.TYPE_FORUM_TAB)) {
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot childsnap: dataSnapshot.getChildren()){
                        if(childsnap.getKey().equals(FirebaseAuth.getInstance().getUid())){
                            userItemFormat.setUserUID(FirebaseAuth.getInstance().getUid());
                            userItemFormat.setUsername((String) childsnap.child("username").getValue());
                            userItemFormat.setImageURL((String) childsnap.child("imageURL").getValue());
                        }
                    }
                    holder.setAcceptDeclineButtonForForumTabs(newRequestItemFormats.get(holder.getAdapterPosition()).getKey(), newRequestItemFormats.get(holder.getAdapterPosition()).getPostedBy().getUID(),userItemFormat,holder.acceptUserButton,holder.declineUserButton);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else if(newRequestItemFormats.get(position).getType().equals(RequestTypeUtilities.TYPE_LINKS)){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot childsnap: dataSnapshot.getChildren()){
                        if(childsnap.getKey().equals(FirebaseAuth.getInstance().getUid())){
                            userItemFormat.setUserUID(FirebaseAuth.getInstance().getUid());
                            userItemFormat.setUsername((String) childsnap.child("username").getValue());
                            userItemFormat.setImageURL((String) childsnap.child("imageURL").getValue());
                        }
                    }
                    holder.setAcceptDeclineButtonForLinks(newRequestItemFormats.get(holder.getAdapterPosition()).getKey(), newRequestItemFormats.get(holder.getAdapterPosition()).getPostedBy().getUID(),userItemFormat,holder.acceptUserButton,holder.declineUserButton);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else if(newRequestItemFormats.get(position).getType().equals(RequestTypeUtilities.TYPE_INFONE_CAT)){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot childsnap: dataSnapshot.getChildren()){
                        if(childsnap.getKey().equals(FirebaseAuth.getInstance().getUid())){
                            userItemFormat.setUserUID(FirebaseAuth.getInstance().getUid());
                            userItemFormat.setUsername((String) childsnap.child("username").getValue());
                            userItemFormat.setImageURL((String) childsnap.child("imageURL").getValue());
                        }
                    }
                    holder.setAcceptDeclineButtonForInfoneCat(newRequestItemFormats.get(holder.getAdapterPosition()).getKey(), newRequestItemFormats.get(holder.getAdapterPosition()).getPostedBy().getUID(),userItemFormat,holder.acceptUserButton,holder.declineUserButton);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return newRequestItemFormats.size();
    }
}
