package com.zconnect.zutto.zconnect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;
import com.zconnect.zutto.zconnect.Utilities.FeatureNamesUtilities;
import com.zconnect.zutto.zconnect.Utilities.ForumsUserTypeUtilities;

import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by shubhamk on 27/7/17.
 */

public class UsersListRVAdapter extends RecyclerView.Adapter<UsersListRVAdapter.ViewHolder> {
    Context context;
    Vector<UsersListItemFormat> usersListItemFormats;
    String featureType,userType,tab,key;

    private SharedPreferences communitySP;
    public String communityReference;

    public UsersListRVAdapter(Context context, Vector<UsersListItemFormat> usersListItemFormats, String featureType,String userType) {
        this.context = context;
        this.usersListItemFormats = usersListItemFormats;
        this.featureType = featureType;
        this.userType = userType;
    }

    public UsersListRVAdapter(Context context, Vector<UsersListItemFormat> usersListItemFormats, String featureType,String userType,String tab,String key) {
        this.context = context;
        this.usersListItemFormats = usersListItemFormats;
        this.featureType = featureType;
        this.userType = userType;
        this.tab = tab;
        this.key = key;
    }

    @Override
    public UsersListRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.cab_people_item_format, parent, false);
        return new UsersListRVAdapter.ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(UsersListRVAdapter.ViewHolder holder, int position) {
        holder.name.setText(usersListItemFormats.get(position).getName());
        holder.number.setText(usersListItemFormats.get(position).getPhonenumber());
        holder.avatarCircle.setImageURI(usersListItemFormats.get(position).getImageThumb());
        holder.openOptions(featureType, userType);

    }

    @Override
    public int getItemCount() {
        return usersListItemFormats.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;
        ImageView call;
        View rv_item;
        Intent intent;
        SimpleDraweeView avatarCircle;

        public ViewHolder(View itemView) {
            super(itemView);

            communitySP = context.getSharedPreferences("communityName", MODE_PRIVATE);
            communityReference = communitySP.getString("communityReference", null);

            call = (ImageView) itemView.findViewById(R.id.ib_call_contact_item);
            name = (TextView) itemView.findViewById(R.id.cab_name);
            number = (TextView) itemView.findViewById(R.id.cab_number);
            avatarCircle = (SimpleDraweeView) itemView.findViewById(R.id.cab_people_avatarCircle);

            rv_item=itemView.findViewById(R.id.rv_item);
            Typeface customFont = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Medium.ttf");
            name.setTypeface(customFont);

            Typeface customFont2 = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/Raleway-Light.ttf");
            number.setTypeface(customFont2);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_DIAL,
                            Uri.parse("tel:" + Long.parseLong(number.getText().toString().trim()))).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
            });

        }

        public void openOptions(String featureType, final String forumUserType){

            if(featureType.equals(FeatureNamesUtilities.KEY_CABPOOL) || featureType.equals(FeatureNamesUtilities.KEY_STOREROOM)){

                String userUID = usersListItemFormats.get(getAdapterPosition()).getUserUID();
                intent=new Intent(context, OpenUserDetail.class);
                intent.putExtra("Uid",userUID);
                context.startActivity(intent);

            }else if(featureType.equals(FeatureNamesUtilities.KEY_FORUMS)){
                rv_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(forumUserType.equals(ForumsUserTypeUtilities.KEY_USER)){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            String[] animals = {"View Profile"};
                            builder.setItems(animals, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: String userUID = usersListItemFormats.get(getAdapterPosition()).getUserUID();
                                            intent=new Intent(context, OpenUserDetail.class);
                                            intent.putExtra("Uid",userUID);
                                            context.startActivity(intent);
                                            break;
                                    }
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }else if(forumUserType.equals(ForumsUserTypeUtilities.KEY_ADMIN)){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            String[] animals = {"View Profile","Make Admin","Remove","Block " +usersListItemFormats.get(getAdapterPosition()).getName()};
                            builder.setItems(animals, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("communities").child(communityReference).child("features").child("forums").child("tabsCategories").child(tab).child(key).child("users").child(usersListItemFormats.get(getAdapterPosition()).getUserUID());
                                    switch (which) {
                                        case 0: String userUID = usersListItemFormats.get(getAdapterPosition()).getUserUID();
                                            intent=new Intent(context, OpenUserDetail.class);
                                            intent.putExtra("Uid",userUID);
                                            context.startActivity(intent);
                                            break;
                                        case 1: userReference.child("userType").setValue(ForumsUserTypeUtilities.KEY_ADMIN);
                                            break;
                                        case 2: userReference.removeValue();
                                            Toast.makeText(context, usersListItemFormats.get(getAdapterPosition()).getName() + " is removed from ", Toast.LENGTH_SHORT).show();
                                            break;
                                        case 3: userReference.child("userType").setValue(ForumsUserTypeUtilities.KEY_BLOCKED);
                                            break;
                                    }
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }else {
                            String userUID = usersListItemFormats.get(getAdapterPosition()).getUserUID();
                            intent=new Intent(context, OpenUserDetail.class);
                            intent.putExtra("Uid",userUID);
                            context.startActivity(intent);
                        }
                    }
                });

            }

        }
    }
}
