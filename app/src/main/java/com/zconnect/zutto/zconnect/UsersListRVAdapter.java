package com.zconnect.zutto.zconnect;

import android.content.Context;
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

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zconnect.zutto.zconnect.ItemFormats.UserItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.UsersListItemFormat;
import com.zconnect.zutto.zconnect.ItemFormats.PhonebookDisplayItem;

import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by shubhamk on 27/7/17.
 */

public class UsersListRVAdapter extends RecyclerView.Adapter<UsersListRVAdapter.ViewHolder> {
    Context context;
    Vector<UsersListItemFormat> usersListItemFormats;


    private SharedPreferences communitySP;
    public String communityReference;

    public UsersListRVAdapter(Context context, Vector<UsersListItemFormat> usersListItemFormats) {
        this.context = context;
        this.usersListItemFormats = usersListItemFormats;
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
            rv_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                String userUID = usersListItemFormats.get(getAdapterPosition()).getUserUID();
                intent=new Intent(context, OpenUserDetail.class);
                intent.putExtra("Uid",userUID);
                context.startActivity(intent);

                }
            });
        }
    }
}