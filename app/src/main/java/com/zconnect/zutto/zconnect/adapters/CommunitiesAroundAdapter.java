package com.zconnect.zutto.zconnect.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.zconnect.zutto.zconnect.HomeActivity;
import com.zconnect.zutto.zconnect.R;
import com.zconnect.zutto.zconnect.itemFormats.CommunitiesItemFormat;

import java.util.Vector;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Lokesh Garg on 10-02-2018.
 */

public class CommunitiesAroundAdapter extends RecyclerView.Adapter<CommunitiesAroundAdapter.communitiesViewHolder>{
    Context context;
    Vector<CommunitiesItemFormat> allCommunities;

    public CommunitiesAroundAdapter(Context context, Vector<CommunitiesItemFormat> allCommunities){
        this.context = context;
        this.allCommunities=allCommunities;
    }

    @Override
    public communitiesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.community_row, parent, false);

        // Return a new holder instance
        return new communitiesViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(communitiesViewHolder holder, int position) {

        holder.communityName.setText(allCommunities.get(position).getName());
        holder.communitySize.setText("32 people");
//        Picasso.with(context).load(allCommunities.get(position).getImage()).into(holder.communityImage);
        holder.communityImage.setImageURI(Uri.parse(allCommunities.get(position).getImage()));
        holder.enterCommunity(allCommunities.get(position).getCode());

    }

    @Override
    public int getItemCount() {
        return allCommunities.size();
    }

    class communitiesViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView communityName,communitySize;
        SimpleDraweeView communityImage;
        Button communityGuestLogin;

        public communitiesViewHolder(View itemView){
            super(itemView);
            mView = itemView;
            communityName = (TextView) itemView.findViewById(R.id.community_name);
            communitySize = (TextView) itemView.findViewById(R.id.community_size);
            communityImage = (SimpleDraweeView) itemView.findViewById(R.id.community_image);
            communityGuestLogin=(Button) itemView.findViewById(R.id.guest_login_button);
        }

        public void enterCommunity(final String communityCode){

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPref2 = context.getSharedPreferences("communityName", MODE_PRIVATE);
                    SharedPreferences.Editor editInfo2 = sharedPref2.edit();
                    editInfo2.putString("communityReference", communityCode);
                    Toast.makeText(itemView.getContext(), communityCode +" community code shared preferance ", Toast.LENGTH_SHORT).show();
                    editInfo2.commit();
                    Intent i = new Intent(context,HomeActivity.class);
                    context.startActivity(i);
                    ((Activity)context).finish();

                }
            });

        }
    }
}
