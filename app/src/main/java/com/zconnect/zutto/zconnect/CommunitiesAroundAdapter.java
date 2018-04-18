package com.zconnect.zutto.zconnect;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;
import com.zconnect.zutto.zconnect.ItemFormats.CommunitiesItemFormat;

import java.util.ArrayList;
import java.util.Vector;

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

    }

    @Override
    public int getItemCount() {
        return allCommunities.size();
    }

    class communitiesViewHolder extends RecyclerView.ViewHolder {

        TextView communityName,communitySize;
        SimpleDraweeView communityImage;
        Button communityGuestLogin;

        public communitiesViewHolder(View itemView){
            super(itemView);
            communityName = (TextView) itemView.findViewById(R.id.community_name);
            communitySize = (TextView) itemView.findViewById(R.id.community_size);
            communityImage = (SimpleDraweeView) itemView.findViewById(R.id.community_image);
            communityGuestLogin=(Button) itemView.findViewById(R.id.guest_login_button);
        }
    }
}
