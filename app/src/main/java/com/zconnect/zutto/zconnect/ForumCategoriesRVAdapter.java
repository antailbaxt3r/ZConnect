package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.forumCategoriesItemFormat;

import java.util.Vector;

import static com.zconnect.zutto.zconnect.BaseActivity.communityReference;

/**
 * Created by shubhamk on 9/2/17.
 */

public class ForumCategoriesRVAdapter extends RecyclerView.Adapter<ForumCategoriesRVAdapter.ViewHolder> {

    Context context;
    Vector<forumCategoriesItemFormat> forumCategoriesItemFormats;
    String tabUID;

    public ForumCategoriesRVAdapter(Vector<forumCategoriesItemFormat> forumCategoriesItemFormats, Context context, String tabUID) {
        this.forumCategoriesItemFormats = forumCategoriesItemFormats;
        this.context = context;
        this.tabUID = tabUID;
    }

    @Override
    public ForumCategoriesRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.student_hostel_list_item_format, parent, false);

        // Return a new holder instance
        return new ForumCategoriesRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ForumCategoriesRVAdapter.ViewHolder holder, int position) {
        holder.catName.setText(forumCategoriesItemFormats.get(position).getName());
        if (position!=0) {
            holder.openChat(forumCategoriesItemFormats.get(position).getCatUID());
        }else {
            holder.createCategory(tabUID);
        }
    }

    @Override
    public int getItemCount() {
        return forumCategoriesItemFormats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView catName;
        View mView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            catName = (TextView) itemView.findViewById(R.id.cat_name);

            //changing fonts
            Typeface ralewayMedium = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
            catName.setTypeface(ralewayMedium);
        }

        void openChat(final String uid){
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("ref", FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories").child(uid).toString());
                    context.startActivity(intent);
                }
            });

        }

        void createCategory(final String uid){
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Enter Title");
                    final EditText input = new EditText(context);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

                    builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference tabName= FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabs").child(uid);
                            tabName.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    addCategory(input.getText().toString(),uid,dataSnapshot.toString());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });


                    builder.show();
                }
            });
        }

        public void addCategory(String catName,String uid,String tabName){

            DatabaseReference databaseReferenceCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("categories");
            DatabaseReference databaseReferenceTabsCategories = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(uid);
            final DatabaseReference databaseReferenceHome = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("home");

            final DatabaseReference newPush=databaseReferenceCategories.push();
            DatabaseReference mPostedByDetails = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("Users1").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            newPush.child("name").setValue(catName);
            Long postTimeMillis = System.currentTimeMillis();
            newPush.child("PostTimeMillis").setValue(postTimeMillis);
            newPush.child("UID").setValue(newPush.getKey());
            newPush.child("tab").setValue(uid);
            newPush.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

            databaseReferenceTabsCategories.child(newPush.getKey()).child("name").setValue(catName);
            databaseReferenceTabsCategories.child(newPush.getKey()).child("catUID").setValue(newPush.getKey());
            databaseReferenceTabsCategories.child(newPush.getKey()).child("tabUID").setValue(uid);


            //Home

            databaseReferenceHome.child(newPush.getKey()).child("feature").setValue("Forums");
            databaseReferenceHome.child(newPush.getKey()).child("name").setValue(catName);
            databaseReferenceHome.child(newPush.getKey()).child("id").setValue(uid);
            databaseReferenceHome.child(newPush.getKey()).child("desc").setValue(tabName);
            databaseReferenceHome.child(newPush.getKey()).child("Key").setValue(newPush.getKey());
            databaseReferenceHome.child(newPush.getKey()).child("PostTimeMillis").setValue(postTimeMillis);

            databaseReferenceHome.child(newPush.getKey()).child("PostedBy").child("UID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

            mPostedByDetails.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    newPush.child("PostedBy").child("Username").setValue(dataSnapshot.child("username").getValue().toString());
                    newPush.child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child("imageURLThumbnail").getValue().toString());

                    databaseReferenceHome.child(newPush.getKey()).child("PostedBy").child("Username").setValue(dataSnapshot.child("username").getValue().toString());
                    databaseReferenceHome.child(newPush.getKey()).child("PostedBy").child("ImageThumb").setValue(dataSnapshot.child("imageURLThumbnail").getValue().toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

}
