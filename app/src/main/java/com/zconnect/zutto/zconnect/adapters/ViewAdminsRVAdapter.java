package com.zconnect.zutto.zconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ChatActivity;
import com.zconnect.zutto.zconnect.OpenUserDetail;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.ZConnectDetails;
import com.zconnect.zutto.zconnect.commonModules.BaseActivity;
import com.zconnect.zutto.zconnect.itemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.itemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.utilities.ForumsUserTypeUtilities;

import java.util.HashMap;
import java.util.Vector;


/**
 * Created by shubhamk on 26/7/17.
 */


public class ViewAdminsRVAdapter extends RecyclerView.Adapter<ViewAdminsRVAdapter.ViewHolder> {
    Context context;
    Vector<String> admname;
    Vector<String> admimg;
    Vector<String> adiminUID;
    String communityReference;

    public ViewAdminsRVAdapter(Context context, Vector<String> admimg, Vector<String> admname, Vector<String> adminUID, String communityReference) {
        this.context = context;
        this.admname = admname;
        this.admimg = admimg;
        this.adiminUID = adminUID;
        this.communityReference = communityReference;
    }


    @Override
    public ViewAdminsRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("oncreatevh", "onCreateViewHolder: ");
        LayoutInflater inflater = LayoutInflater.from(context);
        View adminView = inflater.inflate(R.layout.new_view_admins_item_format, parent, false);

        return new ViewAdminsRVAdapter.ViewHolder(adminView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            Uri uri = Uri.parse(admimg.get(position));
            holder.adminimage.setImageURI(uri);
            //holder.adminimage.setImageURI(uri);
            Log.d("img-set", "onBindViewHolder: ");
            holder.adminname.setText(admname.get(position));
            holder.adminimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), OpenUserDetail.class);
                    intent.putExtra("Uid",adiminUID.get(position));
                    context.startActivity(intent);
                }
            });
            holder.chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FirebaseAuth mAuth= FirebaseAuth.getInstance();

                    final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(ZConnectDetails.COMMUNITIES_DB)
                            .child(communityReference).child(ZConnectDetails.USERS_DB).child(mAuth.getCurrentUser().getUid());

                    if (databaseReferenceUser == null) {
                        Toast.makeText(v.getContext(), "The user does not exist!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (!dataSnapshot.child("userChats").hasChild(adiminUID.get(position))) {
                                final String userImageURL = dataSnapshot.child("imageURL").getValue().toString();
                                Log.d("Try", createPersonalChat(mAuth.getCurrentUser().getUid(), adiminUID.get(position),v.getContext()));
                            }else {
                                databaseReferenceUser.child("userChats").child(adiminUID.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String key = dataSnapshot.getValue().toString();
                                        Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
                                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
                                        intent.putExtra("type", "forums");
                                        intent.putExtra("name", admname.get(position));
                                        intent.putExtra("tab", "personalChats");
                                        intent.putExtra("key", key);
                                        holder.itemView.getContext().startActivity(intent);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }});
        }
        catch (Exception e) {
            Log.d("THOUSAND", admimg.get(position) + "-AAA");
        }
    }



    @Override
    public int getItemCount() {
        return admname.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView adminname;
        SimpleDraweeView adminimage;
        Button chatButton;

        public ViewHolder(View itemView) {
            super(itemView);
            adminname = (TextView) itemView.findViewById(R.id.admin_name);
            adminimage = (SimpleDraweeView) itemView.findViewById(R.id.admin_image);
            chatButton = itemView.findViewById(R.id.admin_chat_button);


        }
    }


    private String createPersonalChat(final String senderUID, final String receiverUserUUID, Context ctx) {
        final DatabaseReference databaseReferenceCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories");
        final DatabaseReference databaseReferenceTabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child("personalChats");

        final DatabaseReference databaseReferenceReceiver = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(receiverUserUUID);
        final DatabaseReference databaseReferenceSender = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(senderUID);

        final DatabaseReference newPush = databaseReferenceCategories.push();


        newPush.child("name").setValue(false);
        Long postTimeMillis = System.currentTimeMillis();
        newPush.child("PostTimeMillis").setValue(postTimeMillis);
        newPush.child("UID").setValue(newPush.getKey());
        newPush.child("tab").setValue("personalChats");
        newPush.child("Chat");


        databaseReferenceReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserItemFormat userItem = dataSnapshot.getValue(UserItemFormat.class);

                UsersListItemFormat userDetails = new UsersListItemFormat();

                userDetails.setImageThumb(userItem.getImageURLThumbnail());

                userDetails.setName(userItem.getUsername());
                userDetails.setPhonenumber(userItem.getMobileNumber());
                userDetails.setUserUID(userItem.getUserUID());
                userDetails.setUserType(ForumsUserTypeUtilities.KEY_ADMIN);


                HashMap<String,UsersListItemFormat> userList = new HashMap<String,UsersListItemFormat>();
                userList.put(receiverUserUUID,userDetails);

                databaseReferenceSender.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        UserItemFormat temp = dataSnapshot1.getValue(UserItemFormat.class);


                        UsersListItemFormat currentUser = new UsersListItemFormat();
                        currentUser.setImageThumb(temp.getImageURLThumbnail());
                        currentUser.setName(temp.getUsername());
                        currentUser.setPhonenumber(temp.getMobileNumber());
                        currentUser.setUserUID(temp.getUserUID());
                        currentUser.setUserType(temp.getUserType());
                        userList.put(senderUID,currentUser);
                        databaseReferenceTabsCategories.child(newPush.getKey()).child("users").setValue(userList);

                        HashMap<String,Object> forumTabs = new HashMap<>();
                        forumTabs.put("name",false);
                        forumTabs.put("catUID",newPush.getKey());
                        forumTabs.put("tabUID","personalChats");
                        forumTabs.put("lastMessage","Null");
                        forumTabs.put("users",userList);
                        databaseReferenceTabsCategories.child(newPush.getKey()).setValue(forumTabs);


                        databaseReferenceSender.child("userChats").child(receiverUserUUID).setValue(newPush.getKey());
                        databaseReferenceReceiver.child("userChats").child(senderUID).setValue(newPush.getKey());

                        String key = newPush.getKey();
                        Intent intent = new Intent(ctx, ChatActivity.class);
                        intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(key).toString());
                        intent.putExtra("type", "forums");
                        intent.putExtra("name", userDetails.getName());
                        intent.putExtra("tab", "personalChats");
                        intent.putExtra("key", key);
                        ctx.startActivity(intent);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });





        return newPush.getKey();

    }
}
